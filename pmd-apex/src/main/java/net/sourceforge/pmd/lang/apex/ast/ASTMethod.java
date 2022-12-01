/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSignature;
import net.sourceforge.pmd.lang.ast.SignedNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import java.util.List;
import java.util.stream.Collectors;

import com.google.summit.ast.SourceLocation;
import com.google.summit.ast.declaration.MethodDeclaration;

public class ASTMethod extends AbstractApexNode implements ApexQualifiableNode,
        SignedNode<ASTMethod>, CanSuppressWarnings {

    // Store the details instead of wrapping a com.google.summit.ast.Node.
    // This is to allow synthetic ASTMethod nodes.
    // An example is the trigger `invoke` method.
    private String name;
    private List<String> parameterTypes;
    private String returnType;
    private SourceLocation sourceLocation;

    /**
     * Internal name used by constructors.
     */
    public static final String CONSTRUCTOR_ID = "<init>";

    /**
     * Internal name used by static initialization blocks.
     */
    public static final String STATIC_INIT_ID = "<clinit>";

    public ASTMethod(
        String name,
        List<String> parameterTypes,
        String returnType,
        SourceLocation sourceLocation)
    {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.sourceLocation = sourceLocation;
    }

    public static ASTMethod fromNode(MethodDeclaration node) {

        String name = node.getId().getString();
        if (node.isAnonymousInitializationCode()) {
            name = STATIC_INIT_ID;
        } else if (node.isConstructor()) {
            name = CONSTRUCTOR_ID;
        }

        return new ASTMethod(
            name,
            node.getParameterDeclarations().stream()
                .map(p -> caseNormalizedTypeIfPrimitive(p.getType().asCodeString()))
                .collect(Collectors.toList()),
            caseNormalizedTypeIfPrimitive(node.getReturnType().asCodeString()),
            node.getSourceLocation());
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        setLineNumbers(sourceLocation);
    }

    @Override
    public boolean hasRealLoc() {
        return !sourceLocation.isUnknown();
    }

    @Override
    public String getLocation() {
        if (hasRealLoc()) {
            return String.valueOf(sourceLocation);
        } else {
            return "no location";
        }
    }

    @Override
    public String getImage() {
        if (isConstructor()) {
            ApexRootNode<?> rootNode = getFirstParentOfType(ApexRootNode.class);
            if (rootNode != null) {
                return rootNode.node.getId().getString();
            }
        }
        return getCanonicalName();
    }

    public String getCanonicalName() {
        if (getParent() instanceof ASTProperty) {
            return ASTProperty.formatAccessorName((ASTProperty) getParent());
        }
        return name;
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
        return name.equals(CONSTRUCTOR_ID);
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    /**
     * Returns the method return type name.
     *
     * This includes any type arguments.
     * If the type is a primitive, its case will be normalized.
     */
    public String getReturnType() {
        return returnType;
    }

    public int getArity() {
        return parameterTypes.size();
    }
}
