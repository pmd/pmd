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

    private static final Map<SourceType, SourceTypeHandler> mapSourceTypeOnSourceTypeHandler = CollectionUtil
            .mapFrom(new SourceType[] { SourceType.JAVA_13, SourceType.JAVA_14,
                    SourceType.JAVA_15, SourceType.JAVA_16, SourceType.JAVA_17, SourceType.JSP, },

            new SourceTypeHandler[] { new Java13Handler(), new Java14Handler(),
                    new Java15Handler(), new Java16Handler(),
                    new Java17Handler(), new JspTypeHandler(), });

    /**
     * Never create one
     */
    private SourceTypeHandlerBroker() {  }

    public static SourceTypeHandler getVisitorsFactoryForSourceType(SourceType sourceType) {
        SourceTypeHandler handler = mapSourceTypeOnSourceTypeHandler.get(sourceType);

        if (handler == null) {
            throw new IllegalArgumentException("No VisitorsFactory is registered for SourceType [" + sourceType + "].");
        } 
        return handler;
    }

}
