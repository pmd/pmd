/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.internal.util.IteratorUtil.anyMatch;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator.ALL_STRICT_SUPERTYPES;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator.ALL_SUPERTYPES_INCLUDING_SELF;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator.DIRECT_STRICT_SUPERTYPES;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator.JUST_SELF;
import static net.sourceforge.pmd.util.CollectionUtil.listOfNotNull;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.CoreResolvers;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.NameResolver;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.NameResolver.SingleNameResolver;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainBuilder;

public final class JavaResolvers {

    private JavaResolvers() {
        // utility class
    }

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
                                 .filter(it -> it.getSimpleName().equals(simpleName) && isAccessibleIn(nestRoot, it, true))
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
     * and it's ok performance-wise to process them on-demand.
     */
    static Pair<NameResolver<JTypeDeclSymbol>, NameResolver<JVariableSymbol>> inheritedMembersResolvers(JClassSymbol t) {
        JClassSymbol nestRoot = t.getNestRoot();

        ShadowChainBuilder<JVariableSymbol, ScopeInfo>.ResolverBuilder fields = SymTableFactory.VARS.new ResolverBuilder();
        ShadowChainBuilder<JTypeDeclSymbol, ScopeInfo>.ResolverBuilder types = SymTableFactory.TYPES.new ResolverBuilder();

        for (JClassSymbol next : DIRECT_STRICT_SUPERTYPES.iterable(t)) {
            walkSelf(next, s -> isAccessibleIn(nestRoot, s, true), fields, types, HashTreePSet.empty(), HashTreePSet.empty());
        }

        // Note that if T is an interface, Object won't have been visited
        // This is fine for now because Object has no fields or nested types
        // in any known version of the JDK

        return Pair.of(types.build(), fields.build());
    }

    private static void walkSelf(JClassSymbol t,
                                 Predicate<? super JAccessibleElementSymbol> isAccessible,
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

        PSet<String> hiddenTypesInSup = processDeclarations(types, hiddenTypes, isAccessible, t.getDeclaredClasses());
        PSet<String> hiddenFieldsInSup = processDeclarations(fields, hiddenFields, isAccessible, t.getDeclaredFields());

        // depth first
        for (JClassSymbol next : DIRECT_STRICT_SUPERTYPES.iterable(t)) {
            walkSelf(next, isAccessible, fields, types, hiddenFieldsInSup, hiddenTypesInSup);
        }
    }

    private static <S extends JAccessibleElementSymbol>
        PSet<String> processDeclarations(ShadowChainBuilder<? super S, ?>.ResolverBuilder builder,
                                         PSet<String> hidden,
                                         Predicate<? super S> isAccessible,
                                         List<? extends S> syms) {
        for (S inner : syms) {
            String simpleName = inner.getSimpleName();
            if (hidden.contains(simpleName)) {
                continue;
            }

            hidden = hidden.plus(simpleName);

            if (isAccessible.test(inner)) {
                builder.appendWithoutDuplicate(inner);
            }
        }
        return hidden;
    }

    private static boolean isAccessibleIn(@NonNull JClassSymbol nestRoot,
                                          JAccessibleElementSymbol sym,
                                          boolean isOwnerASupertypeOfContext) {
        return isAccessibleIn(nestRoot, nestRoot.getPackageName(), sym, isOwnerASupertypeOfContext);
    }

    /**
     * Whether the given sym is accessible in some type T, given
     * the 'nestRoot' of T, and whether T is a subtype of the class
     * declaring 'sym'. This is a general purpose accessibility check,
     * albeit a bit low-level (but only needs subtyping to be computed once).
     */
    private static boolean isAccessibleIn(@Nullable JClassSymbol nestRoot,
                                          String packageName,
                                          JAccessibleElementSymbol sym,
                                          boolean isOwnerASupertypeOfContext) {
        int modifiers = sym.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);

        switch (modifiers) {
        case Modifier.PUBLIC:
            return true;
        case Modifier.PRIVATE:
            return nestRoot != null && nestRoot.equals(sym.getEnclosingClass().getNestRoot());
        case Modifier.PROTECTED:
            if (isOwnerASupertypeOfContext) {
                return true;
            }
            // fallthrough
        case 0:
            return sym.getPackageName().equals(packageName);
        default:
            throw new AssertionError(Modifier.toString(sym.getModifiers()));
        }
    }

    private static final ShadowChainBuilder<JClassSymbol, ScopeInfo> CLASSES = new SymbolChainBuilder<>();
    private static final ShadowChainBuilder<JFieldSymbol, ScopeInfo> FIELDS = new SymbolChainBuilder<>();

    /**
     * Produce a name resolver that resolves member classes with the
     * given name declared or inherited by the given type. Each access
     * may perform a hierarchy traversal, but this handles hidden and
     * ambiguous declarations nicely.
     *
     * @param c      Class to search
     * @param access Context of where the declaration is referenced
     * @param name   Name of the class to find
     */
    public static NameResolver<JClassSymbol> getMemberClassResolver(JClassSymbol c, @NonNull String accessPackageName, @Nullable JClassSymbol access, String name) {
        return getNamedMemberResolver(c, access, accessPackageName, JClassSymbol::getDeclaredClass, name, CLASSES);
    }

    public static NameResolver<JFieldSymbol> getMemberFieldResolver(JClassSymbol c, @NonNull String accessPackageName, @Nullable JClassSymbol access, String name) {
        return getNamedMemberResolver(c, access, accessPackageName, JClassSymbol::getDeclaredField, name, FIELDS);
    }

    private static <S extends JAccessibleElementSymbol> NameResolver<S> getNamedMemberResolver(JClassSymbol c,
                                                                                               @Nullable JClassSymbol access,
                                                                                               @NonNull String accessPackageName,
                                                                                               BiFunction<? super JClassSymbol, String, ? extends S> getter,
                                                                                               String name,
                                                                                               ShadowChainBuilder<S, ?> classes) {
        S found = getter.apply(c, name);
        if (found != null) {
            // fast path, doesn't need to check accessibility, etc
            return CoreResolvers.singleton(name, found);
        }

        JClassSymbol nestRoot = access == null ? null : access.getNestRoot();
        Predicate<S> isAccessible = s -> isAccessibleIn(nestRoot, accessPackageName, s, isSubtype(access, s.getEnclosingClass()));

        ShadowChainBuilder<S, ?>.ResolverBuilder builder = classes.new ResolverBuilder();

        for (JClassSymbol next : DIRECT_STRICT_SUPERTYPES.iterable(c)) {
            walkForSingleName(next, isAccessible, name, getter, builder, HashTreePSet.empty());
        }

        return builder.build();
    }

    private static boolean isSubtype(JClassSymbol sub, JClassSymbol sup) {
        return anyMatch(ALL_SUPERTYPES_INCLUDING_SELF.iterator(sub), it -> it.equals(sup));
    }

    private static <S extends JAccessibleElementSymbol>
        void walkForSingleName(JClassSymbol t,
                               Predicate<? super S> isAccessible,
                               String name,
                               BiFunction<? super JClassSymbol, String, ? extends S> getter,
                               ShadowChainBuilder<? super S, ?>.ResolverBuilder builder,
                               final PSet<String> hidden) {

        PSet<String> hiddenInSup = processDeclarations(builder, hidden, isAccessible, listOfNotNull(getter.apply(t, name)));

        if (!hiddenInSup.isEmpty()) {
            // found it in this branch
            // in this method the hidden set is either empty or one element only
            return;
        }

        // depth first
        for (JClassSymbol next : DIRECT_STRICT_SUPERTYPES.iterable(t)) {
            walkForSingleName(next, isAccessible, name, getter, builder, hiddenInSup);
        }
    }

}
