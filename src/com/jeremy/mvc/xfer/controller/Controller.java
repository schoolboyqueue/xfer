/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer.controller;

import com.jeremy.mvc.xfer.server.ClientThread;
import com.jeremy.mvc.xfer.server.Server;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Jeremy
 */
public class Controller extends AbstractController {
    public static final String CHOSEN_TABLE_MODEL = "setChosenFilesModel";
    public static final String SERVER_TABLE_MODEL = "setServerTableModel";
    public static final String SENDING_TABLE_MODEL = "setSendingTableModel";
    public static final String ADD_FILES_BEING_SENT = "addToChosenFiles";
    public static final String UPDATE_SENDING_TABLE = "updateSendingTable";
    public static final String REMOVE_FILES_BEING_SENT = "removeFromChosenFiles";
    
    public void addToChosenFiles(File[] files) {
        setModelProperty(ADD_FILES_BEING_SENT, files);
    }
    public void removeFromChosenFiles(int row) {
        setModelProperty(REMOVE_FILES_BEING_SENT, row);
    }
    public void startServer(String directory) {
        Server.INSTANCE.setSaveDir(directory);
        new Thread(Server.INSTANCE).start();
    }
    public void stopServer() throws IOException {
        Server.INSTANCE.stop();
    }
    public void newConnection(String ip) {
        new Thread(new ClientThread(ip, Server.INSTANCE.getModel())).start();
        setModelProperty(UPDATE_SENDING_TABLE, ip);
    }
}