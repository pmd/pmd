/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Optional;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextPos2d;
import net.sourceforge.pmd.lang.document.TextRegion;

import com.google.summit.ast.Identifier;
import com.google.summit.ast.Node;
import com.google.summit.ast.SourceLocation;
import com.google.summit.ast.TypeRef;
import com.google.summit.ast.declaration.EnumDeclaration;
import com.google.summit.ast.expression.Expression;
import com.google.summit.ast.expression.LiteralExpression;

public final class ASTField extends AbstractApexNode.Many<Node> {

    private final Identifier name;
    private final Optional<Expression> value;
    private final String typeName;

    ASTField(TypeRef typeRef, Identifier name, Optional<Expression> value) {
        super(value.isPresent()
              ? Arrays.asList(typeRef, name, value.get())
              : Arrays.asList(typeRef, name));
        this.name = name;
        this.value = value;
        this.typeName = caseNormalizedTypeIfPrimitive(typeRef.asCodeString());
    }

    ASTField(EnumDeclaration enumType, Identifier name) {
        super(Arrays.asList(enumType, name));
        this.name = name;
        this.value = Optional.empty();
        this.typeName = enumType.getId().asCodeString();
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return getName();
    }


    /**
     * Returns the type name.
     *
     * <p>This includes any type arguments. (This is tested.)
     * If the type is a primitive, its case will be normalized.
     */
    public String getType() {
        return typeName;
    }

    public ASTModifierNode getModifiers() {
        return firstChild(ASTModifierNode.class);
    }

    public String getName() {
        return name.getString();
    }

    public String getValue() {
        if (value.isPresent()) {
            if (value.get() instanceof LiteralExpression) {
                return literalToString((LiteralExpression) value.get());
            }
        }
        return null;
    }

    @Override
    public boolean hasRealLoc() {
        if (!(nodes.get(0) instanceof TypeRef)) {
            return super.hasRealLoc();
        }

        // only special case if the first child is a TypeRef - then we need to look deeper
        // TypeRef itself doesn't have a source location
        TypeRef typeRef = (TypeRef) nodes.get(0);
        boolean allHaveRealLoc = typeRef.getComponents().stream().noneMatch(c -> c.getId().getSourceLocation().isUnknown());
        // check the remaining nodes (name and optional value)
        for (int i = 1; i < nodes.size(); i++) {
            allHaveRealLoc &= !nodes.get(i).getSourceLocation().isUnknown();
        }
        return allHaveRealLoc;
    }

    @Override
    protected void calculateTextRegion(TextDocument sourceCode) {
        if (nodes.get(0) instanceof TypeRef) {
            super.calculateTextRegion(sourceCode);
        }

        // only special case this time: enum field. Then we need to ignore the first child node,
        // which represents the complete enum declaration. So we need to only look at the identifier,
        // if we have a location for that
        SourceLocation loc = name.getSourceLocation();
        if (loc.isUnknown()) {
            return;
        }

        // Column+1 because Summit columns are 0-based and PMD are 1-based
        setRegion(TextRegion.fromBothOffsets(
                sourceCode.offsetAtLineColumn(TextPos2d.pos2d(loc.getStartLine(), loc.getStartColumn() + 1)),
                sourceCode.offsetAtLineColumn(TextPos2d.pos2d(loc.getEndLine(), loc.getEndColumn() + 1))
        ));
    }
}
