package net.sourceforge.pmd.swingui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
public class MessageDialog extends JDialog {

    private JTextArea m_messageArea;
    private Exception m_exception;
    private boolean m_yesButtonWasPressed;

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param title
     * @param job
     */
    private MessageDialog(Frame parentWindow, String title, String message) {
        super(parentWindow, title, true);

        initialize(parentWindow, message);
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param title
     * @param job
     */
    private MessageDialog(Dialog parentWindow, String title, String message) {
        super(parentWindow, title, true);

        initialize(parentWindow, message);
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param message
     */
    private void initialize(Window parentWindow, String message) {
        int dialogWidth = 600;
        int dialogHeight = 150;
        Rectangle parentWindowBounds = parentWindow.getBounds();
        int x = parentWindowBounds.x + (parentWindowBounds.width - dialogWidth) / 2;
        int y = parentWindowBounds.y + (parentWindowBounds.height - dialogHeight) / 2;

        setBounds(x, y, dialogWidth, dialogHeight);

        EtchedBorder etchedBorder;
        EmptyBorder emptyBorder;
        CompoundBorder compoundBorder;
        JPanel basePanel;

        basePanel = new JPanel();
        etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
        emptyBorder = new EmptyBorder(15, 15, 15, 15);
        compoundBorder = new CompoundBorder(etchedBorder, emptyBorder);

        basePanel.setBorder(compoundBorder);
        basePanel.setLayout(new BorderLayout());
        getContentPane().add(basePanel, BorderLayout.CENTER);

        m_messageArea = ComponentFactory.createTextArea(message);

        m_messageArea.setFont(UIManager.getFont("messageFont"));
        m_messageArea.setEditable(false);
        basePanel.add(m_messageArea, BorderLayout.CENTER);
    }

    /**
     *******************************************************************************
     *
     */
    private void addCloseButton() {
        JButton closeButton = ComponentFactory.createButton("Close");
        JPanel buttonPanel = new JPanel(new FlowLayout());

        closeButton = ComponentFactory.createButton("Close");
        closeButton.setForeground(Color.white);
        closeButton.setBackground(Color.blue);
        closeButton.addActionListener(new CloseButtonActionListener());

        buttonPanel.add(closeButton);
    }

    /**
     *******************************************************************************
     *
     */
    private void addAnswerButtons() {
        JButton yesButton;
        JButton noButton;
        JPanel buttonPanel;

        buttonPanel = new JPanel(new FlowLayout());

        yesButton = ComponentFactory.createButton("Yes");
        yesButton.setForeground(Color.white);
        yesButton.setBackground(UIManager.getColor("pmdGreen"));
        yesButton.addActionListener(new YesButtonActionListener());
        buttonPanel.add(yesButton);

        noButton = ComponentFactory.createButton("No");
        noButton.setForeground(Color.white);
        noButton.setBackground(Color.red);
        noButton.addActionListener(new NoButtonActionListener());
        buttonPanel.add(noButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param message
     * @param exception
     */
    public static void show(Window parentWindow, String message, Exception exception) {
        if (exception == null) {
            show(parentWindow, message);
        } else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(5000);
            PrintStream printStream = new PrintStream(stream);

            exception.printStackTrace(printStream);

            if (message == null) {
                message = stream.toString();
            } else {
                message = message + "\n" + stream.toString();
            }

            printStream.close();

            MessageDialog dialog;

            if (parentWindow instanceof Frame) {
                dialog = new MessageDialog((Frame) parentWindow, "Exception", message);
            } else {
                dialog = new MessageDialog((Dialog) parentWindow, "Exception", message);
            }

            dialog.addCloseButton();
            dialog.setVisible(true);
        }
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param message
     */
    protected static boolean answerIsYes(Window parentWindow, String message) {
        MessageDialog dialog = setup(parentWindow, message);

        dialog.addAnswerButtons();
        dialog.setVisible(true);

        return dialog.m_yesButtonWasPressed;
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param message
     */
    public static void show(Window parentWindow, String message) {
        MessageDialog dialog;

        dialog = setup(parentWindow, message);
        dialog.addCloseButton();
        dialog.setVisible(true);
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param message
     */
    private static MessageDialog setup(Window parentWindow, String message) {
        if (message == null) {
            message = "There is no message.";
        }

        MessageDialog dialog;
        String title;

        title = "Information";

        if (parentWindow instanceof Frame) {
            dialog = new MessageDialog((Frame) parentWindow, title, message);
        } else {
            dialog = new MessageDialog((Dialog) parentWindow, title, message);
        }

        return dialog;
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class CloseButtonActionListener implements ActionListener {

        /**
         ************************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event) {
            MessageDialog.this.setVisible(false);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class YesButtonActionListener implements ActionListener {

        /**
         ************************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event) {
            MessageDialog.this.m_yesButtonWasPressed = true;
            MessageDialog.this.setVisible(false);
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class NoButtonActionListener implements ActionListener {

        /**
         ************************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event) {
            MessageDialog.this.m_yesButtonWasPressed = false;
            MessageDialog.this.setVisible(false);
        }
    }
}