/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public class SingleLineComment extends Comment {

    public SingleLineComment(Token t) {
        super(t);
    }


    @Override
    public String getXPathNodeName() {
        return "SingleLineComment";
    }
}
