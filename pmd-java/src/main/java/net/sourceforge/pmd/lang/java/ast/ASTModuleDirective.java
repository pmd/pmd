/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

/**
 * A directive of a {@linkplain ASTModuleDeclaration module declaration}.
 * Implementations provide more specific attributes.
 *
 * <pre class="grammar">
 *
 * ModuleDirective ::= {@linkplain ASTModuleRequiresDirective ModuleRequiresDirective}
 *                   | {@linkplain ASTModuleOpensDirective ModuleOpensDirective}
 *                   | {@linkplain ASTModuleExportsDirective ModuleExportsDirective}
 *                   | {@linkplain ASTModuleProvidesDirective ModuleProvidesDirective}
 *                   | {@linkplain ASTModuleUsesDirective ModuleUsesDirective}
 *
 * </pre>
 */
public abstract class ASTModuleDirective extends AbstractJavaNode {

    private final DirectiveType type;


    ASTModuleDirective(int id, DirectiveType type) {
        super(id);
        this.type = type;
    }

    /**
     * Returns the kind of the directive.
     */
    @NoAttribute
    public DirectiveType getType() {
        return type;
    }

    /**
     * Kind of a module directive. Specific kinds are represented by
     * specific subclasses.
     */
    public enum DirectiveType {
        REQUIRES, EXPORTS, OPENS, USES, PROVIDES
    }

}
