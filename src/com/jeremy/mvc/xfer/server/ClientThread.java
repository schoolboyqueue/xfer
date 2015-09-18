/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer.server;

import com.jeremy.mvc.xfer.Message;
import com.jeremy.mvc.xfer.CryptoSession;
import com.jeremy.mvc.xfer.FileHandler;
import com.jeremy.mvc.xfer.model.Model;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Jeremy
 */
public class ClientThread implements Runnable {
    private String directory;
    private CryptoSession cs = new CryptoSession();
    private ArrayList<FileHandler> recieveFileHandlers;
    private ArrayList<FileHandler> sendFileHandlers;
    private volatile boolean keepGoing = true;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Model model;
    private  String ip;
    private Message message;
    private Message reply;
    private Object[] data;

    public ClientThread(Socket socket, Model model, String directory) {
        ip = socket.getInetAddress().toString();
        this.model = model;
        this.socket = socket;
        this.directory = directory;
        recieveFileHandlers = new ArrayList<>();
        setupStreams();
    }
    public ClientThread(String ip, Model model) {
        try {
            socket = new Socket(ip, 33600);
            this.ip = ip;
            this.model = model;
            sendFileHandlers = new ArrayList<>();
            setupStreams();
            sendConnect();
        } catch (IOException ex) {
            keepGoing = false;
        }
    }
    @Override
    public void run() {
        while(keepGoing) {
            try {
                message = (Message) ois.readObject();
                data = message.getData();
                switch (message.getType()) {
                    case Message.CONNECT:
                        receiveConnect();
                        sendInitialInfo();
                        break;
                    case Message.DISCONNECT:
                        keepGoing = false;
                        break;
                    case Message.FILE_LIST:
                        receiveFileList();
                        break;
                    case Message.FILE_PART:
                        receiveFilePart();
                        break;
                    case Message.INITIAL_INFO:
                        receiveInitialInfo();
                        sendFileList();
                        break;
                    case Message.ACK: {
                        switch(message.getPreviousType()) {
                            case Message.INITIAL_INFO:
                                cs.setSessionkey((byte[]) data[0]);
                                break;
                            case Message.FILE_LIST:
                                setupSendFileList();
                                sendFilePart();
                                break;
                            case Message.FILE_PART:
                                sendFilePart();
                                break;
                        }
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                if (recieveFileHandlers != null) cleanRecieveList(true);
                if (sendFileHandlers != null) cleanSendList(true);
                keepGoing = false;                
            }
        }
        close();
    }
    private void send(Message message) {
        if(!socket.isConnected()) {
            close();
        }
        try {
            oos.writeObject(message);
            oos.flush();
            oos.reset();
        } catch (IOException ex) {
            close();
        }
    }
    private void close() {
        try {
            if(oos != null) oos.close();
            if(ois != null) ois.close();
            if(socket != null) socket.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    private void setupStreams() {
        try {
            oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            oos.flush();
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException ex) {
            keepGoing = false;
        }
    }
    private void sendConnect() {
        reply = new Message(Message.CONNECT);
        send(reply);
    }
    private void receiveConnect() {
        reply = new Message(Message.ACK);
        reply.setPreviousType(Message.CONNECT);
        send(reply);
    }
    private void receiveFileList() {
        Object[][] files = (Object[][]) data[0];
        for (Object[] file : files) {
            FileHandler fh = new FileHandler((String) file[0], directory, (long) file[1]);
            recieveFileHandlers.add(fh);
        }
        model.addToServerTable(files, ip);
        reply = new Message(Message.ACK);
        reply.setPreviousType(Message.FILE_LIST);
        send(reply);   
    }
    private void receiveInitialInfo() {
        cs.setRecievedIV((byte[]) data[0]);
        cs.setRecievedPublicKey((byte[]) data[1]);
        reply = new Message(Message.ACK);
        reply.setPreviousType(Message.INITIAL_INFO);
        reply.setSessionKey(cs.getEncryptedSessionKey());
        send(reply);
    }
    private void receiveFilePart() {
        String name = (String) data[0];
        byte[] block = (byte[]) data[1];
        for (FileHandler fh : recieveFileHandlers) {
            if (fh.name.equals(name)) {
                fh.writeBlock(cs.decrypt(block));
                model.updateServerTableProgress(ip, name, (int)((float)fh.totalBytesRecieved / (float)fh.totalSize * 100));
                reply = new Message(Message.ACK);
                reply.setPreviousType(Message.FILE_PART);
                send(reply);
            }
        }
        cleanRecieveList(false);
    }
    public void cleanRecieveList(boolean disconnect) {
        for (Iterator<FileHandler> it = recieveFileHandlers.iterator(); it.hasNext();) {
            FileHandler fh = it.next();
            if (fh.done) { 
                it.remove();
            } else if (disconnect && !fh.done) {
                model.updateServerTableProgress(ip, fh.name, -1);
                it.remove();
            }
        }
    }
    private void sendInitialInfo() {
        reply = new Message(Message.INITIAL_INFO);
        cs.genKeys();
        reply.setPublicKey(cs.getPublicKey());
        reply.setIv(cs.getIv());
        send(reply);
    }
    private void sendFileList() {
        reply = new Message(Message.FILE_LIST);
        reply.setFileList(model.getChosenFiles());
        send(reply);
    }
    private void setupSendFileList() {
        Object[][] files = model.getChosenFiles();
        model.clearChosenTable();
        for (Object[] file : files) {
            FileHandler fh = new FileHandler((String) file[0], (String) file[2]);
            sendFileHandlers.add(fh);
        }
    }
    private void sendFilePart() {
        if (!sendFileHandlers.isEmpty()) {
            int number = (int)(Math.random() * sendFileHandlers.size());
            FileHandler current = sendFileHandlers.get(number);
            byte[] block = current.readBlock();
            if (current.done) {
                cleanSendList(false);
            } else {
                reply = new Message(Message.FILE_PART);
                reply.setFileName(current.name);
                reply.setFileChunk(cs.encrypt(block));
                model.updateSendingTableProgress(ip, current.name, (int)((float)current.totalBytesRead / (float)current.totalSize * 100));
                send(reply);
            }
        }
    }
    private void cleanSendList(boolean disconnect) {
        for (Iterator<FileHandler> it = sendFileHandlers.iterator(); it.hasNext();) {
            FileHandler fh = it.next();
            if (fh.done) {
                it.remove();
            } else if (disconnect && !fh.done) {
                model.updateSendingTableProgress(ip, fh.name, -1);
                it.remove();
            }
        }
        if (sendFileHandlers.isEmpty()) {
            reply = new Message(Message.DISCONNECT);
            send(reply);
            keepGoing = false;
        } else {
            sendFilePart();
        }
    }
}
