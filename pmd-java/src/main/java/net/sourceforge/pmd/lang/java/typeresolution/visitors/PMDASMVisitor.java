/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import net.sourceforge.pmd.annotation.InternalApi;

@Deprecated
@InternalApi
public class PMDASMVisitor extends ClassVisitor {

    private String outerName;

    private Map<String, String> packages = new HashMap<>();

    private AnnotationVisitor annotationVisitor = new PMDAnnotationVisitor(this);

    private FieldVisitor fieldVisitor = new PMDFieldVisitor(this);

    private SignatureVisitor sigVisitor = new PMDSignatureVisitor(this);

    private MethodVisitor methodVisitor = new PMDMethodVisitor(this);

    public List<String> innerClasses;

    public PMDASMVisitor(String outerName) {
        super(Opcodes.ASM7);
        this.outerName = outerName;
    }

    public Map<String, String> getPackages() {
        return packages;
    }

    public List<String> getInnerClasses() {
        return innerClasses;
    }

    private String parseClassName(String name) {
        if (name == null) {
            return null;
        }

        String className = name;
        int n = name.lastIndexOf('/');
        if (n > -1) {
            className = name.substring(n + 1);
        }
        name = name.replace('/', '.');
        packages.put(className, name);
        n = className.indexOf('$');
        if (n > -1) {
            // TODO I don't think the first one, with Class$Inner is needed -
            // come back and check
            packages.put(className.substring(n + 1), name);
            packages.put(className.replace('$', '.'), name);
        }

        return name;
    }

    private void parseClassName(String[] names) {
        if (names != null) {
            for (String s : names) {
                parseClassName(s);
            }
        }
    }

    private void extractSignature(String sig) {
        if (sig != null) {
            new SignatureReader(sig).accept(sigVisitor);
        }
    }

    /* Start ClassVisitor implementations */

    @Override
    public void visit(int version, int access, String name, String sig, String superName, String[] interfaces) {
        parseClassName(name);
        parseClassName(interfaces);
        if (sig != null) {
            extractSignature(sig);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        addType(Type.getType(desc));
        return annotationVisitor;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String sig, Object value) {
        if (sig != null) {
            extractSignature(sig);
        }

        addType(Type.getType(desc));
        if (value instanceof Type) {
            addType((Type) value);
        }
        return fieldVisitor;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
        if (sig != null) {
            extractSignature(sig);
        }
        addMethodDesc(desc);
        parseClassName(exceptions);
        return methodVisitor;
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (!this.outerName.replace('.', '/').equals(outerName)) {
            // do not consider the inner class if it is not a member of our
            // outer class
            return;
        }

        if (innerClasses == null) {
            innerClasses = new ArrayList<>();
        }
        if (!innerClasses.contains(name.replace('/', '.'))) {
            innerClasses.add(name.replace('/', '.'));
        }
        packages.put(innerName, name.replace('/', '.'));
    }

    private void addMethodDesc(String desc) {
        addTypes(desc);
        addType(Type.getReturnType(desc));
    }

    private void addTypes(String desc) {
        Type[] types = Type.getArgumentTypes(desc);
        for (Type type : types) {
            addType(type);
        }
    }

    private void addType(Type t) {
        switch (t.getSort()) {
        case Type.ARRAY:
            addType(t.getElementType());
            break;
        case Type.OBJECT:
            parseClassName(t.getClassName().replace('.', '/'));
            break;
        default:
            // Do nothing
            break;
        }
    }

    /*
     * Start visitors
     */

    private static class PMDFieldVisitor extends FieldVisitor {

        private PMDASMVisitor parent;

        PMDFieldVisitor(PMDASMVisitor visitor) {
            super(Opcodes.ASM5);
            parent = visitor;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            parent.addType(Type.getType(desc));
            return parent.annotationVisitor;
        }
    }

    private static class PMDAnnotationVisitor extends AnnotationVisitor {
        private PMDASMVisitor parent;

        PMDAnnotationVisitor(PMDASMVisitor visitor) {
            super(Opcodes.ASM5);
            parent = visitor;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            parent.addType(Type.getType(desc));
            return this;
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            parent.addType(Type.getType(desc));
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return this;
        }

        @Override
        public void visit(String name, Object value) {
            if (value instanceof Type) {
                parent.addType((Type) value);
            }
        }
    }

    private static class PMDSignatureVisitor extends SignatureVisitor {
        private PMDASMVisitor parent;

        PMDSignatureVisitor(PMDASMVisitor visitor) {
            super(Opcodes.ASM5);
            this.parent = visitor;
        }

        @Override
        public SignatureVisitor visitClassBound() {
            return this;
        }

        @Override
        public SignatureVisitor visitInterfaceBound() {
            return this;
        }

        @Override
        public SignatureVisitor visitSuperclass() {
            return this;
        }

        @Override
        public SignatureVisitor visitInterface() {
            return this;
        }

        @Override
        public SignatureVisitor visitParameterType() {
            return this;
        }

        @Override
        public SignatureVisitor visitReturnType() {
            return this;
        }

        @Override
        public SignatureVisitor visitExceptionType() {
            return this;
        }

        @Override
        public SignatureVisitor visitArrayType() {
            return this;
        }

        @Override
        public void visitClassType(String name) {
            parent.parseClassName(name);
        }

        @Override
        public void visitInnerClassType(String name) {
            // parent.parseClassName(name);
        }

        @Override
        public SignatureVisitor visitTypeArgument(char wildcard) {
            return this;
        }
    }

    private static class PMDMethodVisitor extends MethodVisitor {
        private PMDASMVisitor parent;

        PMDMethodVisitor(PMDASMVisitor visitor) {
            super(Opcodes.ASM5);
            parent = visitor;
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            parent.addType(Type.getType(desc));
            return parent.annotationVisitor;
        }

        @Override
        public void visitTypeInsn(int opcode, String desc) {
            if (desc.charAt(0) == '[') {
                parent.addType(Type.getType(desc));
            } else {
                parent.parseClassName(desc);
            }
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            parent.parseClassName(owner);
            parent.addType(Type.getType(desc));
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            parent.parseClassName(owner);
            parent.addMethodDesc(desc);
        }

        /**
         * the constant to be loaded on the stack. This parameter must be a non
         * null Integer, a Float, a Long, a Double a String (or a Type for
         * .class constants, for classes whose version is 49.0 or more).
         *
         * @see org.objectweb.asm.MethodVisitor#visitLdcInsn(java.lang.Object)
         */
        @Override
        public void visitLdcInsn(Object cst) {
            if (cst instanceof Type) {
                parent.addType((Type) cst);
            } else if (cst instanceof String) {
                parent.parseClassName((String) cst);
            }
        }

        @Override
        public void visitMultiANewArrayInsn(String desc, int dims) {
            parent.addType(Type.getType(desc));
        }

        @Override
        public void visitLocalVariable(String name, String desc, String sig, Label start, Label end, int index) {
            parent.extractSignature(sig);
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            parent.parseClassName(type);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return parent.annotationVisitor;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            parent.addType(Type.getType(desc));
            return parent.annotationVisitor;
        }
    }
}
