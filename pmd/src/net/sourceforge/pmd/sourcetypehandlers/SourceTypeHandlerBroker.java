package net.sourceforge.pmd.sourcetypehandlers;

import java.util.Map;

import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Broker for SourceTypeHandler instances for specific SourceTypes.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class SourceTypeHandlerBroker {

    private static final Map mapSourceTypeOnSourceTypeHandler = CollectionUtil.mapFrom( new Object[][] {
    	{ SourceType.JAVA_13, new Java13Handler()},
    	{ SourceType.JAVA_14, new Java14Handler()},
    	{ SourceType.JAVA_15, new Java15Handler()},
    	{ SourceType.JAVA_16, new Java16Handler()},
    	{ SourceType.JSP, new JspTypeHandler()},
    	});

    /**
     * Never create one
     */
    private SourceTypeHandlerBroker() {  }

    public static SourceTypeHandler getVisitorsFactoryForSourceType(SourceType sourceType) {
        SourceTypeHandler handler = (SourceTypeHandler) mapSourceTypeOnSourceTypeHandler.get(sourceType);

        if (handler == null) {
            throw new IllegalArgumentException("No VisitorsFactory is registered for SourceType [" + sourceType + "].");
        } 
        return handler;
    }

}
