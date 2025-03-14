/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.InternalApiBridge;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassType;

/**
 * Common parent for class-specifier nodes, see {@link ModelicaClassSpecifierNode} for public API.
 */
abstract class AbstractModelicaClassSpecifierNode extends AbstractModelicaNode implements ModelicaClassSpecifierNode {
    AbstractModelicaClassSpecifierNode(int id) {
        super(id);
    }

    /**
     * Fills in the class definition with <code>extends</code> and <code>import</code> clauses contained in this AST node.
     *
     * @param classTypeDeclaration a class declaration object corresponding to this AST node
     */
    void populateExtendsAndImports(ModelicaClassType classTypeDeclaration) {
        // by default, do nothing
    }

    private void pushExtendsAndImportsFromList(ModelicaClassType classTypeDeclaration, ASTElementList listNode) {
        for (int i = 0; i < listNode.getNumChildren(); ++i) {
            AbstractModelicaNode child = (AbstractModelicaNode) listNode.getChild(i);
            if (child instanceof ASTExtendsClause) {
                InternalApiBridge.addExtendToClass(
                        classTypeDeclaration,
                        listNode.getVisibility(),
                        child.firstChild(ASTName.class).getCompositeName()
                );
            }
            if (child instanceof ASTImportClause) {
                InternalApiBridge.addImportToClass(
                        classTypeDeclaration,
                        listNode.getVisibility(),
                        child.firstChild(ModelicaImportClause.class)
                );
            }
        }
    }

    void pushExtendsAndImports(ModelicaClassType classTypeDeclaration, ASTComposition composition) {
        for (int i = 0; i < composition.getNumChildren(); ++i) {
            ModelicaNode maybeElementList = composition.getChild(i);
            if (maybeElementList instanceof ASTElementList) {
                pushExtendsAndImportsFromList(classTypeDeclaration, (ASTElementList) maybeElementList);
            }
        }
    }

    @Override
    public String getSimpleClassName() {
        return firstChild(ASTSimpleName.class).getName();
    }
}
