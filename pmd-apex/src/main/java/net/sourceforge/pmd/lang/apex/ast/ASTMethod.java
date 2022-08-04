/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSignature;
import net.sourceforge.pmd.lang.ast.SignedNode;

import com.google.summit.ast.declaration.MethodDeclaration;

public class ASTMethod extends AbstractApexNode.Single<MethodDeclaration> implements ApexQualifiableNode,
       SignedNode<ASTMethod>, CanSuppressWarnings {

    /**
     * Internal name used by constructors.
     */
    public static final String CONSTRUCTOR_ID = "<init>";

    /**
     * Internal name used by static initialization blocks.
     */
    public static final String STATIC_INIT_ID = "<clinit>";

    @Deprecated
    @InternalApi
    public ASTMethod(MethodDeclaration method) {
        super(method);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the name of the method, converting the parser name to the internal PMD name as
     * needed.
     */
    private String getName() {
        if (node.isAnonymousInitializationCode()) {
            return STATIC_INIT_ID;
        } else if (node.isConstructor()) {
            return CONSTRUCTOR_ID;
        } else {
            return node.getId().asCodeString();
        }
    }

    @Override
    public String getImage() {
        return getName();
        // TODO(b/239648780): differs from #getCanonicalName in some instances
    }

    public String getCanonicalName() {
        return getName();
    }

    @Override
    public int getBeginLine() {
        if (!hasRealLoc()) {
            // this is a synthetic method, only in the AST, not in the source
            // search for the last sibling with real location from the end
            // and place this synthetic method after it.
            for (int i = getParent().getNumChildren() - 1; i >= 0; i--) {
                ApexNode<?> sibling = getParent().getChild(i);
                if (sibling.hasRealLoc()) {
                    return sibling.getEndLine();
                }
            }
        }
        return super.getBeginLine();
    }

    @Override
    public int getBeginColumn() {
        if (!hasRealLoc()) {
            // this is a synthetic method, only in the AST, not in the source
            // search for the last sibling with real location from the end
            // and place this synthetic method after it.
            for (int i = getParent().getNumChildren() - 1; i >= 0; i--) {
                ApexNode<?> sibling = getParent().getChild(i);
                if (sibling.hasRealLoc()) {
                    return sibling.getEndColumn();
                }
            }
        }
        return super.getBeginColumn();
    }

    @Override
    public int getEndLine() {
        if (!hasRealLoc()) {
            // this is a synthetic method, only in the AST, not in the source
            return this.getBeginLine();
        }

        ASTBlockStatement block = getFirstChildOfType(ASTBlockStatement.class);
        if (block != null) {
            return block.getEndLine();
        }

        return super.getEndLine();
    }

    @Override
    public int getEndColumn() {
        if (!hasRealLoc()) {
            // this is a synthetic method, only in the AST, not in the source
            return this.getBeginColumn();
        }

        ASTBlockStatement block = getFirstChildOfType(ASTBlockStatement.class);
        if (block != null) {
            return block.getEndColumn();
        }

        return super.getEndColumn();
    }

    @Override
    public ApexQualifiedName getQualifiedName() {
        return ApexQualifiedName.ofMethod(this);
    }


    @Override
    public ApexOperationSignature getSignature() {
        return ApexOperationSignature.of(this);
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        for (ASTModifierNode modifier : findChildrenOfType(ASTModifierNode.class)) {
            for (ASTAnnotation a : modifier.findChildrenOfType(ASTAnnotation.class)) {
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isConstructor() {
        return node.isConstructor();
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getReturnType() {
        return node.getReturnType().asCodeString();
    }

    public int getArity() {
        return node.getParameterDeclarations().size();
    }
}
