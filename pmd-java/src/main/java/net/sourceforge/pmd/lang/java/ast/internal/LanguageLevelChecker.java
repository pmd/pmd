/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;


import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTModuleDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTResourceSpecification;
import net.sourceforge.pmd.lang.java.ast.ASTResources;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledRule;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.SideEffectingVisitorAdapter;

/**
 * Checks that an AST conforms to some language level. The reporting
 * behaviour is parameterized with a {@link ReportingStrategy}.
 *
 * @param <T> Type of object accumulating violations
 */
public class LanguageLevelChecker<T> {

    private final int jdkVersion;
    private final boolean preview;
    private final CheckVisitor visitor = new CheckVisitor();
    private final ReportingStrategy<T> reportingStrategy;

    public LanguageLevelChecker(int jdkVersion, boolean preview, ReportingStrategy<T> reportingStrategy) {
        this.jdkVersion = jdkVersion;
        this.preview = preview;
        this.reportingStrategy = reportingStrategy;
    }

    public int getJdkVersion() {
        return jdkVersion;
    }

    public boolean isPreviewEnabled() {
        return preview;
    }


    public void check(JavaNode node) {
        T accumulator = reportingStrategy.createAccumulator();
        node.jjtAccept(visitor, accumulator);
        reportingStrategy.done(accumulator);
    }

    private boolean check(Node node, LanguageFeature message, T acc) {
        if (message.isAvailable(this.jdkVersion, this.preview)) {
            return true;
        }

        reportingStrategy.report(node, message.whenUnavailableMessage(), acc);
        return false;
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
        VAR_AS_A_TYPE_NAME(10, "var");

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
            String usageType = s.substring(s.indexOf(' ') + 1); // eg "as an identifier"
            return "Since " + LanguageLevelChecker.versionDisplayName(maxJdkVersion) + ", '" + reserved + "'"
                + " is reserved and cannot be used " + usageType;
        }
    }

    /** Those use a min valid version. */
    private enum RegularLanguageFeature implements LanguageFeature {

        ASSERT_STATEMENTS(4),

        STATIC_IMPORT(5),
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
        CONCISE_RESOURCE_SYNTAX(9);

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
                + " are a feature of " + versionDisplayName(minJdkLevel)
                + ", you should select your language version accordingly";
        }

    }

    interface LanguageFeature {

        boolean isAvailable(int jdk, boolean preview);


        String whenUnavailableMessage();
    }

    private class CheckVisitor extends SideEffectingVisitorAdapter<T> {

//        @Override
//        public void visit(ASTStringLiteral node, T data) {
//            if (jdkVersion != 13 || !preview) {
//                if (node.isTextBlock()) {
//                    check(node, PreviewFeature.TEXT_BLOCK_LITERALS, data);
//                }
//            }
//            visitChildren(node, data);
//        }

        @Override
        public void visit(ASTImportDeclaration node, T data) {
            if (node.isStatic()) {
                check(node, RegularLanguageFeature.STATIC_IMPORT, data);
            }
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTYieldStatement node, T data) {
            check(node, PreviewFeature.YIELD_STATEMENTS, data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTBreakStatement node, T data) {
            if (node.jjtGetNumChildren() > 0) {
                check(node, PreviewFeature.BREAK__WITH__VALUE_STATEMENTS, data);
            }
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTSwitchExpression node, T data) {
            check(node, PreviewFeature.SWITCH_EXPRESSIONS, data);
            visitChildren(node, data);
        }

//        @Override
//        public void visit(ASTConstructorCall node, T data) {
//            if (node.usesDiamondTypeArgs()) {
//                if (check(node, RegularLanguageFeature.DIAMOND_TYPE_ARGUMENTS, data) && node.isAnonymousClass()) {
//                    check(node, RegularLanguageFeature.DIAMOND_TYPE_ARGUMENTS_FOR_ANONYMOUS_CLASSES, data);
//                }
//            }
//            visitChildren(node, data);
//        }

        @Override
        public void visit(ASTTypeArguments node, T data) {
            check(node, RegularLanguageFeature.GENERICS, data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTTypeParameters node, T data) {
            check(node, RegularLanguageFeature.GENERICS, data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTFormalParameter node, T data) {
            if (node.isVarargs()) {
                check(node, RegularLanguageFeature.VARARGS_PARAMETERS, data);
            } else if (node.isExplicitReceiverParameter()) {
                check(node, RegularLanguageFeature.RECEIVER_PARAMETERS, data);
            }

            visitChildren(node, data);
        }

        //        @Override
        //        public void visit(ASTReceiverParameter node, T data) {
        //            check(node, RegularLanguageFeature.RECEIVER_PARAMETERS, data);
        //        }

        @Override
        public void visit(ASTAnnotation node, T data) {
            if (node.jjtGetParent() instanceof ASTType) {
                check(node, RegularLanguageFeature.TYPE_ANNOTATIONS, data);
            } else {
                check(node, RegularLanguageFeature.ANNOTATIONS, data);
            }
            visitChildren(node, data);
        }

        //
        //        @Override
        //        public void visit(ASTForeachStatement node, T data) {
        //            check(node, RegularLanguageFeature.FOREACH_LOOPS, data);
        //            visitChildren(node, data);
        //        }


        @Override
        public void visit(ASTForStatement node, T data) {
            if (node.isForeach()) {
                check(node, RegularLanguageFeature.FOREACH_LOOPS, data);
            }
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTEnumDeclaration node, T data) {
            check(node, RegularLanguageFeature.ENUMS, data);
            super.visit(node, data);
        }

        //        @Override
        //        public void visit(ASTNumericLiteral node, T data) {
        //            int base = node.getBase();
        //            if (base == 16 && !node.isIntegral()) {
        //                check(node, RegularLanguageFeature.HEXADECIMAL_FLOATING_POINT_LITERALS, data);
        //            } else if (base == 2) {
        //                check(node, RegularLanguageFeature.BINARY_NUMERIC_LITERALS, data);
        //            } else if (node.getImage().indexOf('_') >= 0) {
        //                check(node, RegularLanguageFeature.UNDERSCORES_IN_NUMERIC_LITERALS, data);
        //            }
        //            visitChildren(node, data);
        //        }

        @Override
        public void visit(ASTMethodReference node, T data) {
            check(node, RegularLanguageFeature.METHOD_REFERENCES, data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTLambdaExpression node, T data) {
            check(node, RegularLanguageFeature.LAMBDA_EXPRESSIONS, data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTMethodDeclaration node, T data) {
            if (node.isDefault()) {
                check(node, RegularLanguageFeature.DEFAULT_METHODS, data);
            }

            if (node.isPrivate() && node.isInterfaceMember()) {
                check(node, RegularLanguageFeature.PRIVATE_METHODS_IN_INTERFACES, data);
            }

            checkIdent(node, node.getMethodName(), data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTAssertStatement node, T data) {
            check(node, RegularLanguageFeature.ASSERT_STATEMENTS, data);
            visitChildren(node, data);
        }

        //        @Override
        //        public void visit(ASTTryStatement node, T data) {
        //            if (node.isTryWithResources()) {
        //                if (check(node, RegularLanguageFeature.TRY_WITH_RESOURCES, data)) {
        //                    for (ASTResource resource : node.getResources()) {
        //                        if (resource.isConciseResource()) {
        //                            check(node, RegularLanguageFeature.CONCISE_RESOURCE_SYNTAX, data);
        //                            break;
        //                        }
        //                    }
        //                }
        //            }
        //            visitChildren(node, data);
        //        }


        @Override
        public void visit(ASTTryStatement node, T data) {
            if (node.isTryWithResources()) {
                if (check(node, RegularLanguageFeature.TRY_WITH_RESOURCES, data)) {
                    for (ASTResource resource : node.children(ASTResourceSpecification.class).children(ASTResources.class).children(ASTResource.class)) {
                        if (resource.children(ASTName.class).nonEmpty()) {
                            check(node, RegularLanguageFeature.CONCISE_RESOURCE_SYNTAX, data);
                            break;
                        }
                    }
                }
            }
            visitChildren(node, data);
        }

        //        @Override
        //        public void visit(ASTIntersectionType node, T data) {
        //            if (node.jjtGetParent() instanceof ASTCastExpression) {
        //                check(node, RegularLanguageFeature.INTERSECTION_TYPES_IN_CASTS, data);
        //            }
        //            visitChildren(node, data);
        //        }


        @Override
        public void visit(ASTCastExpression node, T data) {
            if (node.children(ASTReferenceType.class).nonEmpty()) {
                check(node, RegularLanguageFeature.INTERSECTION_TYPES_IN_CASTS, data);
            }
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTCatchStatement node, T data) {
            if (node.isMulticatchStatement()) {
                check(node, RegularLanguageFeature.COMPOSITE_CATCH_CLAUSES, data);
            }
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTSwitchLabel node, T data) {
            if (node.jjtGetNumChildren() > 1) {
                check(node, PreviewFeature.COMPOSITE_CASE_LABEL, data);
            }
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTModuleDeclaration node, T data) {
            check(node, RegularLanguageFeature.MODULE_DECLARATIONS, data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTSwitchLabeledRule node, T data) {
            check(node, PreviewFeature.SWITCH_RULES, data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTVariableDeclaratorId node, T data) {
            checkIdent(node, node.getVariableName(), data);
            visitChildren(node, data);
        }

        @Override
        public void visit(ASTAnyTypeDeclaration node, T data) {
            if ("var".equals(node.getSimpleName())) {
                check(node, ReservedIdentifiers.VAR_AS_A_TYPE_NAME, data);
            }
            checkIdent(node, node.getSimpleName(), data);
            visitChildren(node, data);
        }

        private void visitChildren(JavaNode node, T data) {
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                node.jjtGetChild(i).jjtAccept(visitor, data);
            }
        }

        private void checkIdent(JavaNode node, String simpleName, T acc) {
            if ("enum".equals(simpleName)) {
                check(node, ReservedIdentifiers.ENUM_AS_AN_IDENTIFIER, acc);
            } else if ("assert".equals(simpleName)) {
                check(node, ReservedIdentifiers.ASSERT_AS_AN_IDENTIFIER, acc);
            } else if ("_".equals(simpleName)) {
                check(node, ReservedIdentifiers.UNDERSCORE_AS_AN_IDENTIFIER, acc);
            }
        }

    }


}
