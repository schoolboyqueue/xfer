/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer;

import java.io.Serializable;

/**
 *
 * @author Jeremy
 */
public class Message implements Serializable {
    public static final int 
            CONNECT = 0, 
            ACK = 1,
            NACK = 2,
            DISCONNECT = 3, 
            FILE_LIST = 4, 
            INITIAL_INFO = 5, 
            FILE_PART = 6;
    private int type;
    private int previousType;
    private Object[] data;
    
    public Message(int type) {
        this.type = type;
        data = new Object[2];
    }
    public int getType() {
        return type;
    }
    public void setPreviousType(int previousType) {
        this.previousType = previousType;
    }
    public int getPreviousType() {
        return previousType;
    }
    public void setIv(byte[] iv) {
        data[0] = iv;
    }
    public void setSessionKey(byte[] key) {
        data[0] = key;
    }
    public void setPublicKey(byte[] key) {
        data[1] = key;
    }
    public void setFileList(Object[][] files) {
        data[0] = files;
    }
    public void setFileName(String name) {
        data[0] = name;
    }
    public void setFileChunk(byte[] data) {
        this.data[1] = data;
    }
    public Object[] getData() {
        return data;
    }
}
