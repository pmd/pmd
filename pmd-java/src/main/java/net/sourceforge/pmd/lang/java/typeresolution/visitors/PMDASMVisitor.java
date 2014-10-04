/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution.visitors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PMDASMVisitor extends ClassVisitor {

    public PMDASMVisitor() {
        super(Opcodes.ASM4);
    }

    private Map<String, String> packages = new HashMap<String, String>();

	private AnnotationVisitor annotationVisitor = new PMDAnnotationVisitor(this);

	private FieldVisitor fieldVisitor = new PMDFieldVisitor(this);

	private SignatureVisitor sigVisitor = new PMDSignatureVisitor(this);

	private MethodVisitor methodVisitor = new PMDMethodVisitor(this);

	public List<String> innerClasses;

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
			//TODO I don't think the first one, with Class$Inner is needed - come back and check
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

	public void visit(int version, int access, String name, String sig, String superName, String[] interfaces) {
		parseClassName(name);
		parseClassName(interfaces);
		if (sig != null) {
			extractSignature(sig);
		}
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		addType(Type.getType(desc));
		return annotationVisitor;
	}

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

	public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
		if (sig != null) {
			extractSignature(sig);
		}
		addMethodDesc(desc);
		parseClassName(exceptions);
		return methodVisitor;
	}

	public void visitSource(String source, String debug) {
	}

	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		if (innerClasses == null) {
			innerClasses = new ArrayList<String>();
		}
		if (!innerClasses.contains(name.replace('/', '.'))) {
			innerClasses.add(name.replace('/', '.'));
		}
		packages.put(innerName, name.replace('/', '.'));
	}

	public void visitOuterClass(String owner, String name, String desc) {
	}

	public void visitEnd() {
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

	public void visitAttribute(Attribute attr) {
	}

	/*
	 * Start visitors
	 */

	private static class PMDFieldVisitor extends FieldVisitor {

		private PMDASMVisitor parent;

		public PMDFieldVisitor(PMDASMVisitor visitor) {
		    super(Opcodes.ASM4);
			parent = visitor;
		}

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			parent.addType(Type.getType(desc));
			return parent.annotationVisitor;
		}

		public void visitAttribute(Attribute attr) {
		}

		public void visitEnd() {
		}
	}

	private static class PMDAnnotationVisitor extends AnnotationVisitor {
		private PMDASMVisitor parent;

		public PMDAnnotationVisitor(PMDASMVisitor visitor) {
            super(Opcodes.ASM4);
			parent = visitor;
		}

		public AnnotationVisitor visitAnnotation(String name, String desc) {
			parent.addType(Type.getType(desc));
			return this;
		}

		public void visitEnum(String name, String desc, String value) {
			parent.addType(Type.getType(desc));
		}

		public AnnotationVisitor visitArray(String name) {
			return this;
		}

		public void visitEnd() {
		}

		public void visit(String name, Object value) {
			if (value instanceof Type) {
				parent.addType((Type) value);
			}
		}
	}

	private static class PMDSignatureVisitor extends SignatureVisitor {
		private PMDASMVisitor parent;

		public PMDSignatureVisitor(PMDASMVisitor visitor) {
            super(Opcodes.ASM4);
			this.parent = visitor;
		}

		public void visitFormalTypeParameter(String name) {
		}

		public SignatureVisitor visitClassBound() {
			return this;
		}

		public SignatureVisitor visitInterfaceBound() {
			return this;
		}

		public SignatureVisitor visitSuperclass() {
			return this;
		}

		public SignatureVisitor visitInterface() {
			return this;
		}

		public SignatureVisitor visitParameterType() {
			return this;
		}

		public SignatureVisitor visitReturnType() {
			return this;
		}

		public SignatureVisitor visitExceptionType() {
			return this;
		}

		public void visitBaseType(char descriptor) {
		}

		public void visitTypeVariable(String name) {
		}

		public SignatureVisitor visitArrayType() {
			return this;
		}

		public void visitClassType(String name) {
			parent.parseClassName(name);
		}

		public void visitInnerClassType(String name) {
			parent.parseClassName(name);
		}

		public void visitTypeArgument() {
		}

		public SignatureVisitor visitTypeArgument(char wildcard) {
			return this;
		}

		public void visitEnd() {
		}
	}

	private static class PMDMethodVisitor extends MethodVisitor {
		private PMDASMVisitor parent;

		public PMDMethodVisitor(PMDASMVisitor visitor) {
            super(Opcodes.ASM4);
			parent = visitor;
		}

		public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			parent.addType(Type.getType(desc));
			return parent.annotationVisitor;
		}

		public AnnotationVisitor visitAnnotation(String name, String desc) {
			parent.addType(Type.getType(desc));
			return parent.annotationVisitor;
		}

		public void visitTypeInsn(int opcode, String desc) {
			if (desc.charAt(0) == '[') {
				parent.addType(Type.getType(desc));
			} else {
				parent.parseClassName(desc);
			}
		}

		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			parent.parseClassName(owner);
			parent.addType(Type.getType(desc));
		}

		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			parent.parseClassName(owner);
			parent.addMethodDesc(desc);
		}

	    /**
	     * the constant to be loaded on the stack. This parameter must be a non null
	     * Integer, a Float, a Long, a Double a String (or a Type for .class
	     * constants, for classes whose version is 49.0 or more).
	     *
	     * @see org.objectweb.asm.MethodVisitor#visitLdcInsn(java.lang.Object)
	     */
	    public void visitLdcInsn(Object cst) {
	        if (cst instanceof Type) {
	        	parent.addType((Type) cst);
	        } else if (cst instanceof String) {
	            parent.parseClassName((String) cst);
	        }
	    }
		public void visitMultiANewArrayInsn(String desc, int dims) {
			parent.addType(Type.getType(desc));
		}

		public void visitLocalVariable(String name, String desc, String sig, Label start, Label end, int index) {
			parent.extractSignature(sig);
		}

		public void visitCode() {
		}

		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		}

		public void visitInsn(int opcode) {
		}

		public void visitIntInsn(int opcode, int operand) {
		}

		public void visitVarInsn(int opcode, int var) {
		}

		public void visitJumpInsn(int opcode, Label label) {
		}

		public void visitLabel(Label label) {
		}

		public void visitIincInsn(int var, int increment) {
		}

		public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
		}

		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		}

		public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
			parent.parseClassName(type);
		}

		public void visitLineNumber(int line, Label start) {
		}

		public void visitMaxs(int maxStack, int maxLocals) {
		}

		public AnnotationVisitor visitAnnotationDefault() {
			return parent.annotationVisitor;
		}

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			parent.addType(Type.getType(desc));
			return parent.annotationVisitor;
		}

		public void visitEnd() {
		}

		public void visitAttribute(Attribute attr) {
		}

	}
}