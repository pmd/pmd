/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.Loader.FailedLoader;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.Loader.StreamLoader;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * A {@link SymbolResolver} that reads class files to produce symbols.
 */
public class AsmSymbolResolver implements SymbolResolver {
    private static final Logger LOG = LoggerFactory.getLogger(AsmSymbolResolver.class);

    static final int ASM_API_V = Opcodes.ASM9;

    private final TypeSystem ts;
    private final Classpath classLoader;
    private final SignatureParser typeLoader;

    private final ConcurrentMap<String, ClassStub> knownStubs = new ConcurrentHashMap<>();

    /**
     * Sentinel for when we fail finding a URL. This allows using a single map,
     * instead of caching failure cases separately.
     */
    private final ClassStub failed;

    public AsmSymbolResolver(TypeSystem ts, Classpath classLoader) {
        this.ts = ts;
        this.classLoader = classLoader;
        this.typeLoader = new SignatureParser(this);
        this.failed = new ClassStub(this, "/*failed-lookup*/", FailedLoader.INSTANCE, 0);
    }

    @Override
    public @Nullable JClassSymbol resolveClassFromBinaryName(@NonNull String binaryName) {
        AssertionUtil.requireParamNotNull("binaryName", binaryName);

        String internalName = getInternalName(binaryName);

        ClassStub found = knownStubs.computeIfAbsent(internalName, iname -> {
            @Nullable InputStream inputStream = getStreamOfInternalName(iname);
            if (inputStream == null) {
                return failed;
            }

            return new ClassStub(this, iname, new StreamLoader(binaryName, inputStream), ClassStub.UNKNOWN_ARITY);
        });

        if (!found.hasCanonicalName()) {
            // note: this check needs to be done outside of computeIfAbsent
            //  to prevent recursive updates of the knownStubs map.
            knownStubs.put(internalName, failed);
            found = failed;
        }

        return found == failed ? null : found; // NOPMD CompareObjectsWithEquals
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

    @Nullable
    InputStream getStreamOfInternalName(String internalName) {
        return classLoader.findResource(internalName + ".class");
    }

    /*
       These methods return an unresolved symbol if the url is not found.
     */

    @Nullable ClassStub resolveFromInternalNameCannotFail(@Nullable String internalName) {
        if (internalName == null) {
            return null;
        }
        return resolveFromInternalNameCannotFail(internalName, ClassStub.UNKNOWN_ARITY);
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals") // ClassStub
    @NonNull ClassStub resolveFromInternalNameCannotFail(@NonNull String internalName, int observedArity) {
        return knownStubs.compute(internalName, (iname, prev) -> {
            if (prev != failed && prev != null) {
                return prev;
            }
            @Nullable InputStream inputStream = getStreamOfInternalName(iname);
            Loader loader = inputStream == null ? FailedLoader.INSTANCE : new StreamLoader(internalName, inputStream);
            return new ClassStub(this, iname, loader, observedArity);
        });
    }

    @Override
    public void logStats() {
        int numParsed = 0;
        int numFailed = 0;
        int numFailedQueries = 0;
        int numNotParsed = 0;

        for (ClassStub stub : knownStubs.values()) {
            if (stub == failed) { // NOPMD CompareObjectsWithEquals
                // Note that failed queries may occur under normal circumstances.
                // Eg package names may be queried just to figure
                // out whether they're packages or classes.
                numFailedQueries++;
            } else if (stub.isNotParsed()) {
                numNotParsed++;
            } else if (!stub.isFailed()) {
                numParsed++;
            } else {
                numFailed++;
            }
        }

        LOG.trace("Of {} distinct queries to the classloader, {} queries failed, "
                        + "{} classes were found and parsed successfully, "
                        + "{} were found but failed parsing (!), "
                        + "{} were found but never parsed.",
                knownStubs.size(), numFailedQueries, numParsed, numFailed, numNotParsed);
    }
}
