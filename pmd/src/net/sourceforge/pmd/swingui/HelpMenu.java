package net.sourceforge.pmd.swingui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class HelpMenu extends JMenu {

    /**
     ********************************************************************
     *
     */
    protected HelpMenu() {
        super("Help");

        setMnemonic('H');

        Icon icon;
        JMenuItem menuItem;

        //
        // Online Help menu item
        //
        icon = UIManager.getIcon("help");
        menuItem = new JMenuItem("Online Help", icon);
        menuItem.addActionListener(new HelpActionListener());
        menuItem.setMnemonic('H');
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK));
        add(menuItem);

        //
        // Separator
        //
        add(new JSeparator());

        //
        // About menu item
        //
        menuItem = new JMenuItem("About...");
        menuItem.addActionListener(new AboutActionListener());
        menuItem.setMnemonic('A');
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
        add(menuItem);
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class HelpActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            MessageDialog.show(PMDViewer.getViewer(), "Online Help not available yet.");
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class AboutActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            PMDViewer viewer = PMDViewer.getViewer();
            viewer.setEnableViewer(false);
            (new AboutPMD(viewer)).setVisible(true);
            viewer.setEnableViewer(true);
        }
    }
}