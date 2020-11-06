/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.List;
import java.util.Locale;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.StringMultiProperty;

@Deprecated
public class VariableNamingConventionsRule extends AbstractJavaRule {

    private boolean checkMembers;
    private boolean checkLocals;
    private boolean checkParameters;
    private boolean checkNativeMethodParameters;
    private List<String> staticPrefixes;
    private List<String> staticSuffixes;
    private List<String> memberPrefixes;
    private List<String> memberSuffixes;
    private List<String> localPrefixes;
    private List<String> localSuffixes;
    private List<String> parameterPrefixes;
    private List<String> parameterSuffixes;

    private static final PropertyDescriptor<Boolean> CHECK_MEMBERS_DESCRIPTOR = booleanProperty("checkMembers").defaultValue(true).desc("Check member variables").build();

    private static final PropertyDescriptor<Boolean> CHECK_LOCALS_DESCRIPTOR = booleanProperty("checkLocals").defaultValue(true).desc("Check local variables").build();

    private static final PropertyDescriptor<Boolean> CHECK_PARAMETERS_DESCRIPTOR = booleanProperty("checkParameters").defaultValue(true).desc("Check constructor and method parameter variables").build();

    private static final PropertyDescriptor<Boolean> CHECK_NATIVE_METHOD_PARAMETERS_DESCRIPTOR = booleanProperty("checkNativeMethodParameters").defaultValue(true).desc("Check method parameter of native methods").build();

    // the rule is deprecated and will be removed so its properties won't be converted
    private static final StringMultiProperty STATIC_PREFIXES_DESCRIPTOR = new StringMultiProperty("staticPrefix",
            "Static variable prefixes", new String[] { "" }, 4.0f, ',');

    private static final StringMultiProperty STATIC_SUFFIXES_DESCRIPTOR = new StringMultiProperty("staticSuffix",
            "Static variable suffixes", new String[] { "" }, 5.0f, ',');

    private static final StringMultiProperty MEMBER_PREFIXES_DESCRIPTOR = new StringMultiProperty("memberPrefix",
            "Member variable prefixes", new String[] { "" }, 6.0f, ',');

    private static final StringMultiProperty MEMBER_SUFFIXES_DESCRIPTOR = new StringMultiProperty("memberSuffix",
            "Member variable suffixes", new String[] { "" }, 7.0f, ',');

    private static final StringMultiProperty LOCAL_PREFIXES_DESCRIPTOR = new StringMultiProperty("localPrefix",
            "Local variable prefixes", new String[] { "" }, 8.0f, ',');

    private static final StringMultiProperty LOCAL_SUFFIXES_DESCRIPTOR = new StringMultiProperty("localSuffix",
            "Local variable suffixes", new String[] { "" }, 9.0f, ',');

    private static final StringMultiProperty PARAMETER_PREFIXES_DESCRIPTOR = new StringMultiProperty("parameterPrefix",
            "Method parameter variable prefixes", new String[] { "" }, 10.0f, ',');

    private static final StringMultiProperty PARAMETER_SUFFIXES_DESCRIPTOR = new StringMultiProperty("parameterSuffix",
            "Method parameter variable suffixes", new String[] { "" }, 11.0f, ',');

    public VariableNamingConventionsRule() {
        definePropertyDescriptor(CHECK_MEMBERS_DESCRIPTOR);
        definePropertyDescriptor(CHECK_LOCALS_DESCRIPTOR);
        definePropertyDescriptor(CHECK_PARAMETERS_DESCRIPTOR);
        definePropertyDescriptor(CHECK_NATIVE_METHOD_PARAMETERS_DESCRIPTOR);
        definePropertyDescriptor(STATIC_PREFIXES_DESCRIPTOR);
        definePropertyDescriptor(STATIC_SUFFIXES_DESCRIPTOR);
        definePropertyDescriptor(MEMBER_PREFIXES_DESCRIPTOR);
        definePropertyDescriptor(MEMBER_SUFFIXES_DESCRIPTOR);
        definePropertyDescriptor(LOCAL_PREFIXES_DESCRIPTOR);
        definePropertyDescriptor(LOCAL_SUFFIXES_DESCRIPTOR);
        definePropertyDescriptor(PARAMETER_PREFIXES_DESCRIPTOR);
        definePropertyDescriptor(PARAMETER_SUFFIXES_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        init();
        return super.visit(node, data);
    }

    protected void init() {
        checkMembers = getProperty(CHECK_MEMBERS_DESCRIPTOR);
        checkLocals = getProperty(CHECK_LOCALS_DESCRIPTOR);
        checkParameters = getProperty(CHECK_PARAMETERS_DESCRIPTOR);
        checkNativeMethodParameters = getProperty(CHECK_NATIVE_METHOD_PARAMETERS_DESCRIPTOR);
        staticPrefixes = getProperty(STATIC_PREFIXES_DESCRIPTOR);
        staticSuffixes = getProperty(STATIC_SUFFIXES_DESCRIPTOR);
        memberPrefixes = getProperty(MEMBER_PREFIXES_DESCRIPTOR);
        memberSuffixes = getProperty(MEMBER_SUFFIXES_DESCRIPTOR);
        localPrefixes = getProperty(LOCAL_PREFIXES_DESCRIPTOR);
        localSuffixes = getProperty(LOCAL_SUFFIXES_DESCRIPTOR);
        parameterPrefixes = getProperty(PARAMETER_PREFIXES_DESCRIPTOR);
        parameterSuffixes = getProperty(PARAMETER_SUFFIXES_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!checkMembers) {
            return data;
        }
        boolean isStatic = node.isStatic();
        boolean isFinal = node.isFinal();

        Node type = node.getParent().getParent().getParent();
        // Anything from an interface is necessarily static and final
        // Anything inside an annotation type is also static and final
        if (type instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) type).isInterface()
                || type instanceof ASTAnnotationTypeDeclaration) {
            isStatic = true;
            isFinal = true;
        }
        return checkVariableDeclarators(node.isStatic() ? staticPrefixes : memberPrefixes,
                isStatic ? staticSuffixes : memberSuffixes, node, isStatic, isFinal, data);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (!checkLocals) {
            return data;
        }
        return checkVariableDeclarators(localPrefixes, localSuffixes, node, false, node.isFinal(), data);
    }

    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        if (!checkParameters) {
            return data;
        }
        ASTMethodDeclaration methodDeclaration = node.getFirstParentOfType(ASTMethodDeclaration.class);
        if (!checkNativeMethodParameters && methodDeclaration.isNative()) {
            return data;
        }

        for (ASTFormalParameter formalParameter : node.findChildrenOfType(ASTFormalParameter.class)) {
            for (ASTVariableDeclaratorId variableDeclaratorId : formalParameter
                    .findChildrenOfType(ASTVariableDeclaratorId.class)) {
                checkVariableDeclaratorId(parameterPrefixes, parameterSuffixes, false, formalParameter.isFinal(),
                        variableDeclaratorId, data);
            }
        }
        return data;
    }

    private Object checkVariableDeclarators(List<String> prefixes, List<String> suffixes, Node root, boolean isStatic,
            boolean isFinal, Object data) {
        for (ASTVariableDeclarator variableDeclarator : root.findChildrenOfType(ASTVariableDeclarator.class)) {
            for (ASTVariableDeclaratorId variableDeclaratorId : variableDeclarator
                    .findChildrenOfType(ASTVariableDeclaratorId.class)) {
                checkVariableDeclaratorId(prefixes, suffixes, isStatic, isFinal, variableDeclaratorId, data);
            }
        }
        return data;
    }

    private Object checkVariableDeclaratorId(List<String> prefixes, List<String> suffixes, boolean isStatic,
            boolean isFinal, ASTVariableDeclaratorId variableDeclaratorId, Object data) {

        // Get the variable name
        String varName = variableDeclaratorId.getImage();

        // Skip serialVersionUID
        if ("serialVersionUID".equals(varName)) {
            return data;
        }

        // Static finals should be uppercase
        if (isStatic && isFinal) {
            if (!varName.equals(varName.toUpperCase(Locale.ROOT))) {
                addViolationWithMessage(data, variableDeclaratorId,
                        "Variables that are final and static should be all capitals, ''{0}'' is not all capitals.",
                        new Object[] { varName });
            }
            return data;
        } else if (!isFinal) {
            String normalizedVarName = normalizeVariableName(varName, prefixes, suffixes);

            if (normalizedVarName.indexOf('_') >= 0) {
                addViolationWithMessage(data, variableDeclaratorId,
                        "Only variables that are final should contain underscores (except for underscores in standard prefix/suffix), ''{0}'' is not final.",
                        new Object[] { varName });
            }
            if (Character.isUpperCase(varName.charAt(0))) {
                addViolationWithMessage(data, variableDeclaratorId,
                        "Variables should start with a lowercase character, ''{0}'' starts with uppercase character.",
                        new Object[] { varName });
            }
        }
        return data;
    }

    private String normalizeVariableName(String varName, List<String> prefixes, List<String> suffixes) {
        return stripSuffix(stripPrefix(varName, prefixes), suffixes);
    }

    private String stripSuffix(String varName, List<String> suffixes) {
        if (suffixes != null) {
            for (String suffix : suffixes) {
                if (varName.endsWith(suffix)) {
                    varName = varName.substring(0, varName.length() - suffix.length());
                    break;
                }
            }
        }
        return varName;
    }

    private String stripPrefix(String varName, List<String> prefixes) {
        if (prefixes != null) {
            for (String prefix : prefixes) {
                if (varName.startsWith(prefix)) {
                    return varName.substring(prefix.length());
                }
            }
        }
        return varName;
    }

    public boolean hasPrefixesOrSuffixes() {

        for (PropertyDescriptor<?> desc : getPropertyDescriptors()) {
            if (desc instanceof StringMultiProperty) {
                List<String> values = getProperty((StringMultiProperty) desc);
                if (!values.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String dysfunctionReason() {
        return hasPrefixesOrSuffixes() ? null : "No prefixes or suffixes specified";
    }

}
