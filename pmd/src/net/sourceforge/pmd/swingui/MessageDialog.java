package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread;
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
class MessageDialog extends JDialog
{

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param title
     * @param job
     */
    private MessageDialog(JFrame parentWindow, String title, String message)
    {
        super(parentWindow, title, true);

        int dialogWidth = 400;
        int dialogHeight = 100;
        Rectangle parentWindowBounds = parentWindow.getBounds();
        int x = parentWindowBounds.x + (parentWindowBounds.width - dialogWidth) / 2;
        int y = parentWindowBounds.y + (parentWindowBounds.height - dialogHeight) / 2;

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
    protected void setJobFinished()
    {
        setVisible(false);
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param title
     * @param job
     */
    protected static void show(JFrame parentWindow, String message, JobThread job)
    {
        if (job != null)
        {
            if (message == null)
            {
                message = "No message.";
            }

            MessageDialog dialog = new MessageDialog(parentWindow, "Message", message);

            job.setMessageDialog(dialog);
            job.start();
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setVisible(true);
        }
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param exception
     */
    protected static void show(JFrame parentWindow, String message, Exception exception)
    {
        if (exception != null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
            PrintStream writer = new PrintStream(stream);

            exception.printStackTrace(writer);

            if (message == null) {
                message = stream.toString();
            } else {
                message = message + "\n" + stream.toString();
            }

            writer.close();
            MessageDialog dialog = new MessageDialog(parentWindow, "Exception", message);

            dialog.setVisible(true);
        }
    }

    /**
     *******************************************************************************
     *
     * @param parentWindow
     * @param exception
     */
    protected static void show(JFrame parentWindow, String message)
    {
        if (message == null)  {
            message = "There is no message.";
        }

        MessageDialog dialog = new MessageDialog(parentWindow, "message", message);

        dialog.setVisible(true);
    }
}