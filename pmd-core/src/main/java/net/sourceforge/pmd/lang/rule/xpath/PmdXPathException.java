/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sf.saxon.trans.XPathException;

/**
 * Unchecked exception wrapper for {@link XPathException}.
 */
public class PmdXPathException extends ContextedRuntimeException {

    private static final String ERROR_KIND = "Kind";
    private static final String ERROR_PHASE = "Phase";
    private static final String EXPR = "Expression";
    private static final String VERSION = "Version";
    private static final String RULE_NAME = "Rule";

    public PmdXPathException(XPathException e, Phase phase, String expression, XPathVersion version) {
        super(e);
        setContextValue(ERROR_KIND, getErrorKind(e));
        setContextValue(ERROR_PHASE, phase);
        setContextValue(EXPR, expression);
        setContextValue(VERSION, version);
    }

    public Phase getPhase() {
        return (Phase) getFirstContextValue(ERROR_PHASE);
    }

    public PmdXPathException addRuleName(String ruleName) {
        setContextValue(RULE_NAME, ruleName);
        return this;
    }

    public @Nullable String getRuleName() {
        return (String) getFirstContextValue(RULE_NAME);
    }

    private String getErrorKind(XPathException e) {
        if (e.isSyntaxError()) {
            return "Syntax error";
        } else if (e.isTypeError()) {
            return "Type error";
        } else if (e.isStaticError()) {
            return "Static error";
        }
        return "Unknown error";
    }

    public enum Phase {
        INITIALIZATION,
        EVALUATION
    }

}
