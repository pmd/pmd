/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


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


    ASTModuleDirective(int id) {
        super(id);
    }

}
