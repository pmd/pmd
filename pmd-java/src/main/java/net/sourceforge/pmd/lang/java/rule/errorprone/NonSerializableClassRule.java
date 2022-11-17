/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringProperty;

import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

// Note: This rule has been formerly known as "BeanMembersShouldSerialize".
public class NonSerializableClassRule extends AbstractJavaRule {

    private static final PropertyDescriptor<String> PREFIX_DESCRIPTOR = stringProperty("prefix")
            .desc("deprecated! A variable prefix to skip, i.e., m_").defaultValue("").build();
    private static final PropertyDescriptor<Boolean> CHECK_ABSTRACT_TYPES = booleanProperty("checkAbstractTypes")
            .desc("Enable to verify fields with abstract types like abstract classes, interfaces, generic types "
                + "or java.lang.Object. Enabling this might lead to more false positives, since the concrete "
                + "runtime type can actually be serializable.")
            .defaultValue(false)
            .build();

    public NonSerializableClassRule() {
        definePropertyDescriptor(PREFIX_DESCRIPTOR);
        definePropertyDescriptor(CHECK_ABSTRACT_TYPES);
        addRuleChainVisit(ASTVariableDeclaratorId.class);
    }

    private boolean hasManualSerializationMethod(ASTAnyTypeDeclaration node) {
        boolean hasWriteObject = false;
        boolean hasReadObject = false;
        boolean hasWriteReplace = false;
        boolean hasReadResolve = false;
        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.getKind() == ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD) {
                ASTMethodDeclaration methodDeclaration = decl.getFirstChildOfType(ASTMethodDeclaration.class);
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
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        ASTAnyTypeDeclaration typeDeclaration = node.getFirstParentOfType(ASTAnyTypeDeclaration.class);

        if (typeDeclaration == null
                // ignore non-serializable classes
                || !TypeTestUtil.isA(Serializable.class, typeDeclaration)
                // ignore Externalizable classes explicitly
                || TypeTestUtil.isA(Externalizable.class, typeDeclaration)
                // ignore manual serialization
                || hasManualSerializationMethod(typeDeclaration)) {
            return null;
        }

        if (isNonStaticNonTransientField(node) && isNotSerializable(node)) {
            asCtx(data).addViolation(node, node.getName(), typeDeclaration.getQualifiedName().toString(), getTypeName(node.getType()));
        }
        return null;
    }

    private boolean isNotSerializable(TypeNode node) {
        Class<?> type = node.getType();
        boolean notSerializable = !TypeTestUtil.isA(Serializable.class, node)
                && type != null
                && !type.isPrimitive();
        if (!getProperty(CHECK_ABSTRACT_TYPES) && type != null) {
            // exclude java.lang.Object, interfaces, abstract classes, and generic types
            notSerializable &= !TypeTestUtil.isExactlyA(Object.class, node)
                    && !type.isInterface()
                    && !Modifier.isAbstract(type.getModifiers())
                    && !isGenericType(node);
        }
        return notSerializable;
    }

    private boolean isGenericType(TypeNode node) {
        ASTClassOrInterfaceType typeRef = node.getFirstParentOfType(ASTFieldDeclaration.class).getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (typeRef != null && typeRef.getTypeDefinition() != null) {
            return typeRef.getTypeDefinition().getDefinitionType() != TypeDefinitionType.EXACT;
        }
        return false;
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
