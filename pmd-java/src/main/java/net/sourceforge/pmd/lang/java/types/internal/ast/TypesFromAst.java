/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.ast;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTArrayType;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTIntersectionType;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ASTUnionType;
import net.sourceforge.pmd.lang.java.ast.ASTWildcardType;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Builds type mirrors from AST nodes.
 */
final class TypesFromAst {

    private TypesFromAst() {
        // utility class
    }

    public static List<JTypeMirror> fromAst(TypeSystem ts, Substitution subst, List<ASTType> reflected) {
        return CollectionUtil.map(reflected, it -> fromAst(ts, subst, it));
    }

    /**
     * Builds a type from an AST node.
     *
     * @param lexicalSubst A substitution to apply to type variables
     * @param node         An ast node
     */
    public static JTypeMirror fromAst(TypeSystem ts, Substitution lexicalSubst, ASTType node) {
        if (node == null) {
            return null;
        }

        if (InternalApiBridge.getTypeMirrorInternal(node) == ts.UNRESOLVED_TYPE) {
            return ts.UNRESOLVED_TYPE;
        }

        try {
            return fromAstImpl(ts, lexicalSubst, node);
        } catch (Exception | AssertionError e) {
            InternalApiBridge.setTypeMirrorInternal(node, ts.UNRESOLVED_TYPE);
            throw new ContextedRuntimeException(e)
                .addContextValue("node:", node);
        }
    }

    public static JTypeMirror fromAstImpl(TypeSystem ts, Substitution lexicalSubst, ASTType node) {

        if (node instanceof ASTClassOrInterfaceType) {

            return makeFromClassType(ts, (ASTClassOrInterfaceType) node, lexicalSubst);

        } else if (node instanceof ASTWildcardType) {


            ASTWildcardType wild = (ASTWildcardType) node;
            @Nullable JTypeMirror bound = fromAst(ts, lexicalSubst, wild.getTypeBoundNode());
            return bound == null
                   ? ts.UNBOUNDED_WILD
                   : ts.wildcard(wild.hasUpperBound(), bound);


        } else if (node instanceof ASTIntersectionType) {

            List<JTypeMirror> components = new ArrayList<>();
            for (ASTType t : (ASTIntersectionType) node) {
                components.add(fromAst(ts, lexicalSubst, t));
            }

            return ts.intersect(components);

        } else if (node instanceof ASTArrayType) {

            JTypeMirror eltType = fromAst(ts, lexicalSubst, ((ASTArrayType) node).getElementType());

            return ts.arrayType(eltType, node.getArrayDepth());

        } else if (node instanceof ASTPrimitiveType) {

            return ts.getPrimitive(((ASTPrimitiveType) node).getKind());

        } else if (node instanceof ASTAmbiguousName) {

            return ts.UNRESOLVED_TYPE;

        } else if (node instanceof ASTUnionType) {

            return ts.lub(CollectionUtil.map(((ASTUnionType) node).getComponents(), TypeNode::getTypeMirror));

        }

        throw new IllegalStateException("Illegal type " + node.getClass() + " " + node);
    }

    private static JTypeMirror makeFromClassType(TypeSystem ts, ASTClassOrInterfaceType node, Substitution subst) {

        if (node == null) {
            return null;
        }

        // TODO error handling, what if we're saying List<String, Int> in source: should be caught before

        ASTClassOrInterfaceType lhsType = node.getQualifier();

        @Nullable JTypeMirror enclosing = makeFromClassType(ts, lhsType, subst);

        JTypeDeclSymbol reference = node.getReferencedSym();
        assert reference != null : "Null reference for " + node + " in " + node.getParent();

        if (enclosing != null
            && (!(enclosing instanceof JClassType)
            || Modifier.isStatic(reference.getModifiers()))) {
            // already reported elsewhere
            enclosing = null;
        }
        if (enclosing == null
            && reference instanceof JClassSymbol
            && reference.getEnclosingClass() != null
            && !Modifier.isStatic(reference.getModifiers())
        ) {
            // todo this should be ensured to happen only inside the body of
            // the declaration, otherwise type variables may leak
            enclosing = ts.declaration(reference.getEnclosingClass());
        }


        if (reference instanceof JTypeParameterSymbol) {
            return subst.apply(((JTypeParameterSymbol) reference).getTypeMirror());
        }

        ASTTypeArguments typeArguments = node.getFirstChildOfType(ASTTypeArguments.class);

        if (typeArguments != null && reference instanceof JClassSymbol) {
            if (typeArguments.isDiamond()) {
                return ts.declaration((JClassSymbol) reference); // a generic type declaration
            } else {
                final List<JTypeMirror> boundGenerics = new ArrayList<>(typeArguments.getNumChildren());
                for (ASTType t : typeArguments) {
                    boundGenerics.add(fromAst(ts, subst, t));
                }

                if (enclosing != null) {
                    return ((JClassType) enclosing).selectInner((JClassSymbol) reference, boundGenerics);
                } else {
                    return ts.parameterise((JClassSymbol) reference, boundGenerics);
                }
            }
        }

        if (enclosing != null) {
            return ((JClassType) enclosing).selectInner((JClassSymbol) reference, Collections.emptyList());
        } else {
            return ts.rawType(reference);
        }
    }
}
