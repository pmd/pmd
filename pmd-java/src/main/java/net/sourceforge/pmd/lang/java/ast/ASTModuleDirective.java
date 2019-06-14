/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTModuleDirective extends AbstractJavaNode {

    public enum DirectiveType {
        REQUIRES, EXPORTS, OPENS, USES, PROVIDES;
    }

    public enum RequiresModifier {
        STATIC, TRANSITIVE;
    }

    private DirectiveType type;

    private RequiresModifier requiresModifier;

    @InternalApi
    @Deprecated
    public ASTModuleDirective(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTModuleDirective(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @InternalApi
    @Deprecated
    public void setType(DirectiveType type) {
        this.type = type;
    }

    public String getType() {
        return String.valueOf(type);
    }

    @InternalApi
    @Deprecated
    public void setRequiresModifier(RequiresModifier requiresModifier) {
        this.requiresModifier = requiresModifier;
    }

    public String getRequiresModifier() {
        return requiresModifier == null ? null : requiresModifier.name();
    }
}
