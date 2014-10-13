/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sourceforge.pmd.dcd.graph.ClassNode;
import net.sourceforge.pmd.dcd.graph.ConstructorNode;
import net.sourceforge.pmd.dcd.graph.FieldNode;
import net.sourceforge.pmd.dcd.graph.MemberNode;
import net.sourceforge.pmd.dcd.graph.MethodNode;
import net.sourceforge.pmd.dcd.graph.NodeVisitorAdapter;
import net.sourceforge.pmd.dcd.graph.UsageGraph;

/**
 * Perform a visitation a UsageGraph, looking for <em>dead code</em>, which
 * is essential code which is not used by any other code.  There are various
 * options for configuration how this determination is made.
 */
public class UsageNodeVisitor extends NodeVisitorAdapter {

	/**
	 * Configuration options for usage analysis.
	 */
	public static final class Options {
		private boolean ignoreClassAnonymous = true;

		private boolean ignoreConstructorStaticInitializer = true;

		private boolean ignoreConstructorSinglePrivateNoArg = true;

		private boolean ignoreConstructorAllPrivate = false;

		private boolean ignoreMethodJavaLangObjectOverride = true;

		private boolean ignoreMethodAllOverride = false;

		private boolean ignoreMethodMain = true;

		private boolean ignoreFieldInlinable = true;

		public boolean isIgnoreClassAnonymous() {
			return ignoreClassAnonymous;
		}

		public void setIgnoreClassAnonymous(boolean ignoreClassAnonymous) {
			this.ignoreClassAnonymous = ignoreClassAnonymous;
		}

		public boolean isIgnoreConstructorStaticInitializer() {
			return ignoreConstructorStaticInitializer;
		}

		public void setIgnoreConstructorStaticInitializer(boolean ignoreConstructorStaticInitializer) {
			this.ignoreConstructorStaticInitializer = ignoreConstructorStaticInitializer;
		}

		public boolean isIgnoreConstructorSinglePrivateNoArg() {
			return ignoreConstructorSinglePrivateNoArg;
		}

		public void setIgnoreConstructorSinglePrivateNoArg(boolean ignoreConstructorSinglePrivateNoArg) {
			this.ignoreConstructorSinglePrivateNoArg = ignoreConstructorSinglePrivateNoArg;
		}

		public boolean isIgnoreConstructorAllPrivate() {
			return ignoreConstructorAllPrivate;
		}

		public void setIgnoreConstructorAllPrivate(boolean ignoreConstructorAllPrivate) {
			this.ignoreConstructorAllPrivate = ignoreConstructorAllPrivate;
		}

		public boolean isIgnoreMethodJavaLangObjectOverride() {
			return ignoreMethodJavaLangObjectOverride;
		}

		public void setIgnoreMethodJavaLangObjectOverride(boolean ignoreMethodJavaLangObjectOverride) {
			this.ignoreMethodJavaLangObjectOverride = ignoreMethodJavaLangObjectOverride;
		}

		public boolean isIgnoreMethodAllOverride() {
			return ignoreMethodAllOverride;
		}

		public void setIgnoreMethodAllOverride(boolean ignoreMethodAllOverride) {
			this.ignoreMethodAllOverride = ignoreMethodAllOverride;
		}

		public boolean isIgnoreMethodMain() {
			return ignoreMethodMain;
		}

		public void setIgnoreMethodMain(boolean ignoreMethodMain) {
			this.ignoreMethodMain = ignoreMethodMain;
		}

		public boolean isIgnoreFieldInlinable() {
			return ignoreFieldInlinable;
		}

		public void setIgnoreFieldInlinable(boolean ignoreFieldInlinable) {
			this.ignoreFieldInlinable = ignoreFieldInlinable;
		}

	}

	private final Options options = new Options();

	public Object visit(UsageGraph usageGraph, Object data) {
		System.out.println("----------------------------------------");
		super.visit(usageGraph, data);
		System.out.println("----------------------------------------");
		return data;
	}

	public Object visit(ClassNode classNode, Object data) {
		boolean log = true;
		if (options.isIgnoreClassAnonymous() && classNode.getType().isAnonymousClass()) {
			ignore("class anonymous", classNode);
			log = false;
		}
		if (log) {
			System.out.println("--- " + classNode.getName() + " ---");
			return super.visit(classNode, data);
		} else {
			return data;
		}
	}

	public Object visit(FieldNode fieldNode, Object data) {
		if (fieldNode.getUsers().isEmpty()) {
			boolean log = true;
			// A field is inlinable if:
			// 1) It is final
			// 2) It is a primitive, or a java.lang.String
			if (options.isIgnoreFieldInlinable()) {
				if (Modifier.isFinal(fieldNode.getMember().getModifiers())
						&& fieldNode.getMember().getType().isPrimitive()
						|| fieldNode.getMember().getType().getName().equals("java.lang.String")) {
					ignore("field inlinable", fieldNode);
					log = false;
				}
			}
			if (log) {
				System.out.println("\t" + fieldNode.toStringLong());
			}
		}
		return super.visit(fieldNode, data);
	}

	public Object visit(ConstructorNode constructorNode, Object data) {
		if (constructorNode.getUsers().isEmpty()) {
			boolean log = true;
			if (constructorNode.isStaticInitializer()) {
				if (options.isIgnoreConstructorStaticInitializer()) {
					ignore("constructor static initializer", constructorNode);
					log = false;
				}
			} else if (constructorNode.isInstanceInitializer()) {
				if (Modifier.isPrivate(constructorNode.getMember().getModifiers())) {
					if (options.isIgnoreConstructorAllPrivate()) {
						ignore("constructor all private", constructorNode);
						log = false;
					} else if (options.isIgnoreConstructorSinglePrivateNoArg()
							&& constructorNode.getMember().getParameterTypes().length == 0
							&& constructorNode.getClassNode().getConstructorNodes().size() == 1) {
						ignore("constructor single private no-arg", constructorNode);
						log = false;
					}
				}
			}
			if (log) {
				System.out.println("\t" + constructorNode.toStringLong());
			}
		}
		return super.visit(constructorNode, data);
	}

	private static boolean isMainMethod(MethodNode node) {
		
		final Method method = node.getMember();
		
		return method.getName().equals("main")
			&& Modifier.isPublic(method.getModifiers())
			&& Modifier.isStatic(method.getModifiers())
			&& method.getReturnType() == Void.TYPE
			&& method.getParameterTypes().length == 1
			&& method.getParameterTypes()[0].isArray()
			&& method.getParameterTypes()[0].getComponentType().equals(java.lang.String.class);
	}
	
	
	public Object visit(MethodNode methodNode, Object data) {
		if (methodNode.getUsers().isEmpty()) {
			boolean log = true;
			if (options.isIgnoreMethodAllOverride()) {
				if (ClassLoaderUtil.isOverridenMethod(methodNode.getClassNode().getClass(), methodNode.getMember(),
						false)) {
					ignore("method all override", methodNode);
					log = false;
				}
			} else if (options.isIgnoreMethodJavaLangObjectOverride()) {
				if (ClassLoaderUtil.isOverridenMethod(java.lang.Object.class, methodNode.getMember(), true)) {
					ignore("method java.lang.Object override", methodNode);
					log = false;
				}
			}
			if (options.isIgnoreMethodMain()) {
				if (isMainMethod(methodNode)) {
					ignore("method public static void main(String[])", methodNode);
					log = false;
				}
			}
			if (log) {
				System.out.println("\t" + methodNode.toStringLong());
			}
		}
		return super.visit(methodNode, data);
	}

	private void ignore(String description, ClassNode classNode) {
		System.out.println("Ignoring " + description + ": " + classNode.getName());
	}

	private void ignore(String description, MemberNode memberNode) {
		System.out.println("Ignoring " + description + ": " + memberNode.toStringLong());
	}
}
