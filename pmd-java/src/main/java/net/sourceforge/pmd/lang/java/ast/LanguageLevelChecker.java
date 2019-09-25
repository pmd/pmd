/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;

final class LanguageLevelChecker extends SideEffectingVisitorAdapter<Void> {

    private final int jdkVersion;
    private final boolean preview;

    public LanguageLevelChecker(int jdkVersion, boolean preview) {
        this.jdkVersion = jdkVersion;
        this.preview = preview;
    }

    private boolean check(Node node, LanguageFeature message) {
        if (message.isAvailable(this.jdkVersion, this.preview)) {
            return true;
        }

        throw new ParseException(
            "Line " + node.getBeginLine() + ", Column " + node.getBeginColumn() + ": "
                + message.whenUnavailableMessage());
    }

    @Override
    public void visit(ASTStringLiteral node, Void data) {
        if (jdkVersion != 13 || !preview) {
            if (node.isTextBlock()) {
                check(node, PreviewFeature.TEXT_BLOCK_LITERALS);
            }
        }
    }

    @Override
    public void visit(ASTImportDeclaration node, Void data) {
        if (node.isStatic()) {
            check(node, RegularLanguageFeature.STATIC_IMPORT);
        }
    }

    @Override
    public void visit(ASTYieldStatement node, Void data) {
        check(node, PreviewFeature.YIELD_STATEMENTS);
    }

    @Override
    public void visit(ASTBreakStatement node, Void data) {
        if (node.jjtGetNumChildren() > 0) {
            check(node, PreviewFeature.BREAK__WITH__VALUE_STATEMENTS);
        }
    }

    @Override
    public void visit(ASTSwitchExpression node, Void data) {
        check(node, PreviewFeature.SWITCH_EXPRESSIONS);
    }

    @Override
    public void visit(ASTConstructorCall node, Void data) {
        if (node.isDiamond()) {
            if (check(node, RegularLanguageFeature.DIAMOND_TYPE_ARGUMENTS) && node.isAnonymousClass()) {
                check(node, RegularLanguageFeature.DIAMOND_TYPE_ARGUMENTS_FOR_ANONYMOUS_CLASSES);
            }
        }
    }

    @Override
    public void visit(ASTTypeArguments node, Void data) {
        check(node, RegularLanguageFeature.GENERICS);
    }

    @Override
    public void visit(ASTTypeParameters node, Void data) {
        check(node, RegularLanguageFeature.GENERICS);
    }

    @Override
    public void visit(ASTFormalParameter node, Void data) {
        if (node.isVarargs()) {
            check(node, RegularLanguageFeature.VARARGS_PARAMETERS);
        } else if (node.isExplicitReceiverParameter()) {
            check(node, RegularLanguageFeature.RECEIVER_PARAMETERS);
        }
    }

    @Override
    public void visit(ASTAnnotation node, Void data) {
        if (node.jjtGetParent() instanceof ASTType) {
            check(node, RegularLanguageFeature.TYPE_ANNOTATIONS);
        } else {
            check(node, RegularLanguageFeature.ANNOTATIONS);
        }
    }

    @Override
    public void visit(ASTForStatement node, Void data) {
        if (node.isForeach()) {
            check(node, RegularLanguageFeature.FOREACH_LOOPS);
        }
    }

    @Override
    public void visit(ASTEnumDeclaration node, Void data) {
        check(node, RegularLanguageFeature.ENUMS);
    }

    @Override
    public void visit(ASTNumericLiteral node, Void data) {
        int base = node.getBase();
        if (base == 16 && !node.isIntegral()) {
            check(node, RegularLanguageFeature.HEXADECIMAL_FLOATING_POINT_LITERALS);
        } else if (base == 2) {
            check(node, RegularLanguageFeature.BINARY_NUMERIC_LITERALS);
        } else if (node.getImage().indexOf('_') >= 0) {
            check(node, RegularLanguageFeature.UNDERSCORES_IN_NUMERIC_LITERALS);
        }
    }

    @Override
    public void visit(ASTMethodReference node, Void data) {
        check(node, RegularLanguageFeature.METHOD_REFERENCES);
    }

    @Override
    public void visit(ASTLambdaExpression node, Void data) {
        check(node, RegularLanguageFeature.LAMBDA_EXPRESSIONS);
    }

    @Override
    public void visit(ASTMethodDeclaration node, Void data) {
        if (node.isDefault()) {
            check(node, RegularLanguageFeature.DEFAULT_METHODS);
        }

        if (node.isPrivate() && node.isInterfaceMember()) {
            check(node, RegularLanguageFeature.PRIVATE_METHODS_IN_INTERFACES);
        }

        checkIdent(node, node.getMethodName());
    }

    @Override
    public void visit(ASTAssertStatement node, Void data) {
        check(node, RegularLanguageFeature.ASSERT_STATEMENTS);
    }

    @Override
    public void visit(ASTTryStatement node, Void data) {
        if (node.isTryWithResources()) {
            if (check(node, RegularLanguageFeature.TRY_WITH_RESOURCES)) {
                for (ASTResource resource : node.getResources()) {
                    if (resource.isConciseResource()) {
                        check(node, RegularLanguageFeature.CONCISE_RESOURCE_SYNTAX);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void visit(ASTIntersectionType node, Void data) {
        if (node.jjtGetParent() instanceof ASTCastExpression) {
            check(node, RegularLanguageFeature.INTERSECTION_TYPES_IN_CASTS);
        }
    }


    @Override
    public void visit(ASTCatchStatement node, Void data) {
        if (node.isMulticatchStatement()) {
            check(node, RegularLanguageFeature.COMPOSITE_CATCH_CLAUSES);
        }
    }


    @Override
    public void visit(ASTSwitchLabel node, Void data) {
        if (IteratorUtil.count(node.iterator()) > 1) {
            check(node, PreviewFeature.COMPOSITE_CASE_LABEL);
        }
    }

    @Override
    public void visit(ASTModuleDeclaration node, Void data) {
        check(node, RegularLanguageFeature.MODULE_DECLARATIONS);
    }


    @Override
    public void visit(ASTSwitchLabeledRule node, Void data) {
        check(node, PreviewFeature.SWITCH_RULES);
    }


    @Override
    public void visit(ASTVariableDeclaratorId node, Void data) {
        checkIdent(node, node.getVariableName());
    }

    @Override
    public void visit(ASTAnyTypeDeclaration node, Void data) {
        checkIdent(node, node.getSimpleName());
    }

    private void checkIdent(JavaNode node, String simpleName) {
        if ("var".equals(simpleName)) {
            check(node, ReservedIdentifiers.VAR_AS_A_TYPE_NAME);
        } else if ("enum".equals(simpleName)) {
            check(node, ReservedIdentifiers.ENUM_AS_AN_IDENTIFIER);
        } else if ("assert".equals(simpleName)) {
            check(node, ReservedIdentifiers.ASSERT_AS_AN_IDENTIFIER);
        }
    }

    private static String displayNameLower(String name) {
        return name.replaceAll("__", "-")
                   .replace('_', ' ')
                   .toLowerCase(Locale.ROOT);
    }


    private static String versionDisplayName(int jdk) {
        if (jdk < 8) {
            return "Java 1." + jdk;
        } else {
            return "Java " + jdk;
        }
    }

    /** Those are hacked just for the preview features. */
    private enum PreviewFeature implements LanguageFeature {
        BREAK__WITH__VALUE_STATEMENTS(12, false),

        COMPOSITE_CASE_LABEL(12, true),
        SWITCH_EXPRESSIONS(12, true),
        SWITCH_RULES(12, true),

        TEXT_BLOCK_LITERALS(13, false),
        YIELD_STATEMENTS(13, false);


        private final int minJdkVersion;
        private final boolean alsoAbove;

        PreviewFeature(int minJdkVersion, boolean alsoAbove) {
            this.minJdkVersion = minJdkVersion;
            this.alsoAbove = alsoAbove;
        }

        @Override
        public boolean isAvailable(int jdk, boolean preview) {
            return preview && (jdk == minJdkVersion || alsoAbove && jdk > minJdkVersion);
        }


        @Override
        public String whenUnavailableMessage() {
            return StringUtils.capitalize(displayNameLower(name()))
                + " is a feature of JDK >= " + "Java " + minJdkVersion + " preview"
                + ", you should select your language version accordingly";
        }
    }

    /** Those use a max valid version. */
    private enum ReservedIdentifiers implements LanguageFeature {
        ASSERT_AS_AN_IDENTIFIER(4, "assert"),
        ENUM_AS_AN_IDENTIFIER(5, "enum"),
        UNDERSCORE_AS_AN_IDENTIFIER(9, "_"),
        VAR_AS_A_TYPE_NAME(10, "var"),
        ;

        private final int maxJdkVersion;
        private final String reserved;

        ReservedIdentifiers(int minJdkVersion, String reserved) {
            this.maxJdkVersion = minJdkVersion;
            this.reserved = reserved;
        }

        @Override
        public boolean isAvailable(int jdk, boolean preview) {
            return jdk < this.maxJdkVersion;
        }

        @Override
        public String whenUnavailableMessage() {
            String s = displayNameLower(name());
            return "Since " + LanguageLevelChecker.versionDisplayName(maxJdkVersion) + ", '" + reserved + "'"
                + " is reserved and cannot be used " + s.substring(s.indexOf(' ') + 1);
        }
    }

    /** Those use a min valid version. */
    private enum RegularLanguageFeature implements LanguageFeature {
        ASSERT_STATEMENTS(4),
        STATIC_IMPORT(4),

        ENUMS(5),
        GENERICS(5),
        ANNOTATIONS(5),
        FOREACH_LOOPS(5),
        VARARGS_PARAMETERS(5),
        HEXADECIMAL_FLOATING_POINT_LITERALS(5),

        UNDERSCORES_IN_NUMERIC_LITERALS(7),
        BINARY_NUMERIC_LITERALS(7),
        TRY_WITH_RESOURCES(7),
        COMPOSITE_CATCH_CLAUSES(7),
        DIAMOND_TYPE_ARGUMENTS(7),

        DEFAULT_METHODS(8),
        RECEIVER_PARAMETERS(8),
        TYPE_ANNOTATIONS(8),
        INTERSECTION_TYPES_IN_CASTS(8),
        LAMBDA_EXPRESSIONS(8),
        METHOD_REFERENCES(8),

        MODULE_DECLARATIONS(9),
        DIAMOND_TYPE_ARGUMENTS_FOR_ANONYMOUS_CLASSES(9),
        PRIVATE_METHODS_IN_INTERFACES(9),
        CONCISE_RESOURCE_SYNTAX(9),
        ;

        private final int minJdkLevel;

        RegularLanguageFeature(int minJdkLevel) {
            this.minJdkLevel = minJdkLevel;
        }


        @Override
        public boolean isAvailable(int jdk, boolean preview) {
            return jdk >= this.minJdkLevel;
        }


        @Override
        public String whenUnavailableMessage() {
            return StringUtils.capitalize(displayNameLower(name()))
                + " is a feature of JDK >= " + versionDisplayName(minJdkLevel)
                + ", you should select your language version accordingly";
        }

    }

    interface LanguageFeature {

        boolean isAvailable(int jdk, boolean preview);


        String whenUnavailableMessage();
    }


}
