/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm;


import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.asm.Loader.FailedLoader;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.asm.Loader.UrlLoader;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * A {@link SymbolResolver} that reads class files to produce symbols.
 */
public class AsmSymbolResolver implements SymbolResolver {
    /*
        TODO
         - expose constant values for compile-time constants (in JFieldSymbol)
             - used to finish implementing ASTExpression::getConstValue
         - expose enum constant names
             - eg used in ASTSwitchLike::isExhaustiveEnumSwitch
     */

    static final int ASM_API_V = Opcodes.ASM7;

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

    @NonNull
    static String getInternalName(@NonNull String binaryName) {
        return binaryName.replace('.', '/');
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
        assert internalName != null : "Null name";
        return knownStubs.computeIfAbsent(internalName, iname -> {
            @Nullable URL url = getUrlOfInternalName(internalName);
            Loader loader = url == null ? FailedLoader.INSTANCE : new UrlLoader(url);
            return new SoftClassReference(this, iname, loader, observedArity);
        }).get();
    }
}
