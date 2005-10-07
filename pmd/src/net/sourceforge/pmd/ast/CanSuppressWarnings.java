package net.sourceforge.pmd.ast;

import net.sourceforge.pmd.Rule;

public interface CanSuppressWarnings {
    boolean hasSuppressWarningsAnnotationFor(Rule rule);
}
