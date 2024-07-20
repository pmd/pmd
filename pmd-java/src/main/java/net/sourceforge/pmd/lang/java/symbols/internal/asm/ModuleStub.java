/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JModuleSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolVisitor;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

class ModuleStub implements JModuleSymbol, AsmStub, AnnotationOwner {

    private final AsmSymbolResolver resolver;

    private final String moduleName;
    private PSet<SymbolicValue.SymAnnot> annotations = HashTreePSet.empty();
    private Set<String> exportedPackages = new HashSet<>();

    private final ParseLock parseLock;

    ModuleStub(AsmSymbolResolver resolver, String moduleName, @NonNull Loader loader) {
        this.resolver = resolver;
        this.moduleName = moduleName;

        this.parseLock = new ParseLock() {
            @Override
            protected boolean doParse() throws IOException {
                try (InputStream instream = loader.getInputStream()) {
                    if (instream != null) {
                        ClassReader classReader = new ClassReader(instream);
                        ClassVisitor classVisitor = new ClassVisitor(AsmSymbolResolver.ASM_API_V) {
                            @Override
                            public ModuleVisitor visitModule(String name, int access, String version) {
                                assert name.equals(moduleName) : "Expected module-info.class for " + moduleName + ", but got " + name;

                                return new ModuleVisitor(AsmSymbolResolver.ASM_API_V) {
                                    @Override
                                    public void visitExport(String packaze, int access, String... modules) {
                                        exportedPackages.add(packaze.replace('/', '.'));
                                    }
                                };
                            }

                            @Override
                            public AnnotationBuilderVisitor visitAnnotation(String descriptor, boolean visible) {
                                return new AnnotationBuilderVisitor(ModuleStub.this, resolver, visible, descriptor);
                            }
                        };
                        classReader.accept(classVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        return true;
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    // add a bit more info to the exception
                    throw new IOException("While loading class from " + loader, e);
                }
            }
        };
    }

    @Override
    public AsmSymbolResolver getResolver() {
        return resolver;
    }

    @Override
    public boolean isUnresolved() {
        return parseLock.isFailed();
    }

    @Override
    public <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return null;
    }

    @Override
    public Set<String> getExportedPackages() {
        parseLock.ensureParsed();
        return exportedPackages;
    }

    @Override
    public String getSimpleName() {
        return moduleName; // TODO
    }

    @Override
    public TypeSystem getTypeSystem() {
        return getResolver().getTypeSystem();
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
}
