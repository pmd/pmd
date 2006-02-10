package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.SourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * Broker for SourceTypeHandler instances for specific SourceTypes.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class SourceTypeHandlerBroker {

    /**
     * Map of SourceType on SourceTypeHandler.
     */
    private Map mapSourceTypeOnSourceTypeHandler = new HashMap();

    /**
     * Public constructor.
     */
    public SourceTypeHandlerBroker() {
        initialize();
    }

    /**
     * Initialize the mapSourceTypeOnVisitorsFactory.
     */
    private void initialize() {
        mapSourceTypeOnSourceTypeHandler.put(SourceType.JAVA_13, new Java13Handler());
        mapSourceTypeOnSourceTypeHandler.put(SourceType.JAVA_14, new Java14Handler());
        mapSourceTypeOnSourceTypeHandler.put(SourceType.JAVA_15, new Java15Handler());
        mapSourceTypeOnSourceTypeHandler.put(SourceType.JSP, new JspTypeHandler());
    }

    public SourceTypeHandler getVisitorsFactoryForSourceType(SourceType sourceType) {
        SourceTypeHandler handler = (SourceTypeHandler) mapSourceTypeOnSourceTypeHandler.get(sourceType);

        if (handler == null) {
            throw new IllegalArgumentException("No VisitorsFactory is registered for SourceType [" + sourceType + "].");
        } else {
            return handler;
        }
    }

}
