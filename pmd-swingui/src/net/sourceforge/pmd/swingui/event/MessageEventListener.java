package net.sourceforge.pmd.swingui.event;
//J-
import java.util.EventListener;

public interface MessageEventListener extends EventListener
{

    /**
     ******************************************************************************
     *
     * @param event
     */
    public void displayMessage(MessageEvent event);
}