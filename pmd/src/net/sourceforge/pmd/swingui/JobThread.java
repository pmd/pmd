package net.sourceforge.pmd.swingui;


class JobThread extends Thread
{

    private MessageDialog m_messageDialog;

    /**
     *********************************************************************************
     *
     * @param threadName
     */
    protected JobThread(String threadName)
    {
        super(threadName);
    }

    /**
     *********************************************************************************
     *
     * @return messageDialog
     */
    protected MessageDialog getMessageDialog()
    {
        return m_messageDialog;
    }

    /**
     *********************************************************************************
     *
     * @param messageDialog
     */
    protected void setMessageDialog(MessageDialog messageDialog)
    {
        m_messageDialog = messageDialog;
    }

    /**
     *********************************************************************************
     *
     */
    protected void closeWindow()
    {
        m_messageDialog.setVisible(false);
    }
}