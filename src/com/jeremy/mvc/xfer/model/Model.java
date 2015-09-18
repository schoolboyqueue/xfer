/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer.model;

import java.io.File;
import javax.swing.table.DefaultTableModel;
import com.jeremy.mvc.xfer.controller.Controller;

/**
 *
 * @author Jeremy
 */
public class Model extends AbstractModel {
    private DefaultTableModel chosenFilesTableModel;
    private DefaultTableModel serverTableModel;
    private DefaultTableModel sendingTableModel;
    
    public void init() {
        setChosenFilesModel();
        setServerTableModel();
        setSendingTableModel();
    }
    
    public void setChosenFilesModel() {
        String[] chosenFilesTableModelColumnNames = {"File Name","Size","Directory"};
        chosenFilesTableModel = new DefaultTableModel(chosenFilesTableModelColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        firePropertyChange(Controller.CHOSEN_TABLE_MODEL, null, chosenFilesTableModel);
    }
    
    public void setServerTableModel() {
        String[] serverTableModelColumnNames = {"Client","File","Size","Progress"};
        serverTableModel = new DefaultTableModel(serverTableModelColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        firePropertyChange(Controller.SERVER_TABLE_MODEL, null, serverTableModel);
    }
    public void setSendingTableModel() {
        String[] sendingTableModelColumnNames = {"Server","File","Size","Progress"};
        sendingTableModel = new DefaultTableModel(sendingTableModelColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        firePropertyChange(Controller.SENDING_TABLE_MODEL, null, sendingTableModel);
    }
    
    public void addToChosenFiles(File[] files) {
        for (File f : files) {
            if (containsFile(chosenFilesTableModel, f.getName())) continue;
            Object[] row = {f.getName(), humanReadableByteCount(f.length(), true), f.getParentFile().getAbsolutePath()};
            chosenFilesTableModel.addRow(row);
        }
    }
    
    public void removeFromChosenFiles(Integer row) {
        chosenFilesTableModel.removeRow(row);
    }
    public Object[][] getChosenFiles() {
        int rows = chosenFilesTableModel.getRowCount();
        Object[][] data = new Object[rows][3];
        for (int i = 0; i < rows; i++) {
            String file = chosenFilesTableModel.getValueAt(i, 0).toString();
            String directory = chosenFilesTableModel.getValueAt(i, 2).toString();
            File f = new File(directory + "/" + file);
            data[i][0] = file;
            data[i][1] = f.length();
            data[i][2] = directory;
        }
        return data;
    }
    public void addToServerTable(Object[][] data, String ip) {
        for (Object[] obj : data) {
            Object[] row = {ip, (String) obj[0], humanReadableByteCount((long) obj[1], true), 0};
            serverTableModel.addRow(row);
        }
    }
    public void removeFromServerTable(String ip, String name) {
        for (int i = 0; i < serverTableModel.getRowCount(); i++) {
            if (serverTableModel.getValueAt(i, 0).equals(ip)) {
                if (serverTableModel.getValueAt(i, 1).equals(name)) serverTableModel.removeRow(i);
            }
        }
    }
    public void updateServerTableProgress(String ip, String name, Object value) {
        for (int i = 0; i < serverTableModel.getRowCount(); i++) {
            if (serverTableModel.getValueAt(i, 0).equals(ip) && !serverTableModel.getValueAt(i, 3).equals("complete")) {
                if (serverTableModel.getValueAt(i, 1).equals(name)) serverTableModel.setValueAt(value, i, 3);
            }
        }
    }
    public void clearChosenTable() {
        chosenFilesTableModel.setRowCount(0);
    }
    public void updateSendingTable(String ip) {
        Object[][] data = getChosenFiles();
        for (Object[] obj : data) {
            Object[] row = {ip, obj[0], humanReadableByteCount((long) obj[1], true), 0};
            sendingTableModel.addRow(row);
        }
        firePropertyChange(Controller.UPDATE_SENDING_TABLE, null, ip);
    }
    public void removeFromSendingTable(String ip, String name) {
        for (int i = 0; i < sendingTableModel.getRowCount(); i++) {
            if (sendingTableModel.getValueAt(i, 0).equals(ip)) {
                if (sendingTableModel.getValueAt(i, 1).equals(name)) sendingTableModel.removeRow(i);
            }
        }
    }
    public void updateSendingTableProgress(String ip, String name, Object value) {
        for (int i = 0; i < sendingTableModel.getRowCount(); i++) {
            if (sendingTableModel.getValueAt(i, 0).equals(ip) && !sendingTableModel.getValueAt(i, 3).equals("complete")) {
                if (sendingTableModel.getValueAt(i, 1).equals(name)) sendingTableModel.setValueAt(value, i, 3);
            }
        }
    }
     /**
     * Convenience Methods
     */
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
    private boolean containsFile(DefaultTableModel model, String name) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (name.equals(model.getValueAt(i, 0))) return true;
        }
        return false;
    }
}