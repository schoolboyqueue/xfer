/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeremy
 */
public class FileHandler {
    public String name;
    private int count;
    private FileOutputStream fos;
    private FileInputStream fis;
    public long totalSize,
            totalBytesRecieved,
            totalBytesRead;
    public boolean done = false;
    private byte[] fileBuffer;
    private ByteArrayOutputStream baos;
    private ByteArrayInputStream bais;
    
    public FileHandler(String name, String directory, long size) {
        this.name = name;
        totalSize = size;
        totalBytesRecieved = 0;
        fileBuffer = new byte[8192];
        try {
            fos = new FileOutputStream(new File(directory + "/" + name));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public FileHandler(String name, String directory) {
        this.name = name;
        File file = new File(directory + "/" + name);
        totalSize = file.length();
        totalBytesRead = 0;
        fileBuffer = new byte[8192];
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
        } catch (FileNotFoundException ex) {
            done = true;
        }
    }
    public byte[] readBlock() {
        try {
            if ((count = fis.read(fileBuffer)) != -1) {
                totalBytesRead += count;
                baos.reset();
                baos.write(fileBuffer, 0, count);
            } else {
                fis.close();
                done = true;
            }
        } catch (IOException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return baos.toByteArray();
    }
    public void writeBlock(byte[] block) {
        try {
            bais = new ByteArrayInputStream(block);
            count = bais.read(fileBuffer);
            totalBytesRecieved += count;
            fos.write(fileBuffer, 0, count);
            if (totalBytesRecieved == totalSize) {
                fos.close();
                done = true;
            }
        } catch (IOException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

