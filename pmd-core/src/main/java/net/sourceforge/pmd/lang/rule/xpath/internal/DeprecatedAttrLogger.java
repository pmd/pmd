/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

/**
 * Records usages of deprecated attributes in XPath rules. This needs
 * to be threadsafe, XPath rules have one each (and share it).
 */
public abstract class DeprecatedAttrLogger {

    private static final Logger LOG = LoggerFactory.getLogger(Attribute.class);


    public abstract void recordUsageOf(Attribute attribute);

    /**
     * Create a new context for the given rule, returns a noop implementation
     * if the warnings would be ignored anyway.
     */
    public static DeprecatedAttrLogger create(XPathRule rule) {
        return doCreate(rule, false);
    }

    public static DeprecatedAttrLogger createForSuppression(Rule rule) {
        return doCreate(rule, true);
    }

    private static DeprecatedAttrLogger doCreate(Rule rule, boolean isSuppressionQuery) {
        if (LOG.isWarnEnabled()) {
            return new AttrLoggerImpl(rule, isSuppressionQuery);
        } else {
            return noop();
        }
    }

    public static DeprecatedAttrLogger createAdHocLogger() {
        if (LOG.isWarnEnabled()) {
            return new AdhocLoggerImpl();
        } else {
            return noop();
        }
    }

    public static Noop noop() {
        return Noop.INSTANCE;
    }

    private static String getLoggableAttributeName(Attribute attr) {
        return attr.getParent().getXPathNodeName() + "/@" + attr.getName();
    }

    private static final class Noop extends DeprecatedAttrLogger {

        static final Noop INSTANCE = new Noop();

        @Override
        public void recordUsageOf(Attribute attribute) {
            // do nothing
        }
    }

    private static final class AttrLoggerImpl extends DeprecatedAttrLogger {

        private final ConcurrentMap<String, Boolean> deprecated = new ConcurrentHashMap<>();
        private final Rule rule;
        private final boolean isSuppressionQuery;

        private AttrLoggerImpl(Rule rule, boolean isSuppressionQuery) {
            this.rule = rule;
            this.isSuppressionQuery = isSuppressionQuery;
        }

        @Override
        public void recordUsageOf(Attribute attribute) {
            String replacement = attribute.replacementIfDeprecated();
            if (replacement != null) {
                String name = getLoggableAttributeName(attribute);
                Boolean b = deprecated.putIfAbsent(name, Boolean.TRUE);
                if (b == null) {
                    // this message needs to be kept in sync with PMDCoverageTest / BinaryDistributionIT

                    String user = isSuppressionQuery ? "violationSuppressXPath for rule " + ruleToString()
                                                     : "XPath rule " + ruleToString();
                    String msg = "Use of deprecated attribute '" + name + "' by " + user;
                    if (!replacement.isEmpty()) {
                        msg += ", please use " + replacement + " instead";
                    }
                    LOG.warn(msg);
                }
            }
        }

        public String ruleToString() {
            // we can't compute that beforehand because the name is set
            // outside of the rule constructor
            String name = "'" + rule.getName() + "'";
            if (rule.getRuleSetName() != null) {
                name += " (in ruleset '" + rule.getRuleSetName() + "')";
            }
            return name;
        }
    }

    private static final class AdhocLoggerImpl extends DeprecatedAttrLogger {
        @Override
        public void recordUsageOf(Attribute attribute) {
            String replacement = attribute.replacementIfDeprecated();
            if (replacement != null) {
                String name = getLoggableAttributeName(attribute);
                // this message needs to be kept in sync with PMDCoverageTest / BinaryDistributionIT
                String msg = "Use of deprecated attribute '" + name + "' in a findChildNodesWithXPath navigation";
                if (!replacement.isEmpty()) {
                    msg += ", please use " + replacement + " instead";
                }
                // log with exception stack trace to help figure out where exactly the xpath is used.
                LOG.warn(msg, new RuntimeException(msg));
            }
        }
    }
}
