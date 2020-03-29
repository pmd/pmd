/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.ClassResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.VarResolveResult;

/**
 * Fields, methods, member types declared in a type.
 */
final class TypeMemberSymTable extends AbstractSymbolTable {

    private final @NonNull JClassSymbol typeSym;
    private final JClassSymbol nestRoot;

    private final Map<String, List<JMethodSymbol>> methodResolveCache = new HashMap<>();
    private final Map<String, ResolveResult<JTypeDeclSymbol>> typeResolveCache = new HashMap<>();

    private final ASTAnyTypeDeclaration node;

    TypeMemberSymTable(JSymbolTable parent,
                       SymbolTableHelper helper,
                       ASTAnyTypeDeclaration node) {

        super(parent, helper);
        this.node = node;
        assert node != null : "Null type decl?";
        typeSym = node.getSymbol();
        nestRoot = typeSym.getNestRoot();
    }


    @Override
    protected @Nullable ResolveResult<JVariableSymbol> resolveValueNameImpl(String simpleName) {
        JVariableSymbol fieldSig = typeSym.getDeclaredField(simpleName); // TODO inherited fields
        if (fieldSig == null) {
            return null;
        }
        // type members are contributed by the class decl, to simplify impl (ie contributor is not the FieldDeclaration)
        return new VarResolveResult(fieldSig, this, node);
    }

    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return typeResolveCache.computeIfAbsent(simpleName, this::findClass);
    }

    @Nullable
    private ResolveResult<JTypeDeclSymbol> findClass(String simpleName) {
        @Nullable JClassSymbol member = findMemberClass(typeSym, simpleName);
        if (member == null) {
            return null;
        }
        // type members are contributed by the class decl, to simplify impl (ie contributor is not the FieldDeclaration)
        return new ClassResolveResult(member, this, node);
    }

    // get an accessible member class
    @Nullable
    private JClassSymbol findMemberClass(@NonNull JClassSymbol typeSym, String simpleName) {
        JClassSymbol klass = typeSym.getDeclaredClass(simpleName);
        if (klass != null && (typeSym == this.typeSym || isAccessibleInStrictSubtypeOfOwner(typeSym))) {
            return klass;
        }
        JClassSymbol superclass = typeSym.getSuperclass();
        if (superclass != null) {
            klass = findMemberClass(superclass, simpleName);
            if (isAccessibleInStrictSubtypeOfOwner(klass)) {
                return klass;
            }
        }
        for (JClassSymbol itf : typeSym.getSuperInterfaces()) {
            klass = findMemberClass(itf, simpleName);
            if (isAccessibleInStrictSubtypeOfOwner(klass)) {
                return klass;
            }
        }
        return null;
    }

    // whether the given symbol is accessible in this.typeSym, assuming
    // the sym is a member of some supertype of this.typeSym
    // it is also assumed that, since it's a member, its enclosing class is != null
    private boolean isAccessibleInStrictSubtypeOfOwner(JAccessibleElementSymbol sym) {
        if (sym == null) {
            return false;
        }

        int modifiers = sym.getModifiers();
        return (modifiers & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0
            // package private
            || Modifier.isPrivate(modifiers) && nestRoot.equals(sym.getEnclosingClass().getNestRoot())
            || sym.getPackageName().equals(nestRoot.getPackageName());
    }


    @Override
    protected @Nullable List<JMethodSymbol> getCachedMethodResults(String simpleName) {
        return methodResolveCache.get(simpleName);
    }

    @Override
    protected void cacheMethodResult(String simpleName, List<JMethodSymbol> sigs) {
        methodResolveCache.put(simpleName, sigs);
    }

    @Override
    protected List<JMethodSymbol> resolveMethodNamesHere(String simpleName) {
        List<JMethodSymbol> acc = new ArrayList<>();
        addMethods(simpleName, acc, typeSym);
        if (typeSym.isInterface() && typeSym.getSuperInterfaces().isEmpty()) {
            // then it's missing Object methods
            addMethods(simpleName, acc, ReflectSymInternals.OBJECT_SYM);
        }

        return acc;
    }

    private void addMethods(String simpleName, List<JMethodSymbol> acc, JClassSymbol owner) {
        for (JMethodSymbol m : owner.getDeclaredMethods()) { // TODO inherited methods
            if (m.getSimpleName().equals(simpleName) && isAccessibleInStrictSubtypeOfOwner(m.getEnclosingClass())) {
                acc.add(m);
            }
        }
    }
}
