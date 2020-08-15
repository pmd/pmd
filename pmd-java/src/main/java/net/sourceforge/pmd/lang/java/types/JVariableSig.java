/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Objects;

import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;

/**
 * Represents a {@link JVariableSymbol value symbol} viewed in the context
 * of a particular program point (ie under a particular {@link Substitution}).
 *
 * <p>The type given to a symbol is context-dependent. For example,
 * when looking in the supertypes of the current class for a field, we
 * have:
 * <pre>{@code
 *
 * abstract class Sup<K> {
 *      K field;
 *
 *      // In this scope the type of `field` is `K`, an abstract type variable
 *
 * }
 *
 *
 * class Foo extends Sup<Integer> {
 *
 *      {
 *          // in this scope, the type of `super.field` is `Integer`, not `K`,
 *          // because we inherit `Sup` where `K` is substituted with `Integer`.
 *          // `K` is not even in scope.
 *
 *          super.field = 2;
 *      }
 *
 * }
 *
 * }</pre>
 *
 * <p>This interface plays a similar role to {@link JMethodSig}. It is
 * the type of search results of a {@link JSymbolTable}, see
 * {@link JSymbolTable#variables()}.
 */
public class JVariableSig {

    private final JVariableSymbol sym;
    private final JTypeMirror declarator;

    private JVariableSig(JTypeMirror declarator, JVariableSymbol sym) {
        assert sym != null;
        assert declarator != null;
        this.sym = sym;
        this.declarator = declarator;
    }

    /**
     * This is the substituted type. Eg in the example of the class javadoc,
     * for {@code super.field}, this would be {@code Sup<Integer>}. For local
     * variables, this is always the generic type declaration of the enclosing
     * type.
     */
    // mm so this thing is useless except for FieldSig
    // Looks weird to me. Also getTypeMirror is captured by LazyTypeResolver
    // but not this one.
    public JTypeMirror getDeclaringType() {
        return declarator;
    }


    /**
     * Returns the symbol for this variable.
     */
    public JVariableSymbol getSymbol() {
        return sym;
    }


    /**
     * Returns the type given to the symbol in the particular scope this
     * signature is valid in.
     */
    public JTypeMirror getTypeMirror() {
        Substitution subst = declarator instanceof JClassType
                             ? ((JClassType) declarator).getTypeParamSubst()
                             : Substitution.EMPTY; // array

        return declarator.isRaw() ? ClassTypeImpl.eraseToRaw(sym.getTypeMirror(Substitution.EMPTY), subst)
                                  : sym.getTypeMirror(subst);
    }

    static JVariableSig.FieldSig forField(JTypeMirror declarator, JFieldSymbol sym) {
        return new JVariableSig.FieldSig(declarator, sym);
    }

    static JVariableSig forLocal(JClassType declarator, JVariableSymbol sym) {
        return new JVariableSig(declarator, sym);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JVariableSig) || o.getClass() != this.getClass()) {
            return false;
        }
        JVariableSig that = (JVariableSig) o;
        return Objects.equals(sym, that.sym)
            && Objects.equals(declarator, that.declarator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sym, declarator);
    }


    /**
     * A field signature.
     */
    public static final class FieldSig extends JVariableSig {

        FieldSig(JTypeMirror declarator, JFieldSymbol sym) {
            super(declarator, sym);
        }

        @Override
        public JFieldSymbol getSymbol() {
            return (JFieldSymbol) super.sym;
        }
    }

}
