package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class MessageDialog extends JDialog implements JobThreadListener
{

    private JTextArea m_messageArea;
    private JobThread m_jobThread;

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param title
     * @param job
     */
    private MessageDialog(Frame parentWindow, String title, String message)
    {
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
    private MessageDialog(Dialog parentWindow, String title, String message)
    {
        super(parentWindow, title, true);

        initialize(parentWindow, message);
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param message
     */
    private void initialize(Window parentWindow, String message)
    {
        int dialogWidth = 400;
        int dialogHeight = 150;
        Rectangle parentWindowBounds = parentWindow.getBounds();
        int x = parentWindowBounds.x + (parentWindowBounds.width - dialogWidth) / 2;
        int y = parentWindowBounds.y + (parentWindowBounds.height - dialogHeight) / 2;

        setBounds(x, y, dialogWidth, dialogHeight);

        BevelBorder bevelBorder;
        EtchedBorder etchedBorder;
        EmptyBorder emptyBorder;
        CompoundBorder compoundBorder;
        JPanel basePanel;

        basePanel = new JPanel();
        etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
        emptyBorder = new EmptyBorder(15,15,15,15);
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
    private void addCloseButton()
    {
        JButton closeButton;
        JPanel buttonPanel;

        closeButton = ComponentFactory.createButton("Close");
        buttonPanel = new JPanel(new FlowLayout());

        closeButton.setForeground(Color.white);
        closeButton.setBackground(Color.blue);
        closeButton.addActionListener(new CloseButtonActionListener());

        buttonPanel.add(closeButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     *******************************************************************************
     *
     * @parameter event
     */
    public void jobThreadStarted(JobThreadEvent event)
    {
    }

    /**
     *******************************************************************************
     *
     * @parameter event
     */
    public void jobThreadFinished(JobThreadEvent event)
    {
        setVisible(false);
    }

    /**
     *******************************************************************************
     *
     * @parameter event
     */
    public void jobThreadStatus(JobThreadEvent event)
    {
        m_messageArea.setText(event.getMessage());
    }

    /**
     *******************************************************************************
     *
     * @param title
     * @param job
     */
    protected static void show(Window parentWindow, String message, JobThread jobThread)
    {
        if (jobThread != null)
        {
            if (message == null)
            {
                message = "No message.";
            }

            MessageDialog dialog;

            if (parentWindow instanceof Frame)
            {
                dialog = new MessageDialog((Frame) parentWindow, "Message", message);
            }
            else
            {
                dialog = new MessageDialog((Dialog) parentWindow, "Message", message);
            }

            dialog.m_jobThread = jobThread;

            jobThread.addListener(dialog);
            jobThread.start();
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setVisible(true);
        }
    }

    /**
     *******************************************************************************
     *
     * @param exception
     */
    protected static void show(Window parentWindow, String message, Exception exception)
    {
        if (exception == null)
        {
            show(parentWindow, message);
        }
        else
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
            PrintStream printStream = new PrintStream(stream);

            exception.printStackTrace(printStream);

            if (message == null)
            {
                message = stream.toString();
            }
            else
            {
                message = message + "\n" + stream.toString();
            }

            printStream.close();

            MessageDialog dialog;

            if (parentWindow instanceof Frame)
            {
                dialog = new MessageDialog((Frame) parentWindow, "Exception", message);
            }
            else
            {
                dialog = new MessageDialog((Dialog) parentWindow, "Exception", message);
            }

            dialog.addCloseButton();
            dialog.setVisible(true);
        }
    }

    /**
     *******************************************************************************
     *
     * @param exception
     */
    protected static void show(Window parentWindow, String message)
    {
        if (message == null)
        {
            message = "There is no message.";
        }

        MessageDialog dialog;

        if (parentWindow instanceof Frame)
        {
            dialog = new MessageDialog((Frame) parentWindow, "message", message);
        }
        else
        {
            dialog = new MessageDialog((Dialog) parentWindow, "message", message);
        }

        dialog.addCloseButton();
        dialog.setVisible(true);
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class CloseButtonActionListener implements ActionListener
    {

        /**
         ************************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            MessageDialog.this.setVisible(false);
        }
    }
}