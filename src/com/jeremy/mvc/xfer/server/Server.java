/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.jeremy.mvc.xfer.model.Model;

/**
 *
 * @author Jeremy
 */
public enum Server implements Runnable {
    INSTANCE;
    private volatile boolean keepGoing;
    private ServerSocket server;
    private String directory;
    private Model model;
    
    private Server() {
        try {
            server = new ServerSocket(33600);
            keepGoing = true;
        } catch (IOException ex) {
            keepGoing = false;
        }
    }
    public void setModel(Model model) {
        this.model = model;
    }
    public Model getModel() {
        return model;
    }
    public String getDirectory() {
        return directory;
    }
    public void setSaveDir(String directory) {
        this.directory = directory;
    }
    public void stop() throws IOException {
        server.close();
        keepGoing = false;
    }
    @Override
    public void run() {
       while (keepGoing) {
           try {
               Socket socket = server.accept();
               new Thread(new ClientThread(socket, model, directory)).start();
           } catch (IOException ex) {
               System.out.println(ex);
           }
        }
    }
}
