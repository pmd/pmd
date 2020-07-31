/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import net.sourceforge.pmd.lang.java.symbols.internal.impl.asm.Loader.NoUrlLoader;


class ClassStubBuilder extends ClassVisitor {

    private final ClassStub myStub;
    private final String myInternalName;
    private final AsmSymbolResolver resolver;

    ClassStubBuilder(ClassStub stub, AsmSymbolResolver resolver) {
        super(AsmSymbolResolver.ASM_API_V);
        this.myStub = stub;
        this.myInternalName = stub.getInternalName();
        this.resolver = resolver;
    }

    @Override
    public void visit(int version, int access, String internalName, @Nullable String signature, String superName, String[] interfaces) {
        myStub.setModifiers(access, true);
        myStub.setHeader(signature, superName, interfaces);
    }

    @Override
    public void visitOuterClass(String ownerInternalName, @Nullable String methodName, @Nullable String methodDescriptor) {
        // only for enclosing method
        myStub.setOuterClass(ownerInternalName, methodName, methodDescriptor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, @Nullable String signature, @Nullable Object value) {
        FieldStub field = new FieldStub(myStub, name, access, descriptor, signature, value);
        myStub.addField(field);
        return null;
    }

    @Override
    public void visitInnerClass(String innerInternalName, @Nullable String outerName, @Nullable String innerSimpleName, int access) {
        if (myInternalName.equals(outerName)
            && innerSimpleName != null) { // not anonymous TODO local?
            ClassStub member = new ClassStub(myStub.getResolver(),
                                             innerInternalName,
                                             new NoUrlLoader(myStub.getResolver(), innerInternalName),
                                             ClassStub.UNKNOWN_ARITY);
            resolver.registerKnown(innerInternalName, member);
            member.setModifiers(access, false);
            myStub.addMemberClass(member);
        } else if (myInternalName.equals(innerInternalName) && outerName != null) {
            // then it's specifying the enclosing class
            myStub.setModifiers(access, false);
            myStub.setOuterClass(outerName, null, null);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if ("<clinit>".equals(name)) {
            return null;
        } else if ("<init>".equals(name)) {
            myStub.addCtor(new ExecutableStub.CtorStub(myStub, access, descriptor, signature, exceptions));
        } else {
            myStub.addMethod(new ExecutableStub.MethodStub(myStub, name, access, descriptor, signature, exceptions));
        }
        return null;
    }
}
