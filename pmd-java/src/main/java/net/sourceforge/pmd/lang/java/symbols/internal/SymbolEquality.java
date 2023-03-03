/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.Objects;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolVisitor;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * Routines to share logic for equality, respecting the contract of
 * {@link JElementSymbol#equals(Object)}.
 *
 * <p>Despite this two equal symbols may not hold the same amount of
 * information... Reflection symbols are nice, but they also add some
 * synthetic stuff (eg implicit formal parameters, bridge methods),
 * which we must either filter-out or replicate in AST symbols. This is TODO
 */
@SuppressWarnings("PMD.CompareObjectsWithEquals")
public final class SymbolEquality {

    private SymbolEquality() {
        // util class
    }

    public static final EqAndHash<JTypeParameterSymbol> TYPE_PARAM = new EqAndHash<JTypeParameterSymbol>() {
        @Override
        public int hash(JTypeParameterSymbol t1) {
            return Objects.hash(t1.getDeclaringSymbol(), t1.getSimpleName());
        }

        @Override
        public boolean equals(JTypeParameterSymbol m1, Object o) {
            if (m1 == o) {
                return true;
            }
            if (!(o instanceof JTypeParameterSymbol)) {
                return false;
            }
            JTypeParameterSymbol m2 = (JTypeParameterSymbol) o;

            return m1.nameEquals(m2.getSimpleName())
                && m1.getDeclaringSymbol().equals(m2.getDeclaringSymbol());
        }
    };

    public static final EqAndHash<JMethodSymbol> METHOD = new EqAndHash<JMethodSymbol>() {
        @Override
        public int hash(JMethodSymbol t1) {
            return 0;
        }

        @Override
        public boolean equals(JMethodSymbol m1, Object o) {
            if (m1 == o) {
                return true;
            }
            if (!(o instanceof JMethodSymbol)) {
                return false;
            }
            JMethodSymbol m2 = (JMethodSymbol) o;

            // FIXME arity check is not enough for overloads
            return m1.getModifiers() == m2.getModifiers()
                && m1.getArity() == m2.getArity()
                && Objects.equals(m1.getSimpleName(), m2.getSimpleName())
                && m1.getEnclosingClass().equals(m2.getEnclosingClass());
        }
    };

    public static final EqAndHash<JConstructorSymbol> CONSTRUCTOR = new EqAndHash<JConstructorSymbol>() {
        @Override
        public int hash(JConstructorSymbol t1) {
            return 0;
        }

        @Override
        public boolean equals(JConstructorSymbol m1, Object o) {
            if (m1 == o) {
                return true;
            }
            if (!(o instanceof JConstructorSymbol)) {
                return false;
            }
            JConstructorSymbol m2 = (JConstructorSymbol) o;

            // FIXME arity check is not enough for overloads
            return m1.getModifiers() == m2.getModifiers()
                && m1.getArity() == m2.getArity()
                && Objects.equals(m1.getSimpleName(), m2.getSimpleName())
                && m1.getEnclosingClass().equals(m2.getEnclosingClass());
        }
    };


    public static final EqAndHash<JClassSymbol> CLASS = new EqAndHash<JClassSymbol>() {
        @Override
        public int hash(JClassSymbol t1) {
            return t1.getBinaryName().hashCode();
        }

        @Override
        public boolean equals(JClassSymbol m1, Object o) {
            if (m1 == o) {
                return true;
            }
            if (!(o instanceof JClassSymbol)) {
                return false;
            }
            JClassSymbol m2 = (JClassSymbol) o;

            return m1.getBinaryName().equals(m2.getBinaryName());
        }
    };

    public static final EqAndHash<JFieldSymbol> FIELD = new EqAndHash<JFieldSymbol>() {
        @Override
        public int hash(JFieldSymbol t1) {
            return Objects.hash(t1.getEnclosingClass(), t1.getSimpleName());
        }

        @Override
        public boolean equals(JFieldSymbol f1, Object o) {
            if (!(o instanceof JFieldSymbol)) {
                return false;
            }
            JFieldSymbol f2 = (JFieldSymbol) o;
            return f1.nameEquals(f2.getSimpleName())
                && f1.getEnclosingClass().equals(f2.getEnclosingClass());

        }
    };

    public static final EqAndHash<SymAnnot> ANNOTATION = new EqAndHash<SymAnnot>() {
        @Override
        public int hash(SymAnnot t1) {
            return Objects.hash(t1.getBinaryName());
        }

        @Override
        public boolean equals(SymAnnot f1, Object o) {
            if (!(o instanceof SymAnnot)) {
                return false;
            }
            SymAnnot f2 = (SymAnnot) o;
            return f1.getBinaryName().equals(f2.getBinaryName());

        }
    };

    public static final EqAndHash<JFormalParamSymbol> FORMAL_PARAM = new EqAndHash<JFormalParamSymbol>() {
        @Override
        public int hash(JFormalParamSymbol t1) {
            return Objects.hash(t1.getDeclaringSymbol(), t1.getSimpleName());
        }

        @Override
        public boolean equals(JFormalParamSymbol f1, Object o) {
            if (!(o instanceof JFormalParamSymbol)) {
                return false;
            }
            JFormalParamSymbol f2 = (JFormalParamSymbol) o;
            return f1.nameEquals(f2.getSimpleName())
                && f1.getDeclaringSymbol().equals(f2.getDeclaringSymbol());

        }
    };

    private static final EqAndHash<Object> IDENTITY = new EqAndHash<Object>() {
        @Override
        public int hash(Object t1) {
            return System.identityHashCode(t1);
        }

        @Override
        public boolean equals(Object t1, Object t2) {
            return t1 == t2;
        }
    };

    /**
     * Strategy to perform equals/hashcode for a type T. There are libraries
     * for that, whatever.
     */
    public abstract static class EqAndHash<T> {

        public abstract int hash(T t1);


        public abstract boolean equals(T t1, Object t2);

    }

    public static <T extends JElementSymbol> boolean equals(T e1, Object e2) {
        if (e1 == e2) {
            return true;
        } else if (e2 == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        EqAndHash<T> eqAndHash = (EqAndHash<T>) e1.acceptVisitor(EqAndHashVisitor.INSTANCE, null);
        return eqAndHash.equals(e1, e2);
    }

    public static <T extends JElementSymbol> int hash(T e1) {
        @SuppressWarnings("unchecked")
        EqAndHash<T> eqAndHash = (EqAndHash<T>) e1.acceptVisitor(EqAndHashVisitor.INSTANCE, null);
        return eqAndHash.hash(e1);
    }


    private static final class EqAndHashVisitor implements SymbolVisitor<EqAndHash<?>, Void> {

        static final EqAndHashVisitor INSTANCE = new EqAndHashVisitor();

        @Override
        public EqAndHash<?> visitSymbol(JElementSymbol sym, Void aVoid) {
            throw new IllegalStateException("Unknown symbol " + sym.getClass());
        }

        @Override
        public EqAndHash<?> visitClass(JClassSymbol sym, Void param) {
            return CLASS;
        }

        @Override
        public EqAndHash<?> visitTypeParam(JTypeParameterSymbol sym, Void param) {
            return TYPE_PARAM;
        }

        @Override
        public EqAndHash<?> visitCtor(JConstructorSymbol sym, Void param) {
            return CONSTRUCTOR;
        }

        @Override
        public EqAndHash<?> visitMethod(JMethodSymbol sym, Void param) {
            return METHOD;
        }

        @Override
        public EqAndHash<?> visitField(JFieldSymbol sym, Void param) {
            return FIELD;
        }

        @Override
        public EqAndHash<?> visitLocal(JLocalVariableSymbol sym, Void param) {
            return IDENTITY;
        }

        @Override
        public EqAndHash<?> visitFormal(JFormalParamSymbol sym, Void param) {
            return FORMAL_PARAM;
        }
    }


}
