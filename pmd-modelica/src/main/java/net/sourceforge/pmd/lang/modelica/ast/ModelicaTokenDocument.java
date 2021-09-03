/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;


public class ModelicaTokenDocument extends JavaccTokenDocument {

    public ModelicaTokenDocument(String fullText) {
        super(fullText);
    }

    @Override
    protected @Nullable String describeKindImpl(int kind) {
        return ModelicaTokenKinds.describe(kind);
    }
}
