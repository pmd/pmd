package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * handles parsing exceptions
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class ParseExceptionHandler
        extends JDialog
        implements ActionListener {
    private Exception exc;
    private JTextArea errorArea;
    private JButton okBtn;

    /**
     * creates the dialog
     *
     * @param parent dialog's parent
     * @param exc    exception to be handled
     */
    public ParseExceptionHandler(JFrame parent, Exception exc) {
        super(parent, NLS.nls("COMPILE_ERROR.DIALOG.TITLE"), true);

        this.exc = exc;

        init();
    }

    private void init() {
        errorArea = new JTextArea();
        errorArea.setEditable(false);
        errorArea.setText(exc.getMessage() + "\n");

        getContentPane().setLayout(new BorderLayout());

        JPanel messagePanel = new JPanel(new BorderLayout());

        messagePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        NLS.nls("COMPILE_ERROR.PANEL.TITLE"))));

        messagePanel.add(new JScrollPane(errorArea), BorderLayout.CENTER);

        getContentPane().add(messagePanel, BorderLayout.CENTER);

        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        okBtn = new JButton(NLS.nls("COMPILE_ERROR.OK_BUTTON.CAPTION"));

        okBtn.addActionListener(this);

        btnPane.add(okBtn);

        getRootPane().setDefaultButton(okBtn);

        getContentPane().add(btnPane, BorderLayout.SOUTH);

        pack();

        setLocationRelativeTo(getParent());

        setVisible(true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okBtn) {
            dispose();
        }
    }
}


/*
 * $Log$
 * Revision 1.4  2004/09/27 19:42:52  tomcopeland
 * A ridiculously large checkin, but it's all just code reformatting.  Nothing to see here...
 *
 * Revision 1.3  2004/04/15 18:21:58  tomcopeland
 * Cleaned up imports with new version of IDEA; fixed some deprecated Ant junx
 *
 * Revision 1.2  2003/09/23 20:51:06  tomcopeland
 * Cleaned up imports
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.2  2003/09/24 00:40:35  bgr
 * evaluation results browsing added
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
