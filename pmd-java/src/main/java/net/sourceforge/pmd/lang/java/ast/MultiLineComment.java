/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.GenericToken;

public class MultiLineComment extends Comment {

    public MultiLineComment(GenericToken t) {
        super(t);
    }


    @Override
    public String getXPathNodeName() {
        return "MultiLineComment";
    }

}
