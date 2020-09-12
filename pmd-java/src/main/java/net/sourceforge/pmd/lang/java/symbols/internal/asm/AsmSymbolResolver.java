/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.Loader.FailedLoader;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.Loader.UrlLoader;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * A {@link SymbolResolver} that reads class files to produce symbols.
 */
public class AsmSymbolResolver implements SymbolResolver {
    /*
        TODO
         - expose enum constant names
             - eg used in ASTSwitchLike::isExhaustiveEnumSwitch
     */

    static final int ASM_API_V = Opcodes.ASM9;

    private final TypeSystem ts;
    private final ClassLoader classLoader;
    private final SignatureParser typeLoader;

    private final ConcurrentHashMap<String, SoftClassReference> knownStubs = new ConcurrentHashMap<>();

    /**
     * Sentinel for when we fail finding a URL. This allows using a single map,
     * instead of caching failure cases separately.
     */
    private final SoftClassReference failed;

    public AsmSymbolResolver(TypeSystem ts, ClassLoader classLoader) {
        this.ts = ts;
        this.classLoader = classLoader;
        this.typeLoader = new SignatureParser(this);
        this.failed = new SoftClassReference(this, "/*failed-lookup*/", FailedLoader.INSTANCE, 0);
    }

    @Override
    public @Nullable JClassSymbol resolveClassFromBinaryName(@NonNull String binaryName) {
        AssertionUtil.requireParamNotNull("binaryName", binaryName);

        String internalName = getInternalName(binaryName);

        SoftClassReference found = knownStubs.computeIfAbsent(internalName, iname -> {
            if (!hasCanonicalName(internalName)) {
                // if the class is anonymous/local, give up
                return failed;
            }

            @Nullable URL url = getUrlOfInternalName(internalName);
            if (url == null) {
                return failed;
            }

            return new SoftClassReference(this, iname, new UrlLoader(url), ClassStub.UNKNOWN_ARITY);
        });

        return found == failed ? null : found.get(); // NOPMD CompareObjectsWithEquals
    }

    SignatureParser getSigParser() {
        return typeLoader;
    }

    TypeSystem getTypeSystem() {
        return ts;
    }

    static @NonNull String getInternalName(@NonNull String binaryName) {
        return binaryName.replace('.', '/');
    }

    /**
     * Test whether an internal name has a canonical name. This means,
     * every segment of the simple name (part after the last '/'), where
     * segments are separated by '$', is a valid java identifier. We only
     * check the first character as anon/local classes are identified with
     * integers, which are not valid java identifier starts.
     */
    static boolean hasCanonicalName(String internalName) {
        int packageEnd = internalName.lastIndexOf('/');
        for (int i = packageEnd; i + 1 < internalName.length();) {
            char firstChar = internalName.charAt(i + 1);
            if (!Character.isJavaIdentifierStart(firstChar)) {
                return false;
            }
            i = internalName.indexOf('$', i + 1);
            if (i == -1) {
                break;
            }
        }
        return !internalName.isEmpty();
    }

    @Nullable
    URL getUrlOfInternalName(String internalName) {
        return classLoader.getResource(internalName + ".class");
    }

    /*
       These methods return an unresolved symbol if the url is not found.
     */

    @Nullable JClassSymbol resolveFromInternalNameCannotFail(@Nullable String internalName) {
        if (internalName == null) {
            return null;
        }
        return resolveFromInternalNameCannotFail(internalName, 0);
    }

    // this is for inner + parent classes
    void registerKnown(@NonNull String internalName, ClassStub innerClass) {
        SoftClassReference softRef = new SoftClassReference(this, innerClass, internalName);
        knownStubs.put(internalName, softRef);
    }

    @NonNull JClassSymbol resolveFromInternalNameCannotFail(@NonNull String internalName, int observedArity) {
        return knownStubs.compute(internalName, (iname, prev) -> {
            if (prev != failed && prev != null) {
                return prev;
            }
            @Nullable URL url = getUrlOfInternalName(iname);
            Loader loader = url == null ? FailedLoader.INSTANCE : new UrlLoader(url);
            return new SoftClassReference(this, iname, loader, observedArity);
        }).get();
    }
}
