package net.sourceforge.pmd.util.viewer.model;

/**
 * The event which will be sent every time  the model changes
 * <p/>
 * <p/>
 * Note: the instances will be immutable
 * </p>
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class ViewerModelEvent {
    /**
     * reason in the case of code recompilation
     */
    public static final int CODE_RECOMPILED = 1;

    /**
     * reason in the case of node selection
     */
    public static final int NODE_SELECTED = 2;

    /**
     * reason in the case of path extension
     */
    public static final int PATH_EXPRESSION_APPENDED = 3;

    /**
     * reason in the case of path expression evaluation
     */
    public static final int PATH_EXPRESSION_EVALUATED = 4;
    private Object source;
    private int reason;
    private Object parameter;

    /**
     * Creates an event
     *
     * @param source event's source
     * @param reason event's reason
     */
    public ViewerModelEvent(Object source, int reason) {
        this(source, reason, null);
    }

    /**
     * Creates an event
     *
     * @param source    event's source
     * @param reason    event's reason
     * @param parameter parameter object
     */
    public ViewerModelEvent(Object source, int reason, Object parameter) {
        this.source = source;
        this.reason = reason;
        this.parameter = parameter;
    }

    /**
     * retrieves the reason for event's occurance
     *
     * @return event's reason
     */
    public int getReason() {
        return reason;
    }

    /**
     * retrieves the object which caused the event
     *
     * @return object that casused the event
     */
    public Object getSource() {
        return source;
    }

    /**
     * retrieves event's parameter
     *
     * @return event's parameter
     */
    public Object getParameter() {
        return parameter;
    }
}


/*
 * $Log$
 * Revision 1.2  2004/09/27 19:42:52  tomcopeland
 * A ridiculously large checkin, but it's all just code reformatting.  Nothing to see here...
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.3  2003/09/24 00:40:35  bgr
 * evaluation results browsing added
 *
 * Revision 1.2  2003/09/23 07:52:16  bgr
 * menus added
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
