package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class MessageDialog extends JDialog implements ActionListener
{

    private JobThread m_jobThread;

    /**
     *******************************************************************************
     *
     * @param title
     * @param job
     */
    private MessageDialog(String title, String message)
    {
        super(PMDViewer.getWindow(), title, true);

        int dialogWidth = 400;
        int dialogHeight = 100;
        Rectangle parentWindowBounds = PMDViewer.getWindow().getBounds();
        int x = parentWindowBounds.x + (parentWindowBounds.width - dialogWidth) / 2;
        int y = parentWindowBounds.y + (parentWindowBounds.height - dialogHeight) / 2;;

        setBounds(x, y, dialogWidth, dialogHeight);

        JPanel basePanel = new JPanel();

        {
            EtchedBorder etchedBorder;
            EmptyBorder emptyBorder;
            CompoundBorder compoundBorder;

            etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
            emptyBorder = new EmptyBorder(15,15,15,15);
            compoundBorder = new CompoundBorder(etchedBorder, emptyBorder);

            basePanel.setBorder(compoundBorder);
        }

        basePanel.setLayout(new BorderLayout());
        getContentPane().add(basePanel, BorderLayout.CENTER);

        JLabel messageArea = new JLabel(message);

        {
            BevelBorder bevelBorder;
            EtchedBorder etchedBorder;
            EmptyBorder emptyBorder;
            CompoundBorder compoundBorder;

            bevelBorder = new BevelBorder(BevelBorder.LOWERED);
            etchedBorder = new EtchedBorder(EtchedBorder.LOWERED);
            emptyBorder = new EmptyBorder(3,3,3,3);
            compoundBorder = new CompoundBorder(bevelBorder, etchedBorder);
            compoundBorder = new CompoundBorder(compoundBorder, emptyBorder);

            messageArea.setBorder(compoundBorder);
        }

        messageArea.setHorizontalAlignment(SwingConstants.CENTER);
        messageArea.setVerticalAlignment(SwingConstants.CENTER);
        basePanel.add(messageArea, BorderLayout.CENTER);
    }

    /**
     *******************************************************************************
     *
     */
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource().equals(m_jobThread))
        {
            if (event.getActionCommand().equals(JobThread.FINISHED_JOB_THREAD))
            {
                setVisible(false);
            }
        }
    }

    /**
     *******************************************************************************
     *
     * @param title
     * @param job
     */
    protected static void show(String message, JobThread jobThread)
    {
        if (jobThread != null)
        {
            if (message == null)
            {
                message = "No message.";
            }

            MessageDialog dialog;

            dialog = new MessageDialog("Message", message);
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
    protected static void show(String message, Exception exception)
    {
        if (exception != null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
            PrintStream writer = new PrintStream(stream);

            exception.printStackTrace(writer);

            if (message == null)
            {
                message = stream.toString();
            }
            else
            {
                message = message + "\n" + stream.toString();
            }

            writer.close();

            MessageDialog dialog = new MessageDialog("Exception", message);

            dialog.setVisible(true);
        }
    }

    /**
     *******************************************************************************
     *
     * @param exception
     */
    protected static void show(String message)
    {
        if (message == null)
        {
            message = "There is no message.";
        }

        MessageDialog dialog = new MessageDialog("message", message);

        dialog.setVisible(true);
    }
}