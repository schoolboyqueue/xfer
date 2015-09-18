/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer.view;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jeremy
 */
public class ProgressRenderer extends DefaultTableCellRenderer {
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    public ProgressRenderer() {
        super();
        setOpaque(true);
        progressBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Integer i = (Integer) value;
        String text = "Completed";
        if (i < 0) {
            text = "Error";
        } else if (i < 100) {
            progressBar.setValue(i);
            return progressBar;
        }
        super.getTableCellRendererComponent(table, text, true, hasFocus, row, column);
        return this;
    }
}
