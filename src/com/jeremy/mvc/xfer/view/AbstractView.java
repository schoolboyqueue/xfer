/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer.view;

import java.beans.PropertyChangeEvent;
import javax.swing.JFrame;

/**
 *
 * @author Jeremy
 */
public abstract class AbstractView extends JFrame {
    
    public abstract void modelPropertyChange(PropertyChangeEvent evt);
}