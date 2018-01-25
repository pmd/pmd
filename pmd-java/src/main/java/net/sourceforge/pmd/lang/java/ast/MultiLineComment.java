/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public class MultiLineComment extends Comment {

    public MultiLineComment(Token t) {
        super(t);
    }


    @Override
    public String getXPathNodeName() {
        return "MultiLineComment";
    }

}
