package net.sourceforge.pmd.swingui;

import java.util.EventObject;

/**
 *
 * @author Donald A. Leckie
 * @since September 27, 2002
 * @version $Revision$, $Date$
 */
class NameChangeEvent extends EventObject
{

    private String m_oldName;
    private String m_newName;

    /**
     *********************************************************************************
     *
     * @param source
     * @param oldName
     * @param newName
     */
    protected NameChangeEvent(Object source, String oldName, String newName)
    {
        super(source);

        m_oldName = oldName;
        m_newName = newName;
    }

    /**
     *********************************************************************************
     *
     * @return
     */
    protected String getOldName()
    {
        return m_oldName;
    }

    /**
     *********************************************************************************
     *
     * @return
     */
    protected String getNewName()
    {
        return m_newName;
    }
}