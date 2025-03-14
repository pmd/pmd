/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

public final class ASTElement extends AbstractJspNode {

    /**
     * Name of the element-tag. Cannot be null.
     */
    private String name;

    /**
     * Flag indicating that the element consists of one tag ("<... />").
     */
    private boolean empty; //

    /**
     * Flag indicating that the parser did not find a proper ending marker or
     * ending tag for this element.
     */
    private boolean unclosed;

    ASTElement(int id) {
        super(id);
    }

    /**
     * @return boolean - true if the element has a namespace-prefix, false
     *         otherwise
     */
    public boolean isHasNamespacePrefix() {
        return name.indexOf(':') >= 0;
    }

    /**
     * @return String - the part of the name that is before the (first) colon
     *         (":")
     */
    public String getNamespacePrefix() {
        int colonIndex = name.indexOf(':');
        return colonIndex >= 0 ? name.substring(0, colonIndex) : "";
    }

    /**
     * @return String - The part of the name that is after the first colon
     *         (":"). If the name does not contain a colon, the full name is
     *         returned.
     */
    public String getLocalName() {
        int colonIndex = name.indexOf(':');
        return colonIndex >= 0 ? name.substring(colonIndex + 1) : name;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isUnclosed() {
        return unclosed;
    }

    void setUnclosed(boolean unclosed) {
        this.unclosed = unclosed;
    }

    void setEmpty(boolean empty) {
        this.empty = empty;
    }

    @Override
    protected <P, R> R acceptVisitor(JspVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
