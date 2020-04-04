/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.table.nimpl.MostlySingularMultimap.Builder;
import net.sourceforge.pmd.lang.java.symbols.table.nimpl.NameResolver.MultiSymResolver;
import net.sourceforge.pmd.lang.java.symbols.table.nimpl.NameResolver.SingleSymResolver;
import net.sourceforge.pmd.util.OptionalBool;

class Resolvers {


    static <V> MostlySingularMultimap.Builder<String, V> newMapBuilder() {
        return MostlySingularMultimap.newBuilder(HashMap::new);
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
        return new SingleSymResolver<JTypeDeclSymbol>() {
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
        return new SingleSymResolver<JTypeDeclSymbol>() {
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

    static <S> NameResolver<S> singleton(String name, S symbol) {
        final List<S> single = singletonList(symbol);
        return new SingleSymResolver<S>() {
            @Override
            public List<S> resolveHere(String s) {
                return name.equals(s) ? single : emptyList();
            }

            @Override
            public @Nullable S resolveFirst(String simpleName) {
                return name.equals(simpleName) ? symbol : null;
            }

            @Override
            public @Nullable OptionalBool knows(String simpleName) {
                return OptionalBool.definitely(name.equals(simpleName));
            }

            @Override
            public String toString() {
                return "Single(" + symbol + ")";
            }
        };
    }

    @NonNull
    static <S> NameResolver<S> mapResolver(MostlySingularMultimap.Builder<String, S> symbols) {
        Entry<String, S> pair = symbols.singleOrNull();
        if (pair != null) {
            return singleton(pair.getKey(), pair.getValue());
        } else if (symbols.isEmpty()) {
            return emptyResolver();
        }

        MostlySingularMultimap<String, S> map = symbols.build();

        return new MultiSymResolver<S>() {
            @Override
            public List<S> resolveHere(String s) {
                return map.get(s);
            }

            @Override
            public @Nullable OptionalBool knows(String simpleName) {
                return OptionalBool.definitely(map.containsKey(simpleName));
            }

            @Override
            public String toString() {
                return "Map(" + mapToString() + ")";
            }

            private String mapToString() {
                if (map.isEmpty()) {
                    return "{}";
                }
                StringBuilder sb = new StringBuilder("{");
                map.processValuesOneByOne((k, v) -> sb.append(v).append(", "));
                return sb.substring(0, sb.length() - 2) + "}";
            }
        };
    }

    static <S> EmptyResolver<S> emptyResolver() {
        return EmptyResolver.INSTANCE;
    }

    static class EmptyResolver<S> implements NameResolver<S> {

        private static final EmptyResolver INSTANCE = new EmptyResolver<>();

        @Nullable
        @Override
        public S resolveFirst(String simpleName) {
            return null;
        }

        @Override
        public List<S> resolveHere(String simpleName) {
            return emptyList();
        }

        @Override
        public @Nullable OptionalBool knows(String simpleName) {
            return OptionalBool.NO;
        }

        @Override
        public String toString() {
            return "Empty";
        }
    }

    static NameResolver<JMethodSymbol> methodResolver(JClassSymbol t) {
        JClassSymbol nestRoot = t.getNestRoot();
        return new MultiSymResolver<JMethodSymbol>() {
            @Override
            public List<JMethodSymbol> resolveHere(String simpleName) {
                return SymUtils.getSuperTypeStream(t, Interfaces.INCLUDE)
                               .flatMap(it -> it.getDeclaredMethods().stream())
                               .filter(it -> it.getSimpleName().equals(simpleName) && isAccessibleInStrictSubtypeOfOwner(nestRoot, it))
                               .collect(Collectors.toList());
            }

            @Override
            public String toString() {
                return "methods of " + t;
            }
        };
    }

    static Pair<NameResolver<JTypeDeclSymbol>, NameResolver<JVariableSymbol>> classAndFieldResolvers(JClassSymbol t) {
        JClassSymbol nestRoot = t.getNestRoot();

        Builder<String, JVariableSymbol> fields = newMapBuilder();
        Builder<String, JTypeDeclSymbol> types = newMapBuilder();

        Set<String> seenFields = new HashSet<>();
        Set<String> seenTypes = new HashSet<>();

        for (JClassSymbol sup : SymUtils.iterateSuperTypes(t, Interfaces.INCLUDE)) {
            for (JFieldSymbol df : sup.getDeclaredFields()) {
                if (seenFields.add(df.getSimpleName()) && isAccessibleInStrictSubtypeOfOwner(nestRoot, df)) {
                    fields.appendValue(df.getSimpleName(), df);
                }
            }

            boolean inInterface = sup.isInterface();
            for (JClassSymbol df : sup.getDeclaredClasses()) {
                if (inInterface
                    || seenTypes.add(df.getSimpleName()) && isAccessibleInStrictSubtypeOfOwner(nestRoot, df)) {
                    types.appendValue(df.getSimpleName(), df);
                }
            }


        }
        return Pair.of(mapResolver(types), mapResolver(fields));
    }

    // same thing as above, the above just uses a single traversal
    // this will usually be queried just once because of
    static NameResolver<JTypeDeclSymbol> lazyNestedClassResolver(JClassSymbol t) {
        return new SingleSymResolver<JTypeDeclSymbol>() {
            @Nullable
            @Override
            public JTypeDeclSymbol resolveFirst(String simpleName) {
                JClassSymbol here = t.getDeclaredClass(simpleName);
                if (here != null) {
                    return here;
                }

                JClassSymbol nestRoot = t.getNestRoot();
                Set<String> seenTypes = new HashSet<>();
                for (JClassSymbol sup : SymUtils.iterateSuperTypes(t, Interfaces.INCLUDE)) {
                    for (JClassSymbol df : sup.getDeclaredClasses()) {
                        if (seenTypes.add(df.getSimpleName())
                            && isAccessibleInStrictSubtypeOfOwner(nestRoot, df)) {
                            return df;
                        }
                    }
                }
                return null;
            }
        };
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
