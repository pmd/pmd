/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.swift.ast.SwiftBaseVisitor;

public abstract class AbstractSwiftRule<T> extends SwiftBaseVisitor<T> {
    public AbstractSwiftRule() {
        super.setLanguage(LanguageRegistry.getLanguage(SwiftLanguageModule.NAME));
    }
}
