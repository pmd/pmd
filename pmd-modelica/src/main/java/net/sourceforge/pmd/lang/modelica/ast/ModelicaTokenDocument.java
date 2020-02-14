/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.util.document.TextDocument;


public class ModelicaTokenDocument extends JavaccTokenDocument {

    public ModelicaTokenDocument(TextDocument textDocument) {
        super(textDocument);
    }

    @Override
    protected @Nullable String describeKindImpl(int kind) {
        return ModelicaTokenKinds.describe(kind);
    }
}
