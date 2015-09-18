/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer;

import java.io.IOException;
import com.jeremy.mvc.xfer.controller.Controller;
import com.jeremy.mvc.xfer.model.Model;
import com.jeremy.mvc.xfer.server.Server;
import com.jeremy.mvc.xfer.view.View;

/**
 *
 * @author Jeremy
 */
public class Xfer {
    
    public Xfer() throws IOException {
        
        Model       model       = new Model();
        Controller  controller  = new Controller();
        View        view        = new View(controller);
        
        Server.INSTANCE.setModel(model);
        controller.addView(view);
        controller.addModel(model);
        model.init();

    }
    public static void main(String[] args) throws IOException {
        Xfer xfer = new Xfer();
    }
}
