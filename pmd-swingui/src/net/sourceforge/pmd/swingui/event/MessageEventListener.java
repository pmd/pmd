package net.sourceforge.pmd.swingui.event;


import java.util.EventListener;


public interface MessageEventListener extends EventListener {

    /**
     ******************************************************************************
     *
     * @param event
     */
    public void displayMessage(MessageEvent event);
}
