/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.ExecutableStub.CtorStub;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.ExecutableStub.MethodStub;

/**
 * Populates a {@link ClassStub} by reading a class file. Some info is
 * known by the ClassStub without parsing (like its internal name), so
 * we defer parsing until later. The class should be parsed only once.
 */
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
    public AnnotationBuilderVisitor visitAnnotation(String descriptor, boolean visible) {
        return new AnnotationBuilderVisitor(myStub, resolver, visible, descriptor);
    }

    @Override
    public void visitOuterClass(String ownerInternalName, @Nullable String methodName, @Nullable String methodDescriptor) {
        isInnerNonStaticClass = true;
        // only for enclosing method
        ClassStub outer = resolver.resolveFromInternalNameCannotFail(ownerInternalName);
        myStub.setOuterClass(outer, methodName, methodDescriptor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, @Nullable String signature, @Nullable Object value) {
        FieldStub field = new FieldStub(myStub, name, access, descriptor, signature, value);
        myStub.addField(field);
        return new FieldVisitor(AsmSymbolResolver.ASM_API_V) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                return new AnnotationBuilderVisitor(field, resolver, visible, descriptor);
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, @Nullable TypePath typePath, String descriptor, boolean visible) {
                assert new TypeReference(typeRef).getSort() == TypeReference.FIELD : typeRef;
                return new AnnotationBuilderVisitor.TypeAnnotBuilderImpl(resolver, field, typeRef, typePath, visible, descriptor);
            }
        };
    }

    /**
     * Visits information about an inner class. This inner class is not necessarily a member of the
     * class being visited.
     *
     * <p>Spec: https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.6
     *
     * @param innerInternalName the internal name of an inner class (see {@link Type#getInternalName()}).
     * @param outerName         the internal name of the class to which the inner class belongs (see {@link
     *                          Type#getInternalName()}). May be {@literal null} for not member classes.
     * @param innerSimpleName   the (simple) name of the inner class inside its enclosing class. May be
     *                          {@literal null} for anonymous inner classes.
     * @param access            the access flags of the inner class as originally
     *                          declared in the enclosing class.
     */
    @Override
    public void visitInnerClass(String innerInternalName, @Nullable String outerName, @Nullable String innerSimpleName, int access) {
        if (myInternalName.equals(outerName) && innerSimpleName != null) { // not anonymous
            ClassStub member = resolver.resolveFromInternalNameCannotFail(innerInternalName, ClassStub.UNKNOWN_ARITY);
            member.setSimpleName(innerSimpleName);
            member.setModifiers(access, false);
            myStub.addMemberClass(member);
        } else if (myInternalName.equals(innerInternalName) && outerName != null) {
            // then it's specifying the enclosing class
            // (myStub is the inner class)
            ClassStub outer = resolver.resolveFromInternalNameCannotFail(outerName);
            myStub.setSimpleName(innerSimpleName);
            myStub.setModifiers(access, false);
            myStub.setOuterClass(outer, null, null);
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
