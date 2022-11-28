/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.StringUtil;

public class InvalidJavaBeanRule extends AbstractJavaRule {
    private static final String LOMBOK_DATA = "lombok.Data";
    private static final String LOMBOK_GETTER = "lombok.Getter";
    private static final String LOMBOK_SETTER = "lombok.Setter";

    private static final PropertyDescriptor<Boolean> ENSURE_SERIALIZATION = PropertyFactory.booleanProperty("ensureSerialization")
            .desc("Require that beans implement java.io.Serializable.")
            .defaultValue(false)
            .build();

    private static final PropertyDescriptor<List<String>> PACKAGES_DESCRIPTOR = stringListProperty("packages")
            .desc("Consider classes in only these package to be beans. Set to an empty value to check all classes.")
            .defaultValues("org.example.beans")
            .delim(',')
            .build();

    private Map<String, PropertyInfo> properties;

    public InvalidJavaBeanRule() {
        definePropertyDescriptor(ENSURE_SERIALIZATION);
        definePropertyDescriptor(PACKAGES_DESCRIPTOR);
        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        String packageName = "";
        ASTPackageDeclaration packageDeclaration = node.getRoot().getPackageDeclaration();
        if (packageDeclaration != null) {
            packageName = packageDeclaration.getName();
        }
        List<String> packages = getProperty(PACKAGES_DESCRIPTOR);
        if (!packages.isEmpty() && !packages.contains(packageName)) {
            // skip analysis outside the configured packages
            return null;
        }

        String beanName = node.getSimpleName();

        if (getProperty(ENSURE_SERIALIZATION) && !TypeTestUtil.isA(Serializable.class, node)) {
            asCtx(data).addViolationWithMessage(node, "The bean ''{0}'' does not implement java.io.Serializable.",
                    beanName);
        }

        if (!hasNoArgConstructor(node)) {
            asCtx(data).addViolationWithMessage(node, "The bean ''{0}'' is missing a no-arg constructor.",
                    beanName);
        }

        if (hasClassLevelLombokDataAnnotation(node)) {
            // skip further analysis
            return null;
        }

        properties = new HashMap<>();
        collectFields(node);
        collectMethods(node);

        for (PropertyInfo propertyInfo : properties.values()) {
            if (!hasClassLevelLombokGetterAnnotation(node) && !hasClassLevelLombokSetterAnnotation(node)
                && propertyInfo.hasMissingGetter() && propertyInfo.hasMissingSetter()) {
                asCtx(data).addViolationWithMessage(propertyInfo.getDeclaratorId(),
                        "The bean ''{0}'' is missing a getter and a setter for property ''{1}''.",
                        beanName, propertyInfo.getName());
            } else if (!hasClassLevelLombokGetterAnnotation(node) && propertyInfo.hasMissingGetter()) {
                asCtx(data).addViolationWithMessage(propertyInfo.getDeclaratorId(),
                        "The bean ''{0}'' is missing a getter for property ''{1}''.",
                        beanName, propertyInfo.getName());

            } else if (!hasClassLevelLombokSetterAnnotation(node) && propertyInfo.hasMissingSetter()) {
                asCtx(data).addViolationWithMessage(propertyInfo.getDeclaratorId(),
                        "The bean ''{0}'' is missing a setter for property ''{1}''.",
                        beanName, propertyInfo.getName());

            }

            if (propertyInfo.hasWrongGetterType()) {
                asCtx(data).addViolationWithMessage(propertyInfo.getGetter(),
                        "The bean ''{0}'' should return a ''{1}'' in getter of property ''{2}''.",
                        beanName, propertyInfo.getTypeName(), propertyInfo.getName());
            }
            if (propertyInfo.hasWrongBooleanGetterName()) {
                asCtx(data).addViolationWithMessage(propertyInfo.getGetter(),
                        "The bean ''{0}'' should use the method name ''is{1}'' for the getter of property ''{2}''.",
                        beanName, propertyInfo.getName(), propertyInfo.getName());
            }
            if (propertyInfo.hasWrongTypeGetterAndSetter()) {
                asCtx(data).addViolationWithMessage(propertyInfo.getGetter(),
                        "The bean ''{0}'' has a property ''{1}'' with getter and setter that don''t have the same type.",
                        beanName, propertyInfo.getName());
            }
            if (propertyInfo.hasWrongIndexedGetterType()) {
                asCtx(data).addViolationWithMessage(propertyInfo.indexedGetter,
                        "The bean ''{0}'' has a property ''{1}'' with an indexed getter using the wrong type.",
                        beanName, propertyInfo.getName());
            }
            if (propertyInfo.hasWrongIndexedSetterType()) {
                asCtx(data).addViolationWithMessage(propertyInfo.indexedSetter,
                        "The bean ''{0}'' has a property ''{1}'' with an indexed setter using the wrong type.",
                        beanName, propertyInfo.getName());
            }
        }

        return null;
    }

    private void collectFields(ASTClassOrInterfaceDeclaration node) {
        Map<VariableNameDeclaration, List<NameOccurrence>> declarations = node.getScope().getDeclarations(VariableNameDeclaration.class);
        for (VariableNameDeclaration declaration : declarations.keySet()) {
            String propertyName = StringUtils.capitalize(declaration.getName());
            if (declaration.getAccessNodeParent() instanceof ASTFieldDeclaration) {
                ASTFieldDeclaration fieldDeclaration = (ASTFieldDeclaration) declaration.getAccessNodeParent();
                if (!fieldDeclaration.isStatic() && !fieldDeclaration.isTransient()) {
                    PropertyInfo field = getOrCreatePropertyInfo(propertyName);
                    field.setDeclaratorId(declaration.getDeclaratorId());
                    field.setReadonly(fieldDeclaration.isFinal());
                }
            }
        }
    }

    private PropertyInfo getOrCreatePropertyInfo(String propertyName) {
        PropertyInfo propertyInfo = properties.get(propertyName);
        if (propertyInfo == null) {
            propertyInfo = new PropertyInfo(propertyName);
            properties.put(propertyName, propertyInfo);
        }
        return propertyInfo;
    }

    private void collectMethods(ASTClassOrInterfaceDeclaration node) {
        Map<MethodNameDeclaration, List<NameOccurrence>> declarations = node.getScope().getDeclarations(MethodNameDeclaration.class);
        for (MethodNameDeclaration declaration : declarations.keySet()) {
            ASTMethodDeclaration methodDeclaration = declaration.getMethodNameDeclaratorNode().getParent();
            String methodName = declaration.getName();
            int parameterCount = declaration.getParameterCount();
            String propertyName = StringUtil.withoutPrefixes(methodName, "get", "set", "is");

            if (methodName.startsWith("get") || methodName.startsWith("is")) {
                if (parameterCount == 0) {
                    PropertyInfo propertyInfo = getOrCreatePropertyInfo(propertyName);
                    propertyInfo.setGetter(methodDeclaration);
                } else if (parameterCount == 1 && getFirstParameterType(methodDeclaration) == Integer.TYPE) {
                    PropertyInfo propertyInfo = getOrCreatePropertyInfo(propertyName);
                    propertyInfo.setIndexedGetter(methodDeclaration);
                }
            } else if (methodName.startsWith("set")) {
                if (parameterCount == 1) {
                    PropertyInfo propertyInfo = getOrCreatePropertyInfo(propertyName);
                    propertyInfo.setSetter(methodDeclaration);
                } else if (parameterCount == 2 && getFirstParameterType(methodDeclaration) == Integer.TYPE) {
                    PropertyInfo propertyInfo = getOrCreatePropertyInfo(propertyName);
                    propertyInfo.setIndexedSetter(methodDeclaration);
                }
            }
        }
    }

    private static Class<?> getFirstParameterType(ASTMethodDeclaration declaration) {
        return getParameterType(declaration, 0);
    }

    private static Class<?> getParameterType(ASTMethodDeclaration declaration, int i) {
        if (declaration.getArity() >= i + 1) {
            ASTFormalParameter firstParameter = declaration.getFormalParameters().findChildrenOfType(ASTFormalParameter.class).get(i);
            return firstParameter.getType();
        }
        return null;
    }

    private static Class<?> getResultType(ASTMethodDeclaration declaration) {
        ASTResultType resultType = declaration.getResultType();
        if (resultType.isVoid()) {
            return Void.class;
        }
        return resultType.getFirstChildOfType(ASTType.class).getType();
    }

    private boolean hasNoArgConstructor(ASTClassOrInterfaceDeclaration node) {
        int constructorCount = 0;
        for (ASTAnyTypeBodyDeclaration declaration : node.getDeclarations()) {
            if (ASTAnyTypeBodyDeclaration.DeclarationKind.CONSTRUCTOR == declaration.getKind()) {
                ASTConstructorDeclaration ctor = declaration.getFirstChildOfType(ASTConstructorDeclaration.class);
                if (ctor.getArity() == 0) {
                    return true;
                }
                constructorCount++;
            }
        }
        // default constructor is ok
        return constructorCount == 0;
    }

    private boolean hasClassLevelLombokDataAnnotation(ASTClassOrInterfaceDeclaration node) {
        return node.isAnnotationPresent(LOMBOK_DATA);
    }

    private boolean hasClassLevelLombokGetterAnnotation(ASTClassOrInterfaceDeclaration node) {
        return node.isAnnotationPresent(LOMBOK_GETTER);
    }

    private boolean hasClassLevelLombokSetterAnnotation(ASTClassOrInterfaceDeclaration node) {
        return node.isAnnotationPresent(LOMBOK_SETTER);
    }

    private static class PropertyInfo {
        private final String name;
        private ASTVariableDeclaratorId declaratorId;
        private boolean readonly;
        private ASTMethodDeclaration getter;
        private ASTMethodDeclaration indexedGetter;
        private ASTMethodDeclaration setter;
        private ASTMethodDeclaration indexedSetter;

        PropertyInfo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public ASTVariableDeclaratorId getDeclaratorId() {
            return declaratorId;
        }

        public void setDeclaratorId(ASTVariableDeclaratorId declaratorId) {
            this.declaratorId = declaratorId;
        }

        public boolean isReadonly() {
            return readonly;
        }

        public void setReadonly(boolean readonly) {
            this.readonly = readonly;
        }

        public ASTMethodDeclaration getGetter() {
            return getter;
        }

        public void setGetter(ASTMethodDeclaration getter) {
            this.getter = getter;
        }

        public ASTMethodDeclaration getIndexedGetter() {
            return indexedGetter;
        }

        public void setIndexedGetter(ASTMethodDeclaration indexedGetter) {
            this.indexedGetter = indexedGetter;
        }

        public ASTMethodDeclaration getSetter() {
            return setter;
        }

        public void setSetter(ASTMethodDeclaration setter) {
            this.setter = setter;
        }

        public ASTMethodDeclaration getIndexedSetter() {
            return indexedSetter;
        }

        public void setIndexedSetter(ASTMethodDeclaration indexedSetter) {
            this.indexedSetter = indexedSetter;
        }

        private boolean hasMissingGetter() {
            return declaratorId != null && getter == null && !hasFieldLombokGetter();
        }

        private boolean hasMissingSetter() {
            return declaratorId != null && !readonly && setter == null && !hasFieldLombokSetter();
        }

        private String getTypeName() {
            Class<?> type = null;
            if (declaratorId != null && declaratorId.getType() != null) {
                type = declaratorId.getType();
            } else if (getter != null) {
                type = getResultType(getter);
            } else if (setter != null) {
                type = getFirstParameterType(setter);
            }
            if (type != null) {
                if (type.isArray()) {
                    return type.getComponentType().getName() + "[]";
                }
                return type.getName();
            }
            return "<unknown type>";
        }

        private boolean hasWrongGetterType() {
            return declaratorId != null
                    && declaratorId.getType() != null
                    && getter != null
                    && !getter.getResultType().isVoid()
                    && declaratorId.getType() != getResultType(getter);
        }

        private boolean hasWrongBooleanGetterName() {
            if (getter != null && (TypeTestUtil.isA(Boolean.class, declaratorId) || TypeTestUtil.isA(Boolean.TYPE, declaratorId))) {
                return !getter.getName().startsWith("is");
            }
            return false;
        }

        private boolean hasWrongTypeGetterAndSetter() {
            if (declaratorId != null || getter == null || setter == null) {
                return false;
            }
            Class<?> parameterType = getFirstParameterType(setter);
            return getter.getResultType().isVoid()
                    || getResultType(getter) != parameterType;
        }

        private boolean hasWrongIndexedGetterType() {
            if (getter == null || indexedGetter == null) {
                return false;
            }
            Class<?> propertyType = getResultType(getter);
            if (propertyType != null && propertyType.isArray()) {
                propertyType = propertyType.getComponentType();
            }
            Class<?> getterType = getResultType(indexedGetter);
            return propertyType != getterType;
        }

        private boolean hasWrongIndexedSetterType() {
            if (setter == null || indexedSetter == null) {
                return false;
            }
            Class<?> propertyType = getFirstParameterType(setter);
            if (propertyType != null && propertyType.isArray()) {
                propertyType = propertyType.getComponentType();
            }
            Class<?> setterType = getParameterType(indexedSetter, 1);
            return propertyType != setterType;
        }

        private boolean hasFieldLombokGetter() {
            ASTFieldDeclaration fieldDeclaration = declaratorId != null ? declaratorId.getFirstParentOfType(ASTFieldDeclaration.class) : null;
            if (fieldDeclaration != null) {
                return fieldDeclaration.isAnnotationPresent(LOMBOK_GETTER);
            }
            return false;
        }

        private boolean hasFieldLombokSetter() {
            ASTFieldDeclaration fieldDeclaration = declaratorId != null ? declaratorId.getFirstParentOfType(ASTFieldDeclaration.class) : null;
            if (fieldDeclaration != null) {
                return fieldDeclaration.isAnnotationPresent(LOMBOK_SETTER);
            }
            return false;
        }
    }
}
