/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.lang.annotation.ElementType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.ast.SymbolResolutionPass;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaResolvers;
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

        return fromAstImpl(ts, lexicalSubst, node);
    }

    private static JTypeMirror fromAstImpl(TypeSystem ts, Substitution lexicalSubst, ASTType node) {

        if (node instanceof ASTClassOrInterfaceType) {

            return makeFromClassType(ts, (ASTClassOrInterfaceType) node, lexicalSubst);

        } else if (node instanceof ASTWildcardType) {


            ASTWildcardType wild = (ASTWildcardType) node;
            @Nullable JTypeMirror bound = fromAst(ts, lexicalSubst, wild.getTypeBoundNode());
            if (bound == null) {
                bound = ts.OBJECT;
            }
            return ts.wildcard(wild.isUpperBound(), bound).withAnnotations(getTypeAnnotations(node));


        } else if (node instanceof ASTIntersectionType) {

            List<JTypeMirror> components = new ArrayList<>();
            for (ASTType t : (ASTIntersectionType) node) {
                components.add(fromAst(ts, lexicalSubst, t));
            }

            try {
                return ts.glb(components);
            } catch (IllegalArgumentException e) {
                return ts.ERROR;
            }
        } else if (node instanceof ASTArrayType) {

            JTypeMirror t = fromAst(ts, lexicalSubst, ((ASTArrayType) node).getElementType());
            ASTArrayDimensions dimensions = ((ASTArrayType) node).getDimensions();
            // we have to iterate in reverse
            for (int i = dimensions.size() - 1; i >= 0; i--) {
                ASTArrayTypeDim dim = dimensions.get(i);
                PSet<SymAnnot> annots = getSymbolicAnnotations(dim);
                t = ts.arrayType(t).withAnnotations(annots);
            }

            return t;

        } else if (node instanceof ASTPrimitiveType) {

            return ts.getPrimitive(((ASTPrimitiveType) node).getKind()).withAnnotations(getTypeAnnotations(node));

        } else if (node instanceof ASTAmbiguousName) {

            return ts.UNKNOWN;

        } else if (node instanceof ASTUnionType) {

            return ts.lub(CollectionUtil.map(((ASTUnionType) node).getComponents(), TypeNode::getTypeMirror));

        } else if (node instanceof ASTVoidType) {

            return ts.NO_TYPE;

        }

        throw new IllegalStateException("Illegal type " + node.getClass() + " " + node);
    }


    private static PSet<SymAnnot> getSymbolicAnnotations(Annotatable dim) {
        return SymbolResolutionPass.buildSymbolicAnnotations(dim.getDeclaredAnnotations());
    }


    private static JTypeMirror makeFromClassType(TypeSystem ts, ASTClassOrInterfaceType node, Substitution subst) {
        if (node == null) {
            return null;
        }

        // TODO error handling, what if we're saying List<String, Int> in source: should be caught before

        PSet<SymAnnot> typeAnnots = getTypeAnnotations(node);
        JTypeDeclSymbol reference = getReferenceEnsureResolved(node);

        if (reference instanceof JTypeParameterSymbol) {
            return subst.apply(((JTypeParameterSymbol) reference).getTypeMirror()).withAnnotations(typeAnnots);
        }

        JClassType enclosing = getEnclosing(ts, node, subst, node.getQualifier(), reference);

        ASTTypeArguments typeArguments = node.getTypeArguments();

        List<JTypeMirror> boundGenerics = Collections.emptyList();
        if (typeArguments != null) {
            if (!typeArguments.isDiamond()) {
                boundGenerics = new ArrayList<>(typeArguments.getNumChildren());
                for (ASTType t : typeArguments) {
                    boundGenerics.add(fromAst(ts, subst, t));
                }
            }
            // fallthrough, this will be set to the raw type (with the correct enclosing type)
            // until the constructor call is fully type resolved
        }

        if (enclosing != null) {
            return enclosing.selectInner((JClassSymbol) reference, boundGenerics, typeAnnots);
        } else {
            return ts.parameterise((JClassSymbol) reference, boundGenerics).withAnnotations(typeAnnots);
        }
    }

    private static @Nullable JClassType getEnclosing(TypeSystem ts, ASTClassOrInterfaceType node, Substitution subst, @Nullable ASTClassOrInterfaceType lhsType, JTypeDeclSymbol reference) {
        @Nullable JTypeMirror enclosing = makeFromClassType(ts, lhsType, subst);

        if (enclosing != null && !shouldEnclose(reference)) {
            // It's possible to write Map.Entry<A,B> but Entry is a static type,
            // so we should ignore the "enclosing" Map
            enclosing = null;
        } else if (enclosing == null && needsEnclosing(reference)) {
            // class Foo<T> {
            //      class Inner {}
            //      void bar(Inner k) {}
            //               ^^^^^
            //               This is shorthand for Foo<T>.Inner (because of regular scoping rules)
            // }
            enclosing = node.getImplicitEnclosing();
            assert enclosing != null : "Implicit enclosing type should have been set by disambiguation, for " + node;
        }

        if (enclosing != null) {
            // the actual enclosing type may be a supertype of the one that was explicitly written
            // (Sub <: Sup) => (Sub.Inner = Sup.Inner)
            // We normalize them to the actual declaring class
            JClassSymbol enclosingClassAccordingToReference = reference.getEnclosingClass();
            if (enclosingClassAccordingToReference == null) {
                return null;
            }
            enclosing = enclosing.getAsSuper(enclosingClassAccordingToReference);
            assert enclosing != null : "We got this symbol by looking into enclosing";
            return (JClassType) enclosing;
        }
        return null;
    }

    // Whether the reference needs an enclosing type if it is unqualified (non-static inner type)
    private static boolean needsEnclosing(JTypeDeclSymbol reference) {
        return reference instanceof JClassSymbol
            && reference.getEnclosingClass() != null
            && !Modifier.isStatic(reference.getModifiers());
    }

    private static @NonNull JTypeDeclSymbol getReferenceEnsureResolved(ASTClassOrInterfaceType node) {
        if (node.getReferencedSym() != null) {
            return node.getReferencedSym();
        } else if (node.getParent() instanceof ASTConstructorCall) {
            ASTExpression qualifier = ((ASTConstructorCall) node.getParent()).getQualifier();
            if (qualifier != null) {
                assert node.getImplicitEnclosing() == null
                    : "Qualified ctor calls should be handled lazily";
                // note: this triggers recursive type resolution of the qualifier
                JTypeMirror qualifierType = qualifier.getTypeMirror();
                JClassSymbol symbol;
                if (qualifierType instanceof JClassType) {
                    JClassType enclosing = (JClassType) qualifierType;
                    JClassType resolved = JavaResolvers.getMemberClassResolver(enclosing, node.getRoot().getPackageName(), node.getEnclosingType().getSymbol(), node.getSimpleName())
                                                       .resolveFirst(node.getSimpleName());
                    if (resolved == null) {
                        // compile-time error
                        symbol = (JClassSymbol) node.getTypeSystem().UNKNOWN.getSymbol();
                    } else {
                        symbol = resolved.getSymbol();
                        JClassType actualEnclosing = enclosing.getAsSuper(symbol.getEnclosingClass());
                        assert actualEnclosing != null : "We got this symbol by looking into enclosing";
                        node.setImplicitEnclosing(actualEnclosing);
                    }
                } else {
                    // qualifier is unresolved, compile-time error
                    symbol = (JClassSymbol) node.getTypeSystem().UNKNOWN.getSymbol();
                }
                node.setSymbol(symbol);
                return symbol;
            } // else fallthrough
        }
        throw new IllegalStateException("Disambiguation pass should resolve everything except qualified ctor calls");
    }

    // Whether the reference is a non-static inner type of the enclosing type
    // Note most checks have already been done in the disambiguation pass (including reporting)
    private static boolean shouldEnclose(JTypeDeclSymbol reference) {
        return !Modifier.isStatic(reference.getModifiers());
    }

    /**
     * Returns the variable declaration or field or formal, etc, that
     * may give additional type annotations to the given type.
     */
    private static @Nullable Annotatable getEnclosingAnnotationGiver(JavaNode node) {
        JavaNode parent = node.getParent();
        if (node.getIndexInParent() == 0 && parent instanceof ASTClassOrInterfaceType) {
            // this is an enclosing type
            return getEnclosingAnnotationGiver(parent);
        } else if (node.getIndexInParent() == 0 && parent instanceof ASTArrayType) {
            // the element type of an array type
            return getEnclosingAnnotationGiver(parent);
        } else if (!(parent instanceof ASTType) && parent instanceof ASTVariableDeclarator) {
            return getEnclosingAnnotationGiver(parent);
        } else if (!(parent instanceof ASTType) && parent instanceof Annotatable) {
            return (Annotatable) parent;
        }
        return null;
    }

    private static PSet<SymAnnot> getTypeAnnotations(ASTType type) {
        PSet<SymAnnot> annotsOnType = getSymbolicAnnotations(type);
        if (type instanceof ASTClassOrInterfaceType && ((ASTClassOrInterfaceType) type).getQualifier() != null) {
            return annotsOnType; // annots on the declaration only apply to the leftmost qualifier
        }
        Annotatable parent = getEnclosingAnnotationGiver(type);
        if (parent != null) {
            PSet<SymAnnot> parentAnnots = getSymbolicAnnotations(parent);
            for (SymAnnot parentAnnot : parentAnnots) {
                // filter annotations by whether they apply to the type use.
                if (parentAnnot.getAnnotationSymbol().annotationAppliesTo(ElementType.TYPE_USE)) {
                    annotsOnType = annotsOnType.plus(parentAnnot);
                }
            }
        }
        return annotsOnType;
    }
}
