/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.types.JIntersectionType;
import net.sourceforge.pmd.lang.java.types.TypeSystem;


/**
 * Formal parameter of a {@linkplain ASTCatchClause catch clause}
 * to represent the declared exception variable.
 *
 * <pre class="grammar">
 *
 * CatchParameter ::= {@link ASTModifierList LocalVarModifierList} {@link ASTType Type} {@link ASTVariableId VariableId}
 *
 * </pre>
 */
public final class ASTCatchParameter extends AbstractJavaNode
    implements InternalInterfaces.VariableIdOwner,
        ModifierOwner {

    ASTCatchParameter(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns true if this is a multi-catch parameter,
     * that is, it catches several unrelated exception types
     * at the same time. For example:
     *
     * <pre>catch (IllegalStateException | IllegalArgumentException e) {}</pre>
     */
    public boolean isMulticatch() {
        return getTypeNode() instanceof ASTUnionType;
    }

    @Override
    @NonNull
    public ASTVariableId getVarId() {
        return (ASTVariableId) getLastChild();
    }

    /** Returns the name of this parameter. */
    public String getName() {
        return getVarId().getName();
    }


    /**
     * Returns the type node of this catch parameter. May be a
     * {@link ASTUnionType UnionType}.
     */
    public ASTType getTypeNode() {
        return (ASTType) getChild(1);
    }


    /**
     * Returns a stream of all declared exception types (expanding a union
     * type if present).
     *
     * <p>Note that this is the only reliable way to inspect multi-catch clauses,
     * as the type mirror of a {@link ASTUnionType} is not itself a {@link JIntersectionType},
     * but the {@link TypeSystem#lub(Collection) LUB} of the components.
     * Since exception types cannot be interfaces, the LUB always erases
     * to a single class supertype (eg {@link RuntimeException}).
     */
    public NodeStream<ASTClassType> getAllExceptionTypes() {
        ASTType typeNode = getTypeNode();
        if (typeNode instanceof ASTUnionType) {
            return typeNode.children(ASTClassType.class);
        } else {
            return NodeStream.of((ASTClassType) typeNode);
        }
    }

    public boolean isFinal() {
        return hasModifiers(JModifier.FINAL);
    }
}
