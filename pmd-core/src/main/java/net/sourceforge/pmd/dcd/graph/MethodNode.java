/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import net.sourceforge.pmd.dcd.ClassLoaderUtil;
import net.sourceforge.pmd.dcd.asm.TypeSignatureVisitor;

import org.objectweb.asm.signature.SignatureReader;

/**
 * Represents a Class Method in a UsageGraph.
 */
@SuppressWarnings("PMD.OverrideBothEqualsAndHashcode")
public class MethodNode extends MemberNode<MethodNode, Method> {

	private WeakReference<Method> methodReference;

	public MethodNode(ClassNode classNode, String name, String desc) {
		super(classNode, name, desc);
		// getMember();
	}

	public Method getMember() {
		Method method = methodReference == null ? null : methodReference.get();
		if (method == null) {
			SignatureReader signatureReader = new SignatureReader(desc);
			TypeSignatureVisitor visitor = new TypeSignatureVisitor();
			signatureReader.accept(visitor);
			method = ClassLoaderUtil.getMethod(super.getClassNode().getType(), name, visitor.getMethodParameterTypes());
			methodReference = new WeakReference<>(method);
		}
		return method;
	}

	public int compareTo(MethodNode that) {
		// Order by method name
		int cmp = this.getName().compareTo(that.getName());
		if (cmp == 0) {
			// Order by parameter list length
			cmp = this.getMember().getParameterTypes().length - that.getMember().getParameterTypes().length;
			if (cmp == 0) {
				// Order by parameter class name
				for (int i = 0; i < this.getMember().getParameterTypes().length; i++) {
					cmp = this.getMember().getParameterTypes()[i].getName().compareTo(
							that.getMember().getParameterTypes()[i].getName());
					if (cmp != 0) {
						break;
					}
				}
			}
		}
		return cmp;
	}

	public boolean equals(Object obj) {
		if (obj instanceof MethodNode) {
			MethodNode that = (MethodNode)obj;
			return super.equals(that);
		}
		return false;
	}
}
