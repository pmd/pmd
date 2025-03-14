/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

abstract class AbstractContentNode extends AbstractJspNode {

    private String content;

    protected AbstractContentNode(int id) {
        super(id);
    }

    public String getContent() {
        return content;
    }

    void setContent(String content) {
        this.content = content;
    }
}
