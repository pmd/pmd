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

import javax.swing.DefaultButtonModel;

/**
 * The model for a quadristate CheckBox. Available states are UNCHECKED,
 * CHECKED, GREY_CHECKED, GREY_UNCHECKED
 *
 * @author boldrini
 */
public class QuadristateButtonModel extends DefaultButtonModel {

    public enum State {
    UNCHECKED, CHECKED, GREY_CHECKED, GREY_UNCHECKED
    }

    public QuadristateButtonModel() {
    super();
    setState(State.UNCHECKED);
    }

    /** Filter: No one may change the armed status except us. */
    @Override
    public void setArmed(boolean b) {
    }

    // public void setSelected(boolean b) {
    // if (b) {
    // setState(State.CHECKED);
    // } else {
    // setState(State.UNCHECKED);
    // }
    // }

    public void setState(State state) {
    switch (state) {
    case UNCHECKED:
        super.setArmed(false);
        setPressed(false);
        setSelected(false);
        break;
    case CHECKED:
        super.setArmed(false);
        setPressed(false);
        setSelected(true);
        break;
    case GREY_UNCHECKED:
        super.setArmed(true);
        setPressed(true);
        setSelected(false);
        break;
    case GREY_CHECKED:
        super.setArmed(true);
        setPressed(true);
        setSelected(true);
        break;
    }
    }

    /**
         * The current state is embedded in the selection / armed state of the
         * model. We return the CHECKED state when the checkbox is selected but
         * not armed, GREY_CHECKED state when the checkbox is selected and armed
         * (grey) and UNCHECKED when the checkbox is deselected.
         */
    public State getState() {
    if (isSelected() && !isArmed()) {
        // CHECKED
        return State.CHECKED;
    } else if (isSelected() && isArmed()) {
        // GREY_CHECKED
        return State.GREY_CHECKED;
    } else if (!isSelected() && isArmed()) {
        // GREY_UNCHECKED
        return State.GREY_UNCHECKED;
    } else { // (!isSelected() && !isArmed()){
        // UNCHECKED
        return State.UNCHECKED;
    }
    }

    /**
         * We rotate between UNCHECKED, CHECKED, GREY_UNCHECKED, GREY_CHECKED.
         */
    public void nextState() {
    switch (getState()) {
    case UNCHECKED:
        setState(State.CHECKED);
        break;
    case CHECKED:
        setState(State.GREY_UNCHECKED);
        break;
    case GREY_UNCHECKED:
        setState(State.GREY_CHECKED);
        break;
    case GREY_CHECKED:
        setState(State.UNCHECKED);
        break;
    }
    }
}
