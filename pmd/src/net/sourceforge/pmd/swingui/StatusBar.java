package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.StatusBarEvent;
import net.sourceforge.pmd.swingui.event.StatusBarEventListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

/**
 *
 * @author Donald A. Leckie
 * @since January 6, 2003
 * @version $Revision$, $Date$
 */
class StatusBar extends JPanel
{
    private JLabel m_message;
    private String m_defaultMessage;
    private StatusArea m_statusArea;

    /**
     ****************************************************************************
     *
     * @param border
     */
    protected StatusBar(String defaultMessage)
    {
        super(new BorderLayout());

        m_defaultMessage = defaultMessage;

        //
        // Status Indicator
        //
        m_statusArea = new StatusArea();
        add(m_statusArea, BorderLayout.WEST);

        //
        // Message
        //
        m_message = new JLabel();
        m_message.setFont(new Font("Dialog", Font.BOLD, 12));
        m_message.setBackground(UIManager.getColor("pmdMessageAreaBackground"));
        m_message.setForeground(UIManager.getColor("pmdBlue"));
        m_message.setBorder(new EmptyBorder(0, 5, 0, 0));
        setDefaultMessage();
        add(m_message, BorderLayout.CENTER);

        ListenerList.addListener((StatusBarEventListener) new StatusBarEventHandler());
    }

    /**
     *********************************************************************************
     *
     */
    protected void setDefaultMessage()
    {
        setMessage(m_defaultMessage);
    }

    /**
     *********************************************************************************
     *
     * @param message The message to be displayed in the status area.
     */
    protected void setMessage(String message)
    {
        if (message == null)
        {
            message = "";
        }

        m_message.setText(message);
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class StatusArea extends JPanel
    {
        private StatusActionThread m_actionThread;
        private Color m_inactiveBackground;
        private Color m_activeBackground;
        private Color m_actionColor;
        private int m_direction;
        private int m_indicatorCurrentPosition;
        private final int POSITION_INCREMENT = 5;
        private final int START_MOVING = 0;
        private final int MOVE_FORWARD = 1;
        private final int MOVE_BACKWARD = 2;

        /**
         ****************************************************************************
         *
         * @param border
         */
        private StatusArea()
        {
            super(null);

            m_inactiveBackground = Color.gray;
            m_activeBackground = UIManager.getColor("pmdStatusAreaBackground");
            m_actionColor = Color.red;

            setOpaque(true);
            setBackground(m_inactiveBackground);
            setBorder(new BevelBorder(BevelBorder.LOWERED));

            Dimension size = new Dimension(160, 12);

            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            setPreferredSize(size);
        }

        /**
         ****************************************************************************
         *
         */
        private void startAction()
        {
            if (m_actionThread == null)
            {
                setBackground(m_activeBackground);
                m_direction = START_MOVING;
                m_actionThread = new StatusActionThread(this);
                m_actionThread.start();
            }
        }

        /**
         ****************************************************************************
         *
         */
        private void stopAction()
        {
            if (m_actionThread != null)
            {
                m_actionThread.stopAction();
                m_actionThread = null;
                setBackground(m_inactiveBackground);
                repaint();
            }
        }

        /**
         ****************************************************************************
         *
         * @param graphics
         */
        public void paint(Graphics graphics)
        {
            super.paint(graphics);

            if (getBackground() == m_activeBackground)
            {
                Rectangle totalArea;
                Insets insets;
                int indicatorWidth;
                int indicatorHeight;
                int indicatorY;
                int indicatorX;
                int totalAreaRight;

                totalArea = getBounds();
                insets = getInsets();
                totalArea.x += insets.left;
                totalArea.y += insets.top;
                totalArea.width -= (insets.left + insets.right);
                totalArea.height -= (insets.top + insets.bottom);
                totalAreaRight = totalArea.x + totalArea.width;
                indicatorWidth = totalArea.width / 3;
                indicatorHeight = totalArea.height;
                indicatorY = totalArea.y;

                if (m_direction == MOVE_FORWARD)
                {
                    m_indicatorCurrentPosition += POSITION_INCREMENT;

                    if (m_indicatorCurrentPosition >= totalAreaRight)
                    {
                        m_indicatorCurrentPosition = totalAreaRight - POSITION_INCREMENT;
                        m_direction = MOVE_BACKWARD;
                    }
                }
                else if (m_direction == MOVE_BACKWARD)
                {
                    m_indicatorCurrentPosition -= POSITION_INCREMENT;

                    if (m_indicatorCurrentPosition < totalArea.x)
                    {
                        m_indicatorCurrentPosition = totalArea.x + POSITION_INCREMENT;
                        m_direction = MOVE_FORWARD;
                    }
                }
                else
                {
                    m_indicatorCurrentPosition = totalArea.x + POSITION_INCREMENT;
                    m_direction = MOVE_FORWARD;
                }

                indicatorX = m_indicatorCurrentPosition;

                Rectangle oldClip = graphics.getClipBounds();
                Color oldColor = graphics.getColor();

                graphics.setColor(m_activeBackground);
                graphics.setClip(totalArea.x, totalArea.y, totalArea.width, totalArea.height);
                graphics.clipRect(totalArea.x, totalArea.y, totalArea.width, totalArea.height);
                graphics.fillRect(totalArea.x, totalArea.y, totalArea.width, totalArea.height);

                if (m_direction == MOVE_FORWARD)
                {
                    int stopX = indicatorX - indicatorWidth;

                    if (stopX < totalArea.x)
                    {
                        stopX = totalArea.x;
                    }

                    int y1 = indicatorY;
                    int y2 = y1 + indicatorHeight;
                    Color color = m_actionColor;

                    for (int x = indicatorX; x > stopX; x--)
                    {
                        graphics.setColor(color);
                        graphics.drawLine(x, y1, x, y2);
                        color = brighter(color);
                    }
                }
                else
                {
                    int stopX = indicatorX + indicatorWidth;

                    if (stopX > totalAreaRight)
                    {
                        stopX = totalAreaRight;
                    }

                    int y1 = indicatorY;
                    int y2 = indicatorY + indicatorHeight;
                    Color color = m_actionColor;

                    for (int x = indicatorX; x < stopX; x++)
                    {
                        graphics.setColor(color);
                        graphics.drawLine(x, y1, x, y2);
                        color = brighter(color);
                    }
                }

                graphics.setColor(oldColor);

                if (oldClip != null)
                {
                    graphics.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
                    graphics.setClip(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
                }
            }
        }

        /**
         ****************************************************************************
         *
         * @param color
         *
         * @return
         */
        private Color brighter(Color color)
        {
            int red;
            int green;
            int blue;

            red = color.getRed() + 5;
            green = color.getGreen() + 5;
            blue = color.getBlue() + 5;

            if (red > 255)
            {
                red = 255;
            }

            if (green > 255)
            {
                green = 255;
            }

            if (blue > 255)
            {
                blue = 255;
            }

            return new Color(red, green, blue);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class StatusActionThread extends Thread
    {
        private StatusArea m_statusArea;
        private boolean m_stopAction;
        private int m_doNothing;
        private final long ELAPSED_TIME = 25;

        /**
         ****************************************************************************
         *
         * @param statusArea
         */
        private StatusActionThread(StatusArea statusArea)
        {
            super("Status Action");

            m_statusArea = statusArea;
        }

        /**
         ****************************************************************************
         *
         */
        public void run()
        {
            while (m_stopAction == false)
            {
                m_statusArea.repaint();

                try
                {
                    sleep(ELAPSED_TIME);
                }
                catch (InterruptedException exception)
                {
                    m_doNothing++;
                }
            }
        }

        /**
         ****************************************************************************
         *
         */
        private void stopAction()
        {
            m_stopAction = true;
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class StatusBarEventHandler implements StatusBarEventListener
    {

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void startAnimation(StatusBarEvent event)
        {
            m_statusArea.startAction();
            m_message.setText("");
            SwingUtilities.invokeLater(new Repaint(m_message));
        }

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void showMessage(StatusBarEvent event)
        {
            m_message.setText(event.getMessage());
            SwingUtilities.invokeLater(new Repaint(m_message));
        }

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void stopAnimation(StatusBarEvent event)
        {
            setDefaultMessage();
            SwingUtilities.invokeLater(new Repaint(m_message));
            m_statusArea.stopAction();
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class Repaint implements Runnable
    {
        private Component m_component;

        /**
         *****************************************************************************
         *
         * @param component
         */
        private Repaint(Component component)
        {
            m_component = component;
        }

        /**
         *****************************************************************************
         *
         */
        public void run()
        {
            m_component.repaint();
        }
    }
}
