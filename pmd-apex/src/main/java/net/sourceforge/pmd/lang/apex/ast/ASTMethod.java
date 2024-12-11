/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextPos2d;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

import com.google.summit.ast.SourceLocation;
import com.google.summit.ast.declaration.MethodDeclaration;

public final class ASTMethod extends AbstractApexNode implements ApexQualifiableNode {
    /**
     * Internal name used by constructors.
     * @see #isConstructor()
     */
    private static final String CONSTRUCTOR_ID = "<init>";

    /**
     * Internal name used by static initialization blocks.
     * @see #isStaticInitializer()
     */
    private static final String STATIC_INIT_ID = "<clinit>";

    /**
     * Internal name used by the synthetic trigger method.
     * @see #isTriggerBlock()
     */
    private static final String TRIGGER_INVOKE_ID = "<invoke>";

    // Store the details instead of wrapping a com.google.summit.ast.Node.
    // This is to allow synthetic ASTMethod nodes.
    // An example is the trigger `invoke` method.
    private final String name;
    private final String internalName;
    private final List<String> parameterTypes;
    private final String returnType;
    private final SourceLocation sourceLocation;

    ASTMethod(
        String name,
        String internalName,
        List<String> parameterTypes,
        String returnType,
        SourceLocation sourceLocation) {

        this.name = name;
        this.internalName = internalName;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.sourceLocation = sourceLocation;
    }

    static ASTMethod fromNode(MethodDeclaration node) {

        String name = node.getId().getString();
        String internalName = name;
        if (node.isAnonymousInitializationCode()) {
            internalName = STATIC_INIT_ID;
        } else if (node.isConstructor()) {
            internalName = CONSTRUCTOR_ID;
        }

        return new ASTMethod(
            name,
            internalName,
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
    void calculateTextRegion(TextDocument sourceCode) {
        if (sourceLocation.isUnknown()) {
            return;
        }
        // Column+1 because Summit columns are 0-based and PMD are 1-based
        setRegion(TextRegion.fromBothOffsets(
                sourceCode.offsetAtLineColumn(
                        TextPos2d.pos2d(sourceLocation.getStartLine(), sourceLocation.getStartColumn() + 1)),
                sourceCode.offsetAtLineColumn(
                        TextPos2d.pos2d(sourceLocation.getEndLine(), sourceLocation.getEndColumn() + 1))
        ));
    }

    @Override
    public boolean hasRealLoc() {
        return !sourceLocation.isUnknown();
    }

    @Override
    public String getImage() {
        if (isConstructor()) {
            BaseApexClass<?> baseClassNode = ancestors(BaseApexClass.class).first();
            if (baseClassNode != null) {
                return baseClassNode.getSimpleName();
            }
        }
        return getCanonicalName();
    }

    public String getCanonicalName() {
        if (getParent() instanceof ASTProperty) {
            return ASTProperty.formatAccessorName((ASTProperty) getParent());
        }

        if (isConstructor()) {
            return CONSTRUCTOR_ID;
        } else if (isStaticInitializer()) {
            return STATIC_INIT_ID;
        }

        return name;
    }

    @Override
    public ApexQualifiedName getQualifiedName() {
        return ApexQualifiedName.ofMethod(this);
    }

    public boolean isConstructor() {
        return CONSTRUCTOR_ID.equals(internalName);
    }

    public boolean isStaticInitializer() {
        return STATIC_INIT_ID.equals(internalName);
    }

    public ASTModifierNode getModifiers() {
        return firstChild(ASTModifierNode.class);
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

    /**
     * Checks whether this method is the synthetic trigger method.
     * @return true if this method is the synthetic trigger method
     * @since 7.5.0
     */
    @NoAttribute
    public boolean isTriggerBlock() {
        return TRIGGER_INVOKE_ID.equals(internalName);
    }
}
