/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Members inserted by the compiler, eg default constructor, etc. They
 * would be absent from the source, but are reflected by the {@link Class}.
 */
public final class ImplicitMemberSymbols {

    private static final int VISIBILITY_MASK = Modifier.PRIVATE | Modifier.PUBLIC | Modifier.PROTECTED;
    /** This is the private access flag for varargs modifiers. */
    private static final int VARARGS_MOD = 0x00000080;

    private ImplicitMemberSymbols() {

    }

    public static JMethodSymbol enumValueOf(JClassSymbol enumSym) {
        assert enumSym.isEnum() : "Not an enum symbol " + enumSym;

        return new FakeMethodSym(
            enumSym,
            "valueOf",
            Modifier.PUBLIC | Modifier.STATIC,
            singletonList(t -> new FakeFormalParamSym(t, "name"))
        );
    }

    public static JMethodSymbol enumValues(JClassSymbol enumSym) {
        assert enumSym.isEnum() : "Not an enum symbol " + enumSym;

        return new FakeMethodSym(
            enumSym,
            "values",
            Modifier.PUBLIC | Modifier.STATIC,
            emptyList()
        );
    }


    public static JConstructorSymbol defaultCtor(JClassSymbol sym) {
        assert sym != null;

        // Enum constructors have 2 additional implicit parameters, for the name and ordinal
        // Inner classes have 1 additional implicit param, for the outer instance
        // They are not reflected by the symbol

        int modifiers = sym.isEnum() ? Modifier.PRIVATE
                                     : sym.getModifiers() & VISIBILITY_MASK;

        return new FakeCtorSym(sym, modifiers, emptyList());
    }


    public static JMethodSymbol arrayClone(JClassSymbol arraySym) {
        assert arraySym.isArray() : "Not an array symbol " + arraySym;

        return new FakeMethodSym(
            arraySym,
            "clone",
            Modifier.PUBLIC | Modifier.FINAL,
            emptyList()
        );
    }

    public static JConstructorSymbol arrayConstructor(JClassSymbol arraySym) {
        assert arraySym.isArray() : "Not an array symbol " + arraySym;

        return new FakeCtorSym(
            arraySym,
            Modifier.PUBLIC | Modifier.FINAL,
            singletonList(c -> new FakeFormalParamSym(c, "arg0"))
        );
    }

    /** Symbol for the canonical record constructor. */
    public static JConstructorSymbol recordConstructor(JClassSymbol recordSym,
                                                       List<JFieldSymbol> recordComponents,
                                                       boolean isVarargs) {
        assert recordSym.isRecord() : "Not a record symbol " + recordSym;

        int modifiers = recordSym.getModifiers() & VISIBILITY_MASK;
        if (isVarargs) {
            modifiers |= VARARGS_MOD;
        }

        return new FakeCtorSym(
            recordSym,
            modifiers,
            CollectionUtil.map(
                recordComponents,
                f -> c -> new FakeFormalParamSym(c, f.getSimpleName())
            )
        );
    }

    /**
     * Symbol for a record component accessor.
     * Only synthesized if it is not explicitly declared.
     */
    public static JMethodSymbol recordAccessor(JClassSymbol recordSym, JFieldSymbol recordComponent) {
        // See https://cr.openjdk.java.net/~gbierman/jep359/jep359-20200115/specs/records-jls.html#jls-8.10.3

        assert recordSym.isRecord() : "Not a record symbol " + recordSym;

        return new FakeMethodSym(
            recordSym,
            recordComponent.getSimpleName(),
            Modifier.PUBLIC,
            emptyList()
        );
    }

    public static JFieldSymbol arrayLengthField(JClassSymbol arraySym) {
        assert arraySym.isArray() : "Not an array symbol " + arraySym;

        return new FakeFieldSym(
            arraySym,
            "length",
            Modifier.PUBLIC | Modifier.FINAL
        );
    }

    private abstract static class FakeExecutableSymBase<T extends JExecutableSymbol> implements JExecutableSymbol {

        private final JClassSymbol owner;
        private final String name;
        private final int modifiers;
        private final List<JFormalParamSymbol> formals;

        FakeExecutableSymBase(JClassSymbol owner,
                              String name,
                              int modifiers,
                              List<Function<T, JFormalParamSymbol>> formals) {
            this.owner = owner;
            this.name = name;
            this.modifiers = modifiers;
            this.formals = CollectionUtil.map(formals, f -> f.apply((T) this));
        }

        @Override
        public String getSimpleName() {
            return name;
        }

        @Override
        public List<JFormalParamSymbol> getFormalParameters() {
            return formals;
        }

        @Override
        public boolean isVarargs() {
            return (modifiers & VARARGS_MOD) != 0;
        }

        @Override
        public int getArity() {
            return formals.size();
        }

        @Override
        public int getModifiers() {
            return modifiers;
        }

        @Override
        public @NonNull JClassSymbol getEnclosingClass() {
            return owner;
        }

        @Override
        public List<JTypeParameterSymbol> getTypeParameters() {
            return emptyList();
        }


        @Override
        public String toString() {
            return SymbolToStrings.FAKE.toString(this);
        }
    }

    private static final class FakeMethodSym extends FakeExecutableSymBase<JMethodSymbol> implements JMethodSymbol {

        FakeMethodSym(JClassSymbol owner,
                      String name,
                      int modifiers,
                      List<Function<JMethodSymbol, JFormalParamSymbol>> formals) {
            super(owner, name, modifiers, formals);
        }

        @Override
        public boolean equals(Object o) {
            return SymbolEquality.METHOD.equals(this, o);
        }

        @Override
        public int hashCode() {
            return SymbolEquality.METHOD.hash(this);
        }
    }

    private static final class FakeCtorSym extends FakeExecutableSymBase<JConstructorSymbol> implements JConstructorSymbol {

        FakeCtorSym(JClassSymbol owner,
                    int modifiers,
                    List<Function<JConstructorSymbol, JFormalParamSymbol>> formals) {
            super(owner, JConstructorSymbol.CTOR_NAME, modifiers, formals);
        }

        @Override
        public boolean equals(Object o) {
            return SymbolEquality.CONSTRUCTOR.equals(this, o);
        }

        @Override
        public int hashCode() {
            return SymbolEquality.CONSTRUCTOR.hash(this);
        }
    }

    private static final class FakeFormalParamSym implements JFormalParamSymbol {

        private final JExecutableSymbol owner;
        private final String name;

        private FakeFormalParamSym(JExecutableSymbol owner, String name) {
            this.owner = owner;
            this.name = name;
        }

        @Override
        public JExecutableSymbol getDeclaringSymbol() {
            return owner;
        }

        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public String getSimpleName() {
            return name;
        }

        @Override
        public String toString() {
            return SymbolToStrings.FAKE.toString(this);
        }

        @Override
        public boolean equals(Object o) {
            return SymbolEquality.FORMAL_PARAM.equals(this, o);
        }

        @Override
        public int hashCode() {
            return SymbolEquality.FORMAL_PARAM.hash(this);
        }
    }


    private static final class FakeFieldSym implements JFieldSymbol {

        private final JClassSymbol owner;
        private final String name;
        private final int modifiers;

        FakeFieldSym(JClassSymbol owner, String name, int modifiers) {
            this.owner = owner;
            this.name = name;
            this.modifiers = modifiers;
        }

        @Override
        public String getSimpleName() {
            return name;
        }

        @Override
        public int getModifiers() {
            return modifiers;
        }

        @Override
        public boolean isEnumConstant() {
            return false;
        }

        @Override
        public @NonNull JClassSymbol getEnclosingClass() {
            return owner;
        }

        @Override
        public String toString() {
            return SymbolToStrings.FAKE.toString(this);
        }

        @Override
        public boolean equals(Object o) {
            return SymbolEquality.FIELD.equals(this, o);
        }

        @Override
        public int hashCode() {
            return SymbolEquality.FIELD.hash(this);
        }
    }

}
