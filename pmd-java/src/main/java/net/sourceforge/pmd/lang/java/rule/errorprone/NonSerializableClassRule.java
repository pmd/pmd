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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

// Note: This rule has been formerly known as "BeanMembersShouldSerialize".
public class NonSerializableClassRule extends AbstractJavaRulechainRule {

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
        super(ASTVariableDeclaratorId.class, ASTClassOrInterfaceDeclaration.class, ASTEnumDeclaration.class,
                ASTRecordDeclaration.class);
        definePropertyDescriptor(PREFIX_DESCRIPTOR);
        definePropertyDescriptor(CHECK_ABSTRACT_TYPES);
    }

    @Override
    public void start(RuleContext ctx) {
        cachedPersistentFieldNames = new HashMap<>();
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        checkSerialPersistentFieldsField(node, data);
        return null;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        checkSerialPersistentFieldsField(node, data);
        return null;
    }

    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        checkSerialPersistentFieldsField(node, data);
        return null;
    }

    private void checkSerialPersistentFieldsField(ASTAnyTypeDeclaration anyTypeDeclaration, Object data) {
        for (ASTFieldDeclaration field : anyTypeDeclaration.descendants(ASTFieldDeclaration.class)) {
            for (ASTVariableDeclaratorId varId : field) {
                if (SERIAL_PERSISTENT_FIELDS_NAME.equals(varId.getName())) {
                    if (!TypeTestUtil.isA(SERIAL_PERSISTENT_FIELDS_TYPE, varId)
                            || field.getVisibility() != AccessNode.Visibility.V_PRIVATE
                            || !field.hasModifiers(JModifier.STATIC)
                            || !field.hasModifiers(JModifier.FINAL)) {
                        asCtx(data).addViolationWithMessage(varId, "The field ''{0}'' should be private static final with type ''{1}''.",
                                varId.getName(), SERIAL_PERSISTENT_FIELDS_TYPE);
                    }
                }
            }
        }
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        ASTAnyTypeDeclaration typeDeclaration = node.ancestors(ASTAnyTypeDeclaration.class).first();

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
            asCtx(data).addViolation(node, node.getName(), typeDeclaration.getBinaryName(), node.getTypeMirror());
        }
        return null;
    }

    private boolean hasManualSerializationMethod(ASTAnyTypeDeclaration node) {
        boolean hasWriteObject = false;
        boolean hasReadObject = false;
        boolean hasWriteReplace = false;
        boolean hasReadResolve = false;
        for (ASTBodyDeclaration decl : node.getDeclarations().toList()) {
            if (decl instanceof ASTMethodDeclaration) {
                ASTMethodDeclaration methodDeclaration = (ASTMethodDeclaration) decl;
                String methodName = methodDeclaration.getName();
                int parameterCount = methodDeclaration.getFormalParameters().size();
                ASTFormalParameter firstParameter = parameterCount > 0 ? methodDeclaration.getFormalParameters().get(0) : null;
                ASTType resultType = methodDeclaration.getResultTypeNode();

                hasWriteObject |= "writeObject".equals(methodName) && parameterCount == 1
                        && TypeTestUtil.isA(ObjectOutputStream.class, firstParameter)
                        && resultType.isVoid();
                hasReadObject |= "readObject".equals(methodName) && parameterCount == 1
                        && TypeTestUtil.isA(ObjectInputStream.class, firstParameter)
                        && resultType.isVoid();
                hasWriteReplace |= "writeReplace".equals(methodName) && parameterCount == 0
                        && TypeTestUtil.isExactlyA(Object.class, resultType);
                hasReadResolve |= "readResolve".equals(methodName) && parameterCount == 0
                        && TypeTestUtil.isExactlyA(Object.class, resultType);
            }
        }

        return hasWriteObject && hasReadObject || hasWriteReplace && hasReadResolve;
    }

    private boolean isNotSerializable(TypeNode node) {
        JTypeMirror typeMirror = node.getTypeMirror();
        JTypeDeclSymbol typeSymbol = typeMirror.getSymbol();
        JClassSymbol classSymbol = null;
        if (typeSymbol instanceof JClassSymbol) {
            classSymbol = (JClassSymbol) typeSymbol;
        }
        boolean notSerializable = !TypeTestUtil.isA(Serializable.class, node)
                && !typeMirror.isPrimitive();
        if (!getProperty(CHECK_ABSTRACT_TYPES) && classSymbol != null) {
            // exclude java.lang.Object, interfaces, abstract classes
            notSerializable &= !TypeTestUtil.isExactlyA(Object.class, node)
                    && !classSymbol.isInterface()
                    && !classSymbol.isAbstract();
        }
        // exclude generic types
        if (!getProperty(CHECK_ABSTRACT_TYPES) && typeMirror instanceof JTypeVar) {
            notSerializable = false;
        }
        // exclude unresolved types in general
        if (typeSymbol != null && typeSymbol.isUnresolved()) {
            notSerializable = false;
        }
        return notSerializable;
    }

    private Set<String> determinePersistentFields(ASTAnyTypeDeclaration typeDeclaration) {
        if (cachedPersistentFieldNames.containsKey(typeDeclaration)) {
            return cachedPersistentFieldNames.get(typeDeclaration);
        }

        ASTVariableDeclarator persistentFieldsDecl = null;
        for (ASTFieldDeclaration field : typeDeclaration.descendants(ASTFieldDeclaration.class)) {
            if (field.getVisibility() == AccessNode.Visibility.V_PRIVATE
                && field.hasModifiers(JModifier.STATIC, JModifier.FINAL)) {
                for (ASTVariableDeclaratorId varId : field) {
                    if (TypeTestUtil.isA(SERIAL_PERSISTENT_FIELDS_TYPE, varId)
                        && SERIAL_PERSISTENT_FIELDS_NAME.equals(varId.getName())) {
                        persistentFieldsDecl = varId.ancestors(ASTVariableDeclarator.class).first();
                    }
                }
            }
        }
        Set<String> fields = null;
        if (persistentFieldsDecl != null) {
            fields = persistentFieldsDecl.descendants(ASTStringLiteral.class).toStream()
                        .map(ASTStringLiteral::getConstValue)
                        .collect(Collectors.toSet());
            if (fields.isEmpty()) {
                // field initializer might be a reference to a constant
                ASTExpression initializer = persistentFieldsDecl.getInitializer();
                if (initializer instanceof ASTVariableAccess) {
                    ASTVariableAccess variableAccess = (ASTVariableAccess) initializer;
                    ASTVariableDeclaratorId reference = variableAccess.getReferencedSym().tryGetNode();
                    fields = reference.getParent().descendants(ASTStringLiteral.class).toStream()
                            .map(ASTStringLiteral::getConstValue)
                            .collect(Collectors.toSet());
                }
            }
        }

        cachedPersistentFieldNames.put(typeDeclaration, fields);
        return fields;
    }

    private boolean isPersistentField(ASTAnyTypeDeclaration typeDeclaration, ASTVariableDeclaratorId node) {
        Set<String> persistentFields = determinePersistentFields(typeDeclaration);

        if (node.isField() && (persistentFields == null || persistentFields.contains(node.getName()))) {
            ASTFieldDeclaration field = node.ancestors(ASTFieldDeclaration.class).first();
            return field != null && !field.hasModifiers(JModifier.STATIC) && !field.hasModifiers(JModifier.TRANSIENT);
        }
        return false;
    }
}
