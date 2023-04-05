/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.document.TextFileContent;

import com.google.summit.ast.SourceLocation;
import com.google.summit.ast.declaration.MethodDeclaration;

public final class ASTMethod extends AbstractApexNode implements ApexQualifiableNode {

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
        SourceLocation sourceLocation) {

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
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    void calculateTextRegion(TextFileContent sourceContent) {
        // TODO post-merge setLineNumbers(sourceLocation);
    }

    @Override
    public boolean hasRealLoc() {
        return !sourceLocation.isUnknown();
    }

    @Override
    public String getImage() {
        if (isConstructor()) {
            ASTUserClass classNode = getFirstParentOfType(ASTUserClass.class);
            if (classNode != null) {
                return classNode.getSimpleName();
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
    public ApexQualifiedName getQualifiedName() {
        return ApexQualifiedName.ofMethod(this);
    }

    /**
     * Returns true if this is a synthetic class initializer, inserted
     * by the parser.
     */
    public boolean isSynthetic() {
        return getImage().matches("<clinit>|<init>|clone");
    }
    
    public boolean isConstructor() {
        return CONSTRUCTOR_ID.equals(name);
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
