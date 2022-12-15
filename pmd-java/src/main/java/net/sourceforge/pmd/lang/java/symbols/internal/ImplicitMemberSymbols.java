/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
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
            TypeSystem::declaration,
            singletonList(t -> new FakeFormalParamSym(t, "name", (ts, s) -> ts.declaration(ts.getClassSymbol(String.class))))
        );
    }

    public static JMethodSymbol enumValues(JClassSymbol enumSym) {
        assert enumSym.isEnum() : "Not an enum symbol " + enumSym;

        return new FakeMethodSym(
            enumSym,
            "values",
            Modifier.PUBLIC | Modifier.STATIC,
            (ts, c) -> ts.arrayType(ts.declaration(c)),
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
            TypeSystem::declaration,
            emptyList()
        );
    }

    public static JConstructorSymbol arrayConstructor(JClassSymbol arraySym) {
        assert arraySym.isArray() : "Not an array symbol " + arraySym;

        return new FakeCtorSym(
            arraySym,
            Modifier.PUBLIC | Modifier.FINAL,
            singletonList(c -> new FakeFormalParamSym(c, "arg0", (ts, sym) -> ts.INT))
        );
    }

    /** Symbol for the canonical record constructor. */
    public static JConstructorSymbol recordConstructor(JClassSymbol recordSym,
                                                       List<JFieldSymbol> recordComponents,
                                                       boolean isVarargs) {
        assert recordSym.isRecord() : "Not a record symbol " + recordSym;

        int modifiers = isVarargs ? Modifier.PUBLIC | VARARGS_MOD
                                  : Modifier.PUBLIC;

        return new FakeCtorSym(
            recordSym,
            modifiers,
            CollectionUtil.map(
                recordComponents,
                f -> c -> new FakeFormalParamSym(c, f.getSimpleName(), f.tryGetNode(), (ts, sym) -> f.getTypeMirror(Substitution.EMPTY))
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
            (ts, encl) -> recordComponent.getTypeMirror(Substitution.EMPTY),
            emptyList()
        );
    }

    public static JFieldSymbol arrayLengthField(JClassSymbol arraySym) {
        assert arraySym.isArray() : "Not an array symbol " + arraySym;

        return new FakeFieldSym(
            arraySym,
            "length",
            Modifier.PUBLIC | Modifier.FINAL,
            (ts, s) -> ts.INT
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
        public TypeSystem getTypeSystem() {
            return owner.getTypeSystem();
        }

        @Override
        public List<JTypeMirror> getFormalParameterTypes(Substitution subst) {
            return CollectionUtil.map(formals, p -> p.getTypeMirror(subst));
        }

        @Override
        public List<JTypeMirror> getThrownExceptionTypes(Substitution subst) {
            return emptyList();
        }

        @Override
        public List<JTypeVar> getTypeParameters() {
            return emptyList();
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
        public @Nullable JTypeMirror getAnnotatedReceiverType(Substitution subst) {
            if (!this.hasReceiver()) {
                return null;
            }
            return getTypeSystem().declaration(owner).subst(subst);
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
        public String toString() {
            return SymbolToStrings.FAKE.toString(this);
        }
    }

    private static final class FakeMethodSym extends FakeExecutableSymBase<JMethodSymbol> implements JMethodSymbol {

        private final BiFunction<? super TypeSystem, ? super JClassSymbol, ? extends JTypeMirror> returnType;

        FakeMethodSym(JClassSymbol owner,
                      String name,
                      int modifiers,
                      BiFunction<? super TypeSystem, ? super JClassSymbol, ? extends JTypeMirror> returnType,
                      List<Function<JMethodSymbol, JFormalParamSymbol>> formals) {
            super(owner, name, modifiers, formals);
            this.returnType = returnType;
        }

        @Override
        public boolean isBridge() {
            return false;
        }

        @Override
        public JTypeMirror getReturnType(Substitution subst) {
            return returnType.apply(getTypeSystem(), getEnclosingClass());
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
        private final ASTVariableDeclaratorId node;
        private final BiFunction<? super TypeSystem, ? super JFormalParamSymbol, ? extends JTypeMirror> type;

        private FakeFormalParamSym(JExecutableSymbol owner, String name, BiFunction<? super TypeSystem, ? super JFormalParamSymbol, ? extends JTypeMirror> type) {
            this(owner, name, null, type);
        }

        private FakeFormalParamSym(JExecutableSymbol owner, String name, @Nullable ASTVariableDeclaratorId node, BiFunction<? super TypeSystem, ? super JFormalParamSymbol, ? extends JTypeMirror> type) {
            this.owner = owner;
            this.name = name;
            this.node = node;
            this.type = type;
        }

        @Override
        public @Nullable ASTVariableDeclaratorId tryGetNode() {
            return node;
        }

        @Override
        public TypeSystem getTypeSystem() {
            return owner.getTypeSystem();
        }

        @Override
        public JTypeMirror getTypeMirror(Substitution subst) {
            return type.apply(getTypeSystem(), this).subst(subst);
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
        private final BiFunction<? super TypeSystem, ? super JClassSymbol, ? extends JTypeMirror> type;

        FakeFieldSym(JClassSymbol owner, String name, int modifiers, BiFunction<? super TypeSystem, ? super JClassSymbol, ? extends JTypeMirror> type) {
            this.owner = owner;
            this.name = name;
            this.modifiers = modifiers;
            this.type = type;
        }

        @Override
        public TypeSystem getTypeSystem() {
            return owner.getTypeSystem();
        }

        @Override
        public JTypeMirror getTypeMirror(Substitution subst) {
            return type.apply(getTypeSystem(), owner).subst(subst);
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
