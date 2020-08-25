/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.ExecutableStub.CtorStub;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.ExecutableStub.MethodStub;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.Loader.NoUrlLoader;


class ClassStubBuilder extends ClassVisitor {

    private final ClassStub myStub;
    private final String myInternalName;
    private final AsmSymbolResolver resolver;

    private boolean isInnerNonStaticClass = false;

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
        isInnerNonStaticClass = true;
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
        if (myInternalName.equals(outerName) && innerSimpleName != null) { // not anonymous
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
            isInnerNonStaticClass = (Opcodes.ACC_STATIC & access) == 0;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if ((access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE)) != 0) {
            // ignore synthetic methods
            return null;
        }

        if ("<clinit>".equals(name)) {
            return null;
        }


        ExecutableStub execStub;
        if ("<init>".equals(name)) {
            CtorStub ctor = new CtorStub(myStub, access, descriptor, signature, exceptions, isInnerNonStaticClass);
            myStub.addCtor(ctor);
            execStub = ctor;
        } else {
            MethodStub method = new MethodStub(myStub, name, access, descriptor, signature, exceptions);
            myStub.addMethod(method);
            execStub = method;
        }
        return new MethodInfoVisitor(execStub);
    }
}
