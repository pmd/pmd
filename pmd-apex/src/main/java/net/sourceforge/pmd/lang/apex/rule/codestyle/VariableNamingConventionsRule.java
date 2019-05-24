/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;

@Deprecated
public class VariableNamingConventionsRule extends AbstractApexRule {

    private boolean checkMembers;
    private boolean checkLocals;
    private boolean checkParameters;
    private List<String> staticPrefixes;
    private List<String> staticSuffixes;
    private List<String> memberPrefixes;
    private List<String> memberSuffixes;
    private List<String> localPrefixes;
    private List<String> localSuffixes;
    private List<String> parameterPrefixes;
    private List<String> parameterSuffixes;

    private static final PropertyDescriptor<Boolean> CHECK_MEMBERS_DESCRIPTOR =
            booleanProperty("checkMembers")
                .desc("Check member variables").defaultValue(true).build();

    private static final PropertyDescriptor<Boolean> CHECK_LOCALS_DESCRIPTOR =
            booleanProperty("checkLocals")
                .desc("Check local variables").defaultValue(true).build();

    private static final PropertyDescriptor<Boolean> CHECK_PARAMETERS_DESCRIPTOR =
            booleanProperty("checkParameters")
                .desc("Check constructor and method parameter variables").defaultValue(true).build();

    private static final PropertyDescriptor<List<String>> STATIC_PREFIXES_DESCRIPTOR =
            stringListProperty("staticPrefix")
                    .desc("Static variable prefixes").defaultValues("").delim(',').build();

    private static final PropertyDescriptor<List<String>> STATIC_SUFFIXES_DESCRIPTOR =
            stringListProperty("staticSuffix")
                    .desc("Static variable suffixes").defaultValues("").delim(',').build();

    private static final PropertyDescriptor<List<String>> MEMBER_PREFIXES_DESCRIPTOR =
            stringListProperty("memberPrefix")
                    .desc("Member variable prefixes").defaultValues("").delim(',').build();

    private static final PropertyDescriptor<List<String>> MEMBER_SUFFIXES_DESCRIPTOR =
            stringListProperty("memberSuffix")
                    .desc("Member variable suffixes").defaultValues("").delim(',').build();

    private static final PropertyDescriptor<List<String>> LOCAL_PREFIXES_DESCRIPTOR =
            stringListProperty("localPrefix")
                    .desc("Local variable prefixes").defaultValues("").delim(',').build();

    private static final PropertyDescriptor<List<String>> LOCAL_SUFFIXES_DESCRIPTOR =
            stringListProperty("localSuffix")
                    .desc("Local variable suffixes").defaultValues("").delim(',').build();

    private static final PropertyDescriptor<List<String>> PARAMETER_PREFIXES_DESCRIPTOR =
            stringListProperty("parameterPrefix")
                    .desc("Method parameter variable prefixes")
                    .defaultValues("").delim(',').build();

    private static final PropertyDescriptor<List<String>> PARAMETER_SUFFIXES_DESCRIPTOR =
            stringListProperty("parameterSuffix")
                    .desc("Method parameter variable suffixes")
                    .defaultValues("").delim(',').build();


    public VariableNamingConventionsRule() {
        definePropertyDescriptor(CHECK_MEMBERS_DESCRIPTOR);
        definePropertyDescriptor(CHECK_LOCALS_DESCRIPTOR);
        definePropertyDescriptor(CHECK_PARAMETERS_DESCRIPTOR);
        for (PropertyDescriptor<List<String>> property : suffixOrPrefixProperties()) {
            definePropertyDescriptor(property);
        }

        setProperty(CODECLIMATE_CATEGORIES, "Style");
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 5);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    private static List<PropertyDescriptor<List<String>>> suffixOrPrefixProperties() {
        List<PropertyDescriptor<List<String>>> res = new ArrayList<>();
        res.add(STATIC_PREFIXES_DESCRIPTOR);
        res.add(STATIC_SUFFIXES_DESCRIPTOR);
        res.add(MEMBER_PREFIXES_DESCRIPTOR);
        res.add(MEMBER_SUFFIXES_DESCRIPTOR);
        res.add(LOCAL_PREFIXES_DESCRIPTOR);
        res.add(LOCAL_SUFFIXES_DESCRIPTOR);
        res.add(PARAMETER_PREFIXES_DESCRIPTOR);
        res.add(PARAMETER_SUFFIXES_DESCRIPTOR);
        return res;
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        init();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        init();
        return super.visit(node, data);
    }

    protected void init() {
        checkMembers = getProperty(CHECK_MEMBERS_DESCRIPTOR);
        checkLocals = getProperty(CHECK_LOCALS_DESCRIPTOR);
        checkParameters = getProperty(CHECK_PARAMETERS_DESCRIPTOR);
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
    public Object visit(ASTField node, Object data) {
        if (!checkMembers) {
            return data;
        }
        boolean isStatic = node.getModifiers().isStatic();
        boolean isFinal = node.getModifiers().isFinal();

        return checkName(isStatic ? staticPrefixes : memberPrefixes, isStatic ? staticSuffixes : memberSuffixes, node,
                isStatic, isFinal, data);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {

        if (!checkLocals) {
            return data;
        }

        boolean isFinal = node.getFirstParentOfType(ASTVariableDeclarationStatements.class).getModifiers().isFinal();
        return checkName(localPrefixes, localSuffixes, node, false, isFinal, data);
    }

    @Override
    public Object visit(ASTParameter node, Object data) {
        if (!checkParameters) {
            return data;
        }

        boolean isFinal = node.getModifiers().isFinal();
        return checkName(parameterPrefixes, parameterSuffixes, node, false, isFinal, data);
    }

    private Object checkName(List<String> prefixes, List<String> suffixes, ApexNode<?> node, boolean isStatic, boolean isFinal,
            Object data) {

        String varName = node.getImage();

        // Skip on null (with exception classes) and serialVersionUID
        if (varName == null || "serialVersionUID".equals(varName)) {
            return data;
        }

        // Static finals should be uppercase
        if (isStatic && isFinal) {
            if (!varName.equals(varName.toUpperCase(Locale.ROOT))) {
                addViolationWithMessage(data, node,
                        "Variables that are final and static should be all capitals, ''{0}'' is not all capitals.",
                        new Object[] { varName });
            }
            return data;
        } else if (!isFinal) {
            String normalizedVarName = normalizeVariableName(varName, prefixes, suffixes);

            if (normalizedVarName.indexOf('_') >= 0) {
                addViolationWithMessage(data, node,
                        "Only variables that are final should contain underscores (except for underscores in standard prefix/suffix), ''{0}'' is not final.",
                        new Object[] { varName });
            }
            if (Character.isUpperCase(varName.charAt(0))) {
                addViolationWithMessage(data, node,
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
                    return varName.substring(0, varName.length() - suffix.length());
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
        for (PropertyDescriptor<List<String>> desc : suffixOrPrefixProperties()) {
            if (!getProperty(desc).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String dysfunctionReason() {
        return hasPrefixesOrSuffixes() ? null : "No prefixes or suffixes specified";
    }

}
