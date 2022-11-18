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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.StringUtil;

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

    private static final String SERIAL_PERSISTENT_FIELDS_TYPE = "java.io.ObjectStreamField[]";
    private static final String SERIAL_PERSISTENT_FIELDS_NAME = "serialPersistentFields";

    private Map<ASTAnyTypeDeclaration, Set<String>> cachedPersistentFieldNames;

    public NonSerializableClassRule() {
        definePropertyDescriptor(PREFIX_DESCRIPTOR);
        definePropertyDescriptor(CHECK_ABSTRACT_TYPES);
        addRuleChainVisit(ASTVariableDeclaratorId.class);
        addRuleChainVisit(ASTTypeDeclaration.class);
    }

    @Override
    public void start(RuleContext ctx) {
        cachedPersistentFieldNames = new HashMap<>();
    }

    @Override
    public Object visit(ASTTypeDeclaration node, Object data) {
        ASTAnyTypeDeclaration anyTypeDeclaration = node.getFirstChildOfType(ASTAnyTypeDeclaration.class);
        for (ASTFieldDeclaration field : anyTypeDeclaration.findDescendantsOfType(ASTFieldDeclaration.class)) {
            for (ASTVariableDeclaratorId varId : field) {
                if (SERIAL_PERSISTENT_FIELDS_NAME.equals(varId.getName()) && varId.getType() != null) {
                    if (!TypeTestUtil.isA(SERIAL_PERSISTENT_FIELDS_TYPE, varId)
                            || !field.isPrivate()
                            || !field.isStatic()
                            || !field.isFinal()) {
                        asCtx(data).addViolationWithMessage(varId, "The field ''{0}'' should be private static final with type ''{1}''.",
                                varId.getName(), SERIAL_PERSISTENT_FIELDS_TYPE);
                    }
                }
            }
        }

        return null;
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

        if (isPersistentField(typeDeclaration, node) && isNotSerializable(node)) {
            asCtx(data).addViolation(node, node.getName(), typeDeclaration.getQualifiedName().toString(), getTypeName(node.getType()));
        }
        return null;
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

    private Set<String> determinePersistentFields(ASTAnyTypeDeclaration typeDeclaration) {
        if (cachedPersistentFieldNames.containsKey(typeDeclaration)) {
            return cachedPersistentFieldNames.get(typeDeclaration);
        }

        ASTVariableDeclarator persistentFieldsDecl = null;
        for (ASTFieldDeclaration field : typeDeclaration.findDescendantsOfType(ASTFieldDeclaration.class)) {
            if (TypeTestUtil.isA(SERIAL_PERSISTENT_FIELDS_TYPE, field)
                    && field.isPrivate() && field.isStatic() && field.isFinal()) {
                for (ASTVariableDeclaratorId varId : field) {
                    if (SERIAL_PERSISTENT_FIELDS_NAME.equals(varId.getName())) {
                        persistentFieldsDecl = varId.getFirstParentOfType(ASTVariableDeclarator.class);
                    }
                }
            }
        }
        Set<String> fields = null;
        if (persistentFieldsDecl != null) {
            fields = new HashSet<>();
            for (ASTLiteral literal : persistentFieldsDecl.findDescendantsOfType(ASTLiteral.class)) {
                if (literal.isStringLiteral()) {
                    fields.add(StringUtil.removeDoubleQuotes(literal.getImage()));
                }
            }
            if (fields.isEmpty()) {
                // field initializer might be a reference to a constant
                ASTName reference = persistentFieldsDecl.getFirstDescendantOfType(ASTName.class);
                if (reference != null && reference.getNameDeclaration() != null) {
                    for (ASTLiteral literal : reference.getNameDeclaration().getNode().getParent().findDescendantsOfType(ASTLiteral.class)) {
                        if (literal.isStringLiteral()) {
                            fields.add(StringUtil.removeDoubleQuotes(literal.getImage()));
                        }
                    }
                }
            }
        }

        cachedPersistentFieldNames.put(typeDeclaration, fields);
        return fields;
    }

    private boolean isPersistentField(ASTAnyTypeDeclaration typeDeclaration, ASTVariableDeclaratorId node) {
        Set<String> persistentFields = determinePersistentFields(typeDeclaration);

        if (node.isField() && (persistentFields == null || persistentFields.contains(node.getName()))) {
            ASTFieldDeclaration field = node.getFirstParentOfType(ASTFieldDeclaration.class);
            return !field.isStatic() && !field.isTransient();
        }
        return false;
    }
}
