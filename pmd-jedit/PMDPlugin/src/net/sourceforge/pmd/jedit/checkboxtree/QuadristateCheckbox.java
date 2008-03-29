/*
 * Copyright 2006,2007 Enrico Boldrini, Lorenzo Bigagli This file is part of
 * CheckboxTree. CheckboxTree is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. CheckboxTree is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with CheckboxTree; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 */
package net.sourceforge.pmd.jedit.checkboxtree;

import net.sourceforge.pmd.jedit.checkboxtree.QuadristateButtonModel.State;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;

/**
 * Checkbox with four states. Available states are UNCHECKED, CHECKED,
 * GREY_CHECKED, GREY_UNCHECKED. The rendering is obtained via a visualization
 * hack. The checkbox exploits the different rendering (greyed) of checkbox
 * pressed, thus the press, arm, rollover events are not available. Maintenance
 * tip - There were some tricks to getting this code working: 1. You have to
 * overwite addMouseListener() to do nothing 2. You have to add a mouse event on
 * mousePressed by calling super.addMouseListener() 3. You have to replace the
 * UIActionMap for the keyboard event "pressed" with your own one. 4. You have
 * to remove the UIActionMap for the keyboard event "released". 5. You have to
 * grab focus when the next state is entered, otherwise clicking on the
 * component won't get the focus.
 *
 * @author boldrini
 * @author bigagli
 */

public class QuadristateCheckbox extends JCheckBox {

    public QuadristateCheckbox(String text, Icon icon, State state) {
    super(text, icon);
    // Add a listener for when the mouse is pressed
    super.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
        grabFocus();
        getModel().nextState();
        }
    });
    // Reset the keyboard action map
    ActionMap map = new ActionMapUIResource();
    map.put("pressed", new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
        grabFocus();
        getModel().nextState();
        }
    });
    map.put("released", null);
    SwingUtilities.replaceUIActionMap(this, map);
    setState(state);
    }

    public QuadristateCheckbox(String text, State initial) {
    this(text, null, initial);
    }

    public QuadristateCheckbox(String text) {
    this(text, State.UNCHECKED);
    }

    public QuadristateCheckbox() {
    this(null);
    }

    @Override
    protected void init(String text, Icon icon) {
    // substitutes the underlying checkbox model:
    // if we had call setModel an exception would be raised
    // because setModel calls a getModel that return a
    // QuadristateButtonModel, but at this point we
    // have a JToggleButtonModel
    this.model = new QuadristateButtonModel();
    super.setModel(this.model); // side effect: set listeners
    super.init(text, icon);
    }

    @Override
    public QuadristateButtonModel getModel() {
    return (QuadristateButtonModel) super.getModel();
    }

    public void setModel(QuadristateButtonModel model) {
    super.setModel(model);
    }

    @Override
    @Deprecated
    public void setModel(ButtonModel model) {
    // if (!(model instanceof TristateButtonModel))
    // useless: Java always calls the most specific method
    super.setModel(model);
    }

    /** No one may add mouse listeners, not even Swing! */
    @Override
    public void addMouseListener(MouseListener l) {
    }

    /**
         * Set the new state to either CHECKED, UNCHECKED or GREY_CHECKED. If
         * state == null, it is treated as GREY_CHECKED.
         */
    public void setState(State state) {
    getModel().setState(state);
    }

    /**
         * Return the current state, which is determined by the selection status
         * of the model.
         */
    public State getState() {
    return getModel().getState();
    }

}
