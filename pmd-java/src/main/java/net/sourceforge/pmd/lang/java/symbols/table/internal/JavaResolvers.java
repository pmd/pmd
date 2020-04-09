/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator.ALL_STRICT_SUPERTYPES;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator.DIRECT_STRICT_SUPERTYPES;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator.JUST_SELF;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.NameResolver;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.NameResolver.SingleNameResolver;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainBuilder;

class JavaResolvers {

    /** Prepend the package name, handling empty package. */
    static String prependPackageName(String pack, String name) {
        return pack.isEmpty() ? name : pack + "." + name;
    }

    /**
     * Returns true if the given element can be imported in the current file
     * (it's visible & accessible). This is not a general purpose accessibility
     * check and is only appropriate for imports.
     *
     *
     * <p>We consider protected members inaccessible outside of the package they were declared in,
     * which is an approximation but won't cause problems in practice.
     * In an ACU in another package, the name is accessible only inside classes that inherit
     * from the declaring class. But inheriting from a class makes its static members
     * accessible via simple name too. So this will actually be picked up by some other symbol table
     * when in the subclass. Usages outside of the subclass would have made the compilation fail.
     */
    static boolean canBeImportedIn(String thisPackage, JAccessibleElementSymbol member) {
        int modifiers = member.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            return true;
        } else if (Modifier.isPrivate(modifiers)) {
            return false;
        } else {
            // then it's package private, or protected
            return thisPackage.equals(member.getPackageName());
        }
    }

    @NonNull
    static NameResolver<JTypeDeclSymbol> importedOnDemand(Set<String> lazyImportedPackagesAndTypes,
                                                          final SymbolResolver symResolver,
                                                          final String thisPackage) {
        return new SingleNameResolver<JTypeDeclSymbol>() {
            @Nullable
            @Override
            public JTypeDeclSymbol resolveFirst(String simpleName) {
                for (String pack : lazyImportedPackagesAndTypes) {
                    // here 'pack' may be a package or a type name, so we must resolve by canonical name
                    String name = prependPackageName(pack, simpleName);
                    JClassSymbol sym = symResolver.resolveClassFromCanonicalName(name);
                    if (sym != null && canBeImportedIn(thisPackage, sym)) {
                        return sym;
                    }
                }
                return null;
            }

            @Override
            public String toString() {
                return "ImportOnDemandResolver(" + lazyImportedPackagesAndTypes + ")";
            }
        };
    }

    @NonNull
    static NameResolver<JTypeDeclSymbol> packageResolver(SymbolResolver symResolver, String packageName) {
        return new SingleNameResolver<JTypeDeclSymbol>() {
            @Nullable
            @Override
            public JTypeDeclSymbol resolveFirst(String simpleName) {
                return symResolver.resolveClassFromBinaryName(prependPackageName(packageName, simpleName));
            }

            @Override
            public String toString() {
                return "PackageResolver(" + packageName + ")";
            }
        };
    }

    private static SuperTypesEnumerator enumeratorFor(boolean onlyInherited) {
        return onlyInherited ? ALL_STRICT_SUPERTYPES : JUST_SELF;
    }

    static NameResolver<JMethodSymbol> methodResolver(JClassSymbol t, boolean onlyInherited) {
        JClassSymbol nestRoot = t.getNestRoot();
        SuperTypesEnumerator enumerator = enumeratorFor(onlyInherited);
        return new NameResolver<JMethodSymbol>() {
            @Override
            public @NonNull List<JMethodSymbol> resolveHere(String simpleName) {
                return enumerator.stream(t)
                                 .flatMap(it -> it.getDeclaredMethods().stream())
                                 .filter(it -> it.getSimpleName().equals(simpleName)
                                     && isAccessibleInStrictSubtypeOfOwner(nestRoot, it))
                                 .collect(Collectors.toList());
            }

            @Override
            public String toString() {
                return "methods of " + t;
            }
        };
    }

    /**
     * Resolvers for inherited member types and fields. We can't process
     * methods that way, because there may be duplicates and the equals
     * of {@link JMethodSymbol} is not reliable for now (cannot differentiate
     * overloads). But also, usually a subset of methods is used in a subclass,
     * and it's ok performance-wise to process them lazily.
     */
    static Pair<NameResolver<JTypeDeclSymbol>, NameResolver<JVariableSymbol>> inheritedMembersResolvers(JClassSymbol t) {
        JClassSymbol nestRoot = t.getNestRoot();

        ShadowChainBuilder<JVariableSymbol, ScopeInfo>.ResolverBuilder fields = SymTableFactory.VARS.new ResolverBuilder();
        ShadowChainBuilder<JTypeDeclSymbol, ScopeInfo>.ResolverBuilder types = SymTableFactory.TYPES.new ResolverBuilder();

        for (JClassSymbol next : DIRECT_STRICT_SUPERTYPES.iterable(t)) {
            walkSelf(next, nestRoot, fields, types, HashTreePSet.empty(), HashTreePSet.empty());
        }

        // Note that if T is an interface, Object won't have been visited
        // This is fine for now because Object has no fields or nested types
        // in any known version of the JDK

        return Pair.of(types.build(), fields.build());
    }

    private static void walkSelf(JClassSymbol t,
                                 JClassSymbol nestRoot, // context
                                 ShadowChainBuilder<JVariableSymbol, ?>.ResolverBuilder fields,
                                 ShadowChainBuilder<JTypeDeclSymbol, ?>.ResolverBuilder types,
                                 // persistent because may change in every path of the recursion
                                 final PSet<String> hiddenFields,
                                 final PSet<String> hiddenTypes) {

        // Note that it is possible that this process recurses several
        // times into the same interface (if it is reachable from several paths)
        // This is because the set of hidden declarations depends on the
        // full path, and may be different each time.
        // Profiling shows that this doesn't occur very often, and adding
        // a recursion guard is counter-productive performance-wise

        PSet<String> hiddenTypesInSup = processDeclarations(nestRoot, types, hiddenTypes, t.getDeclaredClasses());
        PSet<String> hiddenFieldsInSup = processDeclarations(nestRoot, fields, hiddenFields, t.getDeclaredFields());

        // depth first
        for (JClassSymbol next : DIRECT_STRICT_SUPERTYPES.iterable(t)) {
            walkSelf(next, nestRoot, fields, types, hiddenFieldsInSup, hiddenTypesInSup);
        }
    }

    private static <S extends JAccessibleElementSymbol> PSet<String> processDeclarations(JClassSymbol nestRoot,
                                                                                         ShadowChainBuilder<? super S, ?>.ResolverBuilder builder,
                                                                                         PSet<String> hidden,
                                                                                         List<S> syms) {
        for (S inner : syms) {
            String simpleName = inner.getSimpleName();
            if (hidden.contains(simpleName)) {
                continue;
            }

            hidden = hidden.plus(simpleName);

            if (isAccessibleInStrictSubtypeOfOwner(nestRoot, inner)) {
                builder.appendWithoutDuplicate(inner);
            }
        }
        return hidden;
    }

    // whether the given symbol is accessible in this.typeSym, assuming
    // the sym is a member of some supertype of this.typeSym
    // it is also assumed that, since it's a member, its enclosing class is != null
    private static boolean isAccessibleInStrictSubtypeOfOwner(JClassSymbol nestRoot, JAccessibleElementSymbol sym) {
        if (sym == null) {
            return false;
        }

        int modifiers = sym.getModifiers();
        if ((modifiers & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
            return true;
        } else if (Modifier.isPrivate(modifiers)) {
            return nestRoot.equals(sym.getEnclosingClass().getNestRoot());
        } else {
            return sym.getPackageName().equals(nestRoot.getPackageName());
        }
    }
}
