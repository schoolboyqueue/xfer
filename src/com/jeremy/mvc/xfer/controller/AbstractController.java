/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeremy.mvc.xfer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import com.jeremy.mvc.xfer.model.AbstractModel;
import com.jeremy.mvc.xfer.view.AbstractView;

/**
 *
 * @author Jeremy
 */
public abstract class AbstractController implements PropertyChangeListener {

    private ArrayList<AbstractView> registeredViews;
    private ArrayList<AbstractModel> registeredModels;

    public AbstractController() {
        registeredViews = new ArrayList<>();
        registeredModels = new ArrayList<>();
    }

    public void addModel(AbstractModel model) {
        registeredModels.add(model);
        model.addPropertyChangeListener(this);
    }

    public void removeModel(AbstractModel model) {
        registeredModels.remove(model);
        model.removePropertyChangeListener(this);
    }

    public void addView(AbstractView view) {
        registeredViews.add(view);
    }

    public void removeView(AbstractView view) {
        registeredViews.remove(view);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        for (AbstractView view : registeredViews) {
            view.modelPropertyChange(evt);
        }
    }
    
    protected void setModelProperty(String propertyName, Object newValue) {
        for (AbstractModel model : registeredModels) {
            try {
                Method method = model.getClass().getMethod(propertyName, new Class[] {newValue.getClass()});
                method.invoke(model, newValue);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}