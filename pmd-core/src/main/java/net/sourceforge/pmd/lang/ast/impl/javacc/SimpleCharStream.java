/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

/**
 * A char stream that does not perform unicode escaping.
 */
public class SimpleCharStream extends JavaCharStream {

    public SimpleCharStream(JavaccTokenDocument document) {
        super(document);
    }

    @Override
    protected boolean doEscape() {
        return false;
    }
}
