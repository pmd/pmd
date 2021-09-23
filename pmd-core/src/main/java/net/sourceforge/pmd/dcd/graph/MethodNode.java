/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dcd.graph;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import org.objectweb.asm.signature.SignatureReader;

import net.sourceforge.pmd.dcd.ClassLoaderUtil;
import net.sourceforge.pmd.dcd.DCD;
import net.sourceforge.pmd.dcd.asm.TypeSignatureVisitor;

/**
 * Represents a Class Method in a UsageGraph.
 * @deprecated See {@link DCD}
 */
@Deprecated
public class MethodNode extends MemberNode<MethodNode, Method> {

    private WeakReference<Method> methodReference;

    public MethodNode(ClassNode classNode, String name, String desc) {
        super(classNode, name, desc);
        // getMember();
    }

    @Override
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

    @Override
    protected Class<?>[] getMemberParameterTypes() {
        return this.getMember().getParameterTypes();
    }
}
