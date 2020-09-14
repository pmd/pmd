/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTNonEmptyList;

/**
 * A list of resources in a {@linkplain ASTTryStatement try-with-resources}.
 *
 * <pre class="grammar">
 *
 * ResourceList ::= "(" {@link ASTResource Resource} ( ";" {@link ASTResource Resource} )* ";"? ")"
 *
 * </pre>
 */
public final class ASTResourceList extends ASTNonEmptyList<ASTResource> {

    private boolean trailingSemi;

    ASTResourceList(int id) {
        super(id, ASTResource.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    void setTrailingSemi() {
        this.trailingSemi = true;
    }

    /**
     * Returns true if this resource list has a trailing semicolon, eg
     * in {@code try (InputStream is = getInputStream();) { ... }}.
     */
    public boolean hasTrailingSemiColon() {
        return trailingSemi;
    }

}
