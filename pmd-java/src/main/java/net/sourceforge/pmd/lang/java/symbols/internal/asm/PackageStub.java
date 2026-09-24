/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.io.IOException;
import java.io.InputStream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JPackageSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolVisitor;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

final class PackageStub implements JPackageSymbol, AnnotationOwner, AsmStub {
    private final String packageName;
    private final AsmSymbolResolver resolver;
    private final ParseLock parseLock;
    private PSet<SymbolicValue.SymAnnot> annotations = HashTreePSet.empty();

    PackageStub(AsmSymbolResolver resolver, String packageName, @NonNull Loader loader) {
        this.packageName = packageName;
        this.resolver = resolver;

        this.parseLock = new ParseLock.CheckedParseLock("PackageStub:" + packageName) {
            @Override
            protected boolean doParse() throws IOException {
                try (InputStream instream = loader.getInputStream()) {
                    if (instream != null) {
                        ClassReader classReader = new ClassReader(instream);
                        PackageStubBuilder builder = new PackageStubBuilder(PackageStub.this, resolver);
                        classReader.accept(builder, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        return true;
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    // add a bit more info to the exception
                    throw new IOException("While loading package-info from " + loader, e);
                }
            }
        };
    }

    @Override
    public String getSimpleName() {
        return packageName;
    }

    @Override
    public AsmSymbolResolver getResolver() {
        return resolver;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return getResolver().getTypeSystem();
    }

    @Override
    public <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return null;
    }

    @Override
    public void addAnnotation(SymbolicValue.SymAnnot annot) {
        annotations = annotations.plus(annot);
    }

    @Override
    public PSet<SymbolicValue.SymAnnot> getDeclaredAnnotations() {
        parseLock.ensureParsed();
        return annotations;
    }


    private static final class PackageStubBuilder extends ClassVisitor {
        private final PackageStub stub;
        private final AsmSymbolResolver resolver;

        PackageStubBuilder(PackageStub stub, AsmSymbolResolver resolver) {
            super(AsmSymbolResolver.ASM_API_V);
            this.stub = stub;
            this.resolver = resolver;
        }

        @Override
        public AnnotationBuilderVisitor visitAnnotation(String descriptor, boolean visible) {
            return new AnnotationBuilderVisitor(stub, resolver, visible, descriptor);
        }
    }
}
