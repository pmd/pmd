/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.XPathRule;

/**
 * Records usages of deprecated attributes in XPath rules. This needs
 * to be threadsafe, XPath rules have one each (and share it).
 */
public abstract class DeprecatedAttrLogger {

    private static final Logger LOG = Logger.getLogger(Attribute.class.getName());


    public abstract void recordUsageOf(Attribute attribute);

    /**
     * Create a new context for the given rule, returns a noop implementation
     * if the warnings would be ignored anyway.
     */
    public static DeprecatedAttrLogger create(XPathRule rule) {
        if (LOG.isLoggable(Level.WARNING)) {
            return new AttrLoggerImpl(rule);
        } else {
            return noop();
        }
    }

    public static DeprecatedAttrLogger createAdHocLogger() {
        if (LOG.isLoggable(Level.WARNING)) {
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

    private static class Noop extends DeprecatedAttrLogger {

        static final Noop INSTANCE = new Noop();

        @Override
        public void recordUsageOf(Attribute attribute) {
            // do nothing
        }
    }

    private static class AttrLoggerImpl extends DeprecatedAttrLogger {

        private final ConcurrentMap<String, Boolean> deprecated = new ConcurrentHashMap<>();
        private final XPathRule rule;

        private AttrLoggerImpl(XPathRule rule) {
            this.rule = rule;
        }

        @Override
        public void recordUsageOf(Attribute attribute) {
            String replacement = attribute.replacementIfDeprecated();
            if (replacement != null) {
                String name = getLoggableAttributeName(attribute);
                Boolean b = deprecated.putIfAbsent(name, Boolean.TRUE);
                if (b == null) {
                    // this message needs to be kept in sync with PMDCoverageTest / BinaryDistributionIT
                    String msg = "Use of deprecated attribute '" + name + "' by XPath rule " + ruleToString();
                    if (!replacement.isEmpty()) {
                        msg += ", please use " + replacement + " instead";
                    }
                    LOG.warning(msg);
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

    private static class AdhocLoggerImpl extends DeprecatedAttrLogger {
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
                // log with execption stack trace to help figure out where exactly the xpath is used.
                LOG.log(Level.WARNING, msg, new RuntimeException(msg));
            }
        }
    }
}
