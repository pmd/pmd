/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;

import net.sourceforge.pmd.dcd.asm.PrintVisitor;
import net.sourceforge.pmd.dcd.asm.TypeSignatureVisitor;
import net.sourceforge.pmd.util.filter.Filter;

/**
 * Utility class used to build a UsageGraph.
 */
public class UsageGraphBuilder {

    /**
     * Should trace level logging be enabled.  This is a development debugging
     * option.
     */
    private static final boolean TRACE = false;
    private static final boolean INDEX = true;

    protected final UsageGraph usageGraph;
    protected final Filter<String> classFilter;

    public UsageGraphBuilder(Filter<String> classFilter) {
        this.classFilter = classFilter;
        this.usageGraph = new UsageGraph(classFilter);
    }

    public void index(String name) {
        try {
            String className = getClassName(name);
            String classResourceName = getResourceName(name);
            if (classFilter.filter(className)) {
                if (!usageGraph.isClass(className)) {
                    usageGraph.defineClass(className);
                    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
                            classResourceName + ".class");
                    ClassReader classReader = new ClassReader(inputStream);
                    try {
                        classReader.accept(getNewClassVisitor(), 0);
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("For " + name + ": " + e.getMessage(), e);
        }
    }

    public UsageGraph getUsageGraph() {
        return usageGraph;
    }

    private ClassVisitor getNewClassVisitor() {
        return new MyClassVisitor();
    }

    // ASM visitor to on Class files to build a UsageGraph
    class MyClassVisitor extends ClassVisitor {
        private final PrintVisitor p;
        private String className;

        public MyClassVisitor() {
            super(Opcodes.ASM5);
            p = new PrintVisitor();
        }

        protected void println(String s) {
            p.println(s);
        }

        protected void printlnIndent(String s) {
            p.printlnIndent(s);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (TRACE) {
                println("visit:");
                printlnIndent("version: " + version);
                printlnIndent("access: " + access);
                printlnIndent("name: " + name);
                printlnIndent("signature: " + signature);
                printlnIndent("superName: " + superName);
                printlnIndent("interfaces: " + asList(interfaces));
            }
            this.className = getClassName(name);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (TRACE) {
                println("visitAnnotation:");
                printlnIndent("desc: " + desc);
                printlnIndent("visible: " + visible);
            }
            return null;
        }

        @Override
        public void visitAttribute(Attribute attr) {
            if (TRACE) {
                println("visitAttribute:");
                printlnIndent("attr: " + attr);
            }
        }

        @Override
        public void visitEnd() {
            if (TRACE) {
                println("visitEnd:");
            }
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if (TRACE) {
                println("visitField:");
                printlnIndent("access: " + access);
                printlnIndent("name: " + name);
                printlnIndent("desc: " + desc);
                printlnIndent("signature: " + signature);
                printlnIndent("value: " + value);
            }
            if (INDEX) {
                SignatureReader signatureReader = new SignatureReader(desc);
                TypeSignatureVisitor visitor = new TypeSignatureVisitor(p);
                signatureReader.acceptType(visitor);
                if (TRACE) {
                    printlnIndent("fieldType: " + visitor.getFieldType());
                }

                usageGraph.defineField(className, name, desc);
            }
            return null;
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            if (TRACE) {
                println("visitInnerClass:");
                printlnIndent("name: " + name);
                printlnIndent("outerName: " + outerName);
                printlnIndent("innerName: " + innerName);
                printlnIndent("access: " + access);
            }
            index(name);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MemberNode memberNode = null;
            if (TRACE) {
                println("visitMethod:");
                printlnIndent("access: " + access);
                printlnIndent("name: " + name);
                printlnIndent("desc: " + desc);
                printlnIndent("signature: " + signature);
                printlnIndent("exceptions: " + asList(exceptions));
            }
            if (INDEX) {
                memberNode = usageGraph.defineMethod(className, name, desc);
            }
            return getNewMethodVisitor(p, memberNode);
        }

        @Override
        public void visitOuterClass(String owner, String name, String desc) {
            if (TRACE) {
                println("visitOuterClass:");
                printlnIndent("owner: " + owner);
                printlnIndent("name: " + name);
                printlnIndent("desc: " + desc);
            }
        }

        @Override
        public void visitSource(String source, String debug) {
            if (TRACE) {
                println("visitSource:");
                printlnIndent("source: " + source);
                printlnIndent("debug: " + debug);
            }
        }
    }

    protected MethodVisitor getNewMethodVisitor(PrintVisitor parent, MemberNode usingMemberNode) {
        return new MyMethodVisitor(parent, usingMemberNode);
    }

    protected class MyMethodVisitor extends MethodVisitor {
        private final PrintVisitor p;

        protected void println(String s) {
            p.println(s);
        }

        protected void printlnIndent(String s) {
            p.printlnIndent(s);
        }

        private final MemberNode usingMemberNode;

        public MyMethodVisitor(PrintVisitor parent, MemberNode usingMemberNode) {
            super(Opcodes.ASM5);
            p = parent;
            this.usingMemberNode = usingMemberNode;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (TRACE) {
                println("visitAnnotation:");
                printlnIndent("desc: " + desc);
                printlnIndent("visible: " + visible);
            }
            return null;
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            if (TRACE) {
                println("visitAnnotationDefault:");
            }
            return null;
        }

        @Override
        public void visitAttribute(Attribute attr) {
            if (TRACE) {
                println("visitAttribute:");
                printlnIndent("attr: " + attr);
            }
        }

        @Override
        public void visitCode() {
            if (TRACE) {
                println("visitCode:");
            }
        }

        @Override
        public void visitEnd() {
            if (TRACE) {
                println("visitEnd:");
            }
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (TRACE) {
                println("visitFieldInsn:");
                printlnIndent("opcode: " + opcode);
                printlnIndent("owner: " + owner);
                printlnIndent("name: " + name);
                printlnIndent("desc: " + desc);
            }
            if (INDEX) {
                String className = getClassName(owner);
                usageGraph.usageField(className, name, desc, usingMemberNode);
            }
        }

        @Override
        public void visitFrame(int type, int local, Object[] local2, int stack, Object[] stack2) {
            if (TRACE) {
                println("visitFrame:");
                printlnIndent("type: " + type);
                printlnIndent("local: " + local);
                printlnIndent("local2: " + asList(local2));
                printlnIndent("stack: " + stack);
                printlnIndent("stack2: " + asList(stack2));
            }
        }

        @Override
        public void visitIincInsn(int var, int increment) {
            if (TRACE) {
                println("visitIincInsn:");
                printlnIndent("var: " + var);
                printlnIndent("increment: " + increment);
            }
        }

        @Override
        public void visitInsn(int opcode) {
            if (TRACE) {
                println("visitInsn:");
                printlnIndent("opcode: " + opcode);
            }
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            if (TRACE) {
                println("visitIntInsn:");
                printlnIndent("opcode: " + opcode);
                printlnIndent("operand: " + operand);
            }
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            if (TRACE) {
                println("visitJumpInsn:");
                printlnIndent("opcode: " + opcode);
                printlnIndent("label: " + label);
            }
        }

        @Override
        public void visitLabel(Label label) {
            if (TRACE) {
                println("visitLabel:");
                printlnIndent("label: " + label);
            }
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if (TRACE) {
                println("visitLdcInsn:");
                printlnIndent("cst: " + cst);
            }
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            if (TRACE) {
                println("visitLineNumber:");
                printlnIndent("line: " + line);
                printlnIndent("start: " + start);
            }
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            if (TRACE) {
                println("visitLocalVariable:");
                printlnIndent("name: " + name);
                printlnIndent("desc: " + desc);
                printlnIndent("signature: " + signature);
                printlnIndent("start: " + start);
                printlnIndent("end: " + end);
                printlnIndent("index: " + index);
            }
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            if (TRACE) {
                println("visitLookupSwitchInsn:");
                printlnIndent("dflt: " + dflt);
                printlnIndent("keys: " + asList(keys));
                printlnIndent("labels: " + asList(labels));
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            if (TRACE) {
                println("visitMaxs:");
                printlnIndent("maxStack: " + maxStack);
                printlnIndent("maxLocals: " + maxLocals);
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (TRACE) {
                println("visitMethodInsn:");
                printlnIndent("opcode: " + opcode);
                printlnIndent("owner: " + owner);
                printlnIndent("name: " + name);
                printlnIndent("desc: " + desc);
                printlnIndent("itf: " + itf);
            }
            if (INDEX) {
                String className = getClassName(owner);
                usageGraph.usageMethod(className, name, desc, usingMemberNode);
            }
        }

        @Override
        public void visitMultiANewArrayInsn(String desc, int dims) {
            if (TRACE) {
                println("visitMultiANewArrayInsn:");
                printlnIndent("desc: " + desc);
                printlnIndent("dims: " + dims);
            }
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            if (TRACE) {
                println("visitParameterAnnotation:");
                printlnIndent("parameter: " + parameter);
                printlnIndent("desc: " + desc);
                printlnIndent("visible: " + visible);
            }
            return null;
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt,
                Label... labels) {
            if (TRACE) {
                println("visitTableSwitchInsn:");
                printlnIndent("min: " + min);
                printlnIndent("max: " + max);
                printlnIndent("dflt: " + dflt);
                printlnIndent("labels: " + asList(labels));
            }
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            if (TRACE) {
                println("visitTryCatchBlock:");
                printlnIndent("start: " + start);
                printlnIndent("end: " + end);
                printlnIndent("handler: " + handler);
                printlnIndent("type: " + type);
            }
        }

        @Override
        public void visitTypeInsn(int opcode, String desc) {
            if (TRACE) {
                println("visitTypeInsn:");
                printlnIndent("opcode: " + opcode);
                printlnIndent("desc: " + desc);
            }
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (TRACE) {
                println("visitVarInsn:");
                printlnIndent("opcode: " + opcode);
                printlnIndent("var: " + var);
            }
        }
    }

    private static String getResourceName(String name) {
        return name.replace('.', '/');
    }

    static String getClassName(String name) {
        return name.replace('/', '.');
    }

    private static List<Integer> asList(int[] array) {
        List<Integer> list = null;
        if (array != null) {
            list = new ArrayList<>(array.length);
            for (int i : array) {
                list.add(i);
            }
        }
        return list;
    }

    private static List<Object> asList(Object[] array) {
        return array != null ? Arrays.asList(array) : null;
    }
}
