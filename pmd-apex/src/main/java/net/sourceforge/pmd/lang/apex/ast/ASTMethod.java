/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSignature;
import net.sourceforge.pmd.lang.ast.SignedNode;

public class ASTMethod extends AbstractApexNode<Node> implements ApexQualifiableNode,
       SignedNode<ASTMethod>, CanSuppressWarnings {

    @Deprecated
    @InternalApi
    public ASTMethod(Node method) {
        super(method);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        // return node.getMethodInfo().getName();
        // TODO(b/239648780)
        return null;
    }

    public String getCanonicalName() {
        // return node.getMethodInfo().getCanonicalName();
        // TODO(b/239648780)
        return null;
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
        // return node.getMethodInfo().isConstructor();
        // TODO(b/239648780)
        return false;
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getReturnType() {
        // return node.getMethodInfo().getEmitSignature().getReturnType().getApexName();
        // TODO(b/239648780)
        return null;
    }

    public int getArity() {
        // return node.getMethodInfo().getParameterTypes().size();
        // TODO(b/239648780)
        return 0;
    }
}
