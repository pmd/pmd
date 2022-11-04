/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.stringProperty;

import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class NonSerializableClassRule extends AbstractJavaRule {

    private String currentClassName;

    private static final PropertyDescriptor<String> PREFIX_DESCRIPTOR = stringProperty("prefix")
            .desc("deprecated! A variable prefix to skip, i.e., m_").defaultValue("").build();

    public NonSerializableClassRule() {
        definePropertyDescriptor(PREFIX_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        // ignore non-serializable classes
        if (!TypeTestUtil.isA(Serializable.class, node)
            // ignore Externalizable classes explicitly
            || TypeTestUtil.isA(Externalizable.class, node)
            // ignore manual serialization
            || hasManualSerializationMethod(node)) {
            return null;
        }

        currentClassName = node.getSimpleName();
        return super.visit(node, data);
    }

    private boolean hasManualSerializationMethod(ASTClassOrInterfaceDeclaration node) {
        boolean hasWriteObject = false;
        boolean hasReadObject = false;
        boolean hasWriteReplace = false;
        boolean hasReadResolve = false;
        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.getKind() == ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD) {
                ASTMethodDeclaration methodDeclaration = (ASTMethodDeclaration) decl.getChild(0);
                String methodName = methodDeclaration.getName();
                int parameterCount = methodDeclaration.getFormalParameters().size();
                ASTFormalParameter firstParameter = methodDeclaration.getFormalParameters().getFirstChildOfType(ASTFormalParameter.class);
                ASTType resultType = methodDeclaration.getResultType().getFirstChildOfType(ASTType.class);

                hasWriteObject |= "writeObject".equals(methodName) && parameterCount == 1
                    && TypeTestUtil.isA(ObjectOutputStream.class, firstParameter)
                    && resultType == null;
                hasReadObject |= "readObject".equals(methodName) && parameterCount == 1
                    && TypeTestUtil.isA(ObjectInputStream.class, firstParameter)
                    && resultType == null;
                hasWriteReplace |= "writeReplace".equals(methodName) && parameterCount == 0
                        && TypeTestUtil.isExactlyA(Object.class, resultType);
                hasReadResolve |= "readResolve".equals(methodName) && parameterCount == 0
                        && TypeTestUtil.isExactlyA(Object.class, resultType);
            }
        }

        return hasWriteObject && hasReadObject || hasWriteReplace && hasReadResolve;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (isNonStaticNonTransientField(node) && isNotSerializable(node)) {
            asCtx(data).addViolation(node, node.getName(), currentClassName, getTypeName(node.getType()));
        }
        return super.visit(node, data);
    }

    private boolean isNotSerializable(TypeNode node) {
        return !TypeTestUtil.isA(Serializable.class, node)
                && node.getType() != null && !node.getType().isPrimitive();
    }

    private String getTypeName(Class<?> clazz) {
        return clazz != null ? clazz.getName() : "<unknown>";
    }

    private boolean isNonStaticNonTransientField(ASTVariableDeclaratorId node) {
        if (node.isField()) {
            ASTFieldDeclaration field = node.getFirstParentOfType(ASTFieldDeclaration.class);
            return !field.isStatic() && !field.isTransient();
        }
        return false;
    }
}
