package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.Rule;

public interface CanSuppressWarnings {
    boolean hasSuppressWarningsAnnotationFor(Rule rule);
}
