/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.dcd.asm.PrintVisitor;
import net.sourceforge.pmd.dcd.asm.TypeSignatureVisitor;
import net.sourceforge.pmd.util.filter.Filter;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;

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
					classReader.accept(getNewClassVisitor(), 0);
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
	    protected void println(String s) {
	        p.println(s);
	    }
	    protected void printlnIndent(String s) {
	        p.printlnIndent(s);
	    }
	    
	    public MyClassVisitor() {
	        super(Opcodes.ASM4);
	        p = new PrintVisitor();
	    }

	    private String className;

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

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if (TRACE) {
				println("visitAnnotation:");
				printlnIndent("desc: " + desc);
				printlnIndent("visible: " + visible);
			}
			return null;
		}

		public void visitAttribute(Attribute attr) {
			if (TRACE) {
				println("visitAttribute:");
				printlnIndent("attr: " + attr);
			}
		}

		public void visitEnd() {
			if (TRACE) {
				println("visitEnd:");
			}
		}

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

		public void visitOuterClass(String owner, String name, String desc) {
			if (TRACE) {
				println("visitOuterClass:");
				printlnIndent("owner: " + owner);
				printlnIndent("name: " + name);
				printlnIndent("desc: " + desc);
			}
		}

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
            super(Opcodes.ASM4);
            p = parent;
			this.usingMemberNode = usingMemberNode;
		}

		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if (TRACE) {
				println("visitAnnotation:");
				printlnIndent("desc: " + desc);
				printlnIndent("visible: " + visible);
			}
			return null;
		}

		public AnnotationVisitor visitAnnotationDefault() {
			if (TRACE) {
				println("visitAnnotationDefault:");
			}
			return null;
		}

		public void visitAttribute(Attribute attr) {
			if (TRACE) {
				println("visitAttribute:");
				printlnIndent("attr: " + attr);
			}
		}

		public void visitCode() {
			if (TRACE) {
				println("visitCode:");
			}
		}

		public void visitEnd() {
			if (TRACE) {
				println("visitEnd:");
			}
		}

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

		public void visitIincInsn(int var, int increment) {
			if (TRACE) {
				println("visitIincInsn:");
				printlnIndent("var: " + var);
				printlnIndent("increment: " + increment);
			}
		}

		public void visitInsn(int opcode) {
			if (TRACE) {
				println("visitInsn:");
				printlnIndent("opcode: " + opcode);
			}
		}

		public void visitIntInsn(int opcode, int operand) {
			if (TRACE) {
				println("visitIntInsn:");
				printlnIndent("opcode: " + opcode);
				printlnIndent("operand: " + operand);
			}
		}

		public void visitJumpInsn(int opcode, Label label) {
			if (TRACE) {
				println("visitJumpInsn:");
				printlnIndent("opcode: " + opcode);
				printlnIndent("label: " + label);
			}
		}

		public void visitLabel(Label label) {
			if (TRACE) {
				println("visitLabel:");
				printlnIndent("label: " + label);
			}
		}

		public void visitLdcInsn(Object cst) {
			if (TRACE) {
				println("visitLdcInsn:");
				printlnIndent("cst: " + cst);
			}
		}

		public void visitLineNumber(int line, Label start) {
			if (TRACE) {
				println("visitLineNumber:");
				printlnIndent("line: " + line);
				printlnIndent("start: " + start);
			}
		}

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

		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
			if (TRACE) {
				println("visitLookupSwitchInsn:");
				printlnIndent("dflt: " + dflt);
				printlnIndent("keys: " + asList(keys));
				printlnIndent("labels: " + asList(labels));
			}
		}

		public void visitMaxs(int maxStack, int maxLocals) {
			if (TRACE) {
				println("visitMaxs:");
				printlnIndent("maxStack: " + maxStack);
				printlnIndent("maxLocals: " + maxLocals);
			}
		}

		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			if (TRACE) {
				println("visitMethodInsn:");
				printlnIndent("opcode: " + opcode);
				printlnIndent("owner: " + owner);
				printlnIndent("name: " + name);
				printlnIndent("desc: " + desc);
			}
			if (INDEX) {
				String className = getClassName(owner);
				usageGraph.usageMethod(className, name, desc, usingMemberNode);
			}
		}

		public void visitMultiANewArrayInsn(String desc, int dims) {
			if (TRACE) {
				println("visitMultiANewArrayInsn:");
				printlnIndent("desc: " + desc);
				printlnIndent("dims: " + dims);
			}
		}

		public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			if (TRACE) {
				println("visitParameterAnnotation:");
				printlnIndent("parameter: " + parameter);
				printlnIndent("desc: " + desc);
				printlnIndent("visible: " + visible);
			}
			return null;
		}

		public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
			if (TRACE) {
				println("visitTableSwitchInsn:");
				printlnIndent("min: " + min);
				printlnIndent("max: " + max);
				printlnIndent("dflt: " + dflt);
				printlnIndent("labels: " + asList(labels));
			}
		}

		public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
			if (TRACE) {
				println("visitTryCatchBlock:");
				printlnIndent("start: " + start);
				printlnIndent("end: " + end);
				printlnIndent("handler: " + handler);
				printlnIndent("type: " + type);
			}
		}

		public void visitTypeInsn(int opcode, String desc) {
			if (TRACE) {
				println("visitTypeInsn:");
				printlnIndent("opcode: " + opcode);
				printlnIndent("desc: " + desc);
			}
		}

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
			list = new ArrayList<Integer>(array.length);
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
