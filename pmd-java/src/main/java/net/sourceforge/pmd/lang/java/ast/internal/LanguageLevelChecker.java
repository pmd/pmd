/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;


import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTGuardedPattern;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIntersectionType;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTModuleDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPatternExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReceiverParameter;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordPattern;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchGuard;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.ASTTypePattern;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * Checks that an AST conforms to some language level. The reporting
 * behaviour is parameterized with a {@link ReportingStrategy}.
 *
 * @param <T> Type of object accumulating violations
 */
public class LanguageLevelChecker<T> {

    private static final Pattern SPACE_ESCAPE_PATTERN = Pattern.compile("(?<!\\\\)\\\\s");

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
        node.descendants(JavaNode.class).crossFindBoundaries().forEach(n -> n.acceptVisitor(visitor, accumulator));
        reportingStrategy.done(accumulator);
    }

    private boolean check(Node node, LanguageFeature feature, T acc) {
        String message = feature.errorMessage(this.jdkVersion, this.preview);
        if (message != null) {
            reportingStrategy.report(node, message, acc);
            return false;
        }
        return true;
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


    /**
     * Those are just for the preview features.
     * They are implemented in at least one preview language version.
     * They might be also be standardized.
     */
    private enum PreviewFeature implements LanguageFeature {
        /**
         * Pattern matching for switch
         * @see <a href="https://openjdk.org/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a> (Java 17)
         * @see <a href="https://openjdk.org/jeps/420">JEP 420: Pattern Matching for switch (Second Preview)</a> (Java 18)
         * @see <a href="https://openjdk.org/jeps/427">JEP 427: Pattern Matching for switch (Third Preview)</a> (Java 19)
         */
        PATTERN_MATCHING_FOR_SWITCH(17, 19, false),

        /**
         * Part of pattern matching for switch
         * @see #PATTERN_MATCHING_FOR_SWITCH
         * @see <a href="https://openjdk.java.net/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a> (Java 17)
         * @see <a href="https://openjdk.java.net/jeps/420">JEP 420: Pattern Matching for switch (Second Preview)</a> (Java 18)
         * @deprecated This solution has been discontinued in favor of an explicit guard using "when" keyword
         * in Java 19, see {@link #CASE_REFINEMENT}.</p>
         */
        @Deprecated
        GUARDED_PATTERNS(17, 18, false),

        /**
         * Part of pattern matching for switch
         * @see #PATTERN_MATCHING_FOR_SWITCH
         * @see <a href="https://openjdk.org/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a> (Java 17)
         * @see <a href="https://openjdk.org/jeps/420">JEP 420: Pattern Matching for switch (Second Preview)</a> (Java 18)
         * @see <a href="https://openjdk.org/jeps/427">JEP 427: Pattern Matching for switch (Third Preview)</a> (Java 19)
         */
        NULL_CASE_LABELS(17, 19, false),

        /**
         * Part of pattern matching for switch: Case refinement using "when"
         * @see #PATTERN_MATCHING_FOR_SWITCH
         * @see <a href="https://openjdk.org/jeps/427">JEP 427: Pattern Matching for switch (Third Preview)</a> (Java 19)
         */
        CASE_REFINEMENT(19, 19, false),

        /**
         * Record patterns
         * @see <a href="https://openjdk.org/jeps/405">JEP 405: Record Patterns (Preview)</a>
         */
        RECORD_PATTERNS(19, 19, false),

        ;  // SUPPRESS CHECKSTYLE enum trailing semi is awesome


        private final int minPreviewVersion;
        private final int maxPreviewVersion;
        private final boolean wasStandardized;

        PreviewFeature(int minPreviewVersion, int maxPreviewVersion, boolean wasStandardized) {
            this.minPreviewVersion = minPreviewVersion;
            this.maxPreviewVersion = maxPreviewVersion;
            this.wasStandardized = wasStandardized;
        }


        @Override
        public String errorMessage(int jdk, boolean preview) {
            boolean isStandard = wasStandardized && jdk > maxPreviewVersion;
            boolean canBePreview = jdk >= minPreviewVersion && jdk <= maxPreviewVersion;
            boolean isPreview = preview && canBePreview;

            if (isStandard || isPreview) {
                return null;
            }

            String message = StringUtils.capitalize(displayNameLower(name()));
            if (canBePreview) {
                message += " is a preview feature of JDK " + jdk;
            } else if (wasStandardized) {
                message = message + " was only standardized in Java " + (maxPreviewVersion + 1);
            } else if (minPreviewVersion == maxPreviewVersion) {
                message += " is a preview feature of JDK " + minPreviewVersion;
            } else {
                message += " is a preview feature of JDKs " + minPreviewVersion + " to " + maxPreviewVersion;
            }
            return message + ", you should select your language version accordingly";
        }
    }

    /**
     * Those use a max valid version.
     *
     * @see <a href="http://cr.openjdk.java.net/~gbierman/jep397/jep397-20201204/specs/contextual-keywords-jls.html">Contextual Keywords</a>
     */
    private enum Keywords implements LanguageFeature {
        /**
         * ReservedKeyword since Java 1.4.
         */
        ASSERT_AS_AN_IDENTIFIER(4, "assert"),
        /**
         * ReservedKeyword since Java 1.5.
         */
        ENUM_AS_AN_IDENTIFIER(5, "enum"),
        /**
         * ReservedKeyword since Java 9.
         */
        UNDERSCORE_AS_AN_IDENTIFIER(9, "_"),
        /**
         * ContextualKeyword since Java 10.
         */
        VAR_AS_A_TYPE_NAME(10, "var"),

        /**
         * ContextualKeyword since Java 13 Preview.
         */
        YIELD_AS_A_TYPE_NAME(13, "yield"),

        /**
         * ContextualKeyword since Java 14 Preview.
         */
        RECORD_AS_A_TYPE_NAME(14, "record"),

        /**
         * ContextualKeyword since Java 15 Preview.
         */
        SEALED_AS_A_TYPE_NAME(15, "sealed"),

        /**
         * ContextualKeyword since Java 15 Preview.
         */
        PERMITS_AS_A_TYPE_NAME(15, "permits"),

        ;  // SUPPRESS CHECKSTYLE enum trailing semi is awesome

        private final int maxJdkVersion;
        private final String reserved;

        Keywords(int minJdkVersion, String reserved) {
            this.maxJdkVersion = minJdkVersion;
            this.reserved = reserved;
        }

        @Override
        public String errorMessage(int jdk, boolean preview) {
            if (jdk < this.maxJdkVersion) {
                return null;
            }
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
        CONCISE_RESOURCE_SYNTAX(9),

        /**
         * @see <a href="https://openjdk.java.net/jeps/361">JEP 361: Switch Expressions</a>
         */
        COMPOSITE_CASE_LABEL(14),
        /**
         * @see <a href="https://openjdk.java.net/jeps/361">JEP 361: Switch Expressions</a>
         */
        SWITCH_EXPRESSIONS(14),
        /**
         * @see <a href="https://openjdk.java.net/jeps/361">JEP 361: Switch Expressions</a>
         */
        SWITCH_RULES(14),
        /**
         * @see #SWITCH_EXPRESSIONS
         * @see <a href="https://openjdk.java.net/jeps/361">JEP 361: Switch Expressions</a>
         */
        YIELD_STATEMENTS(14),

        /**
         * @see <a href="https://openjdk.java.net/jeps/378">JEP 378: Text Blocks</a>
         */
        TEXT_BLOCK_LITERALS(15),
        /**
         * The new escape sequence {@code \s} simply translates to a single space {@code \u0020}.
         *
         * @see #TEXT_BLOCK_LITERALS
         * @see <a href="https://openjdk.java.net/jeps/378">JEP 378: Text Blocks</a>
         */
        SPACE_STRING_ESCAPES(15),

        /**
         * @see <a href="https://openjdk.java.net/jeps/359">JEP 359: Records (Preview)</a> (Java 14)
         * @see <a href="https://openjdk.java.net/jeps/384">JEP 384: Records (Second Preview)</a> (Java 15)
         * @see <a href="https://openjdk.java.net/jeps/395">JEP 395: Records</a> (Java 16)
         */
        RECORD_DECLARATIONS(16),

        /**
         * @see <a href="https://openjdk.java.net/jeps/305">JEP 305: Pattern Matching for instanceof (Preview)</a> (Java 14)
         * @see <a href="https://openjdk.java.net/jeps/375">JEP 375: Pattern Matching for instanceof (Second Preview)</a> (Java 15)
         * @see <a href="https://openjdk.java.net/jeps/394">JEP 394: Pattern Matching for instanceof</a> (Java 16)
         */
        TYPE_PATTERNS_IN_INSTANCEOF(16),

        /**
         * Part of the records JEP 394.
         * @see #RECORD_DECLARATIONS
         * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8253374">JLS changes for Static Members of Inner Classes</a> (Java 16)
         */
        STATIC_LOCAL_TYPE_DECLARATIONS(16),

        /**
         * @see <a href="https://openjdk.java.net/jeps/360">JEP 360: Sealed Classes (Preview)</a> (Java 15)
         * @see <a href="https://openjdk.java.net/jeps/397">JEP 397: Sealed Classes (Second Preview)</a> (Java 16)
         * @see <a href="https://openjdk.java.net/jeps/409">JEP 409: Sealed Classes</a> (Java 17)
         */
        SEALED_CLASSES(17),

        ;  // SUPPRESS CHECKSTYLE enum trailing semi is awesome

        private final int minJdkLevel;

        RegularLanguageFeature(int minJdkLevel) {
            this.minJdkLevel = minJdkLevel;
        }


        @Override
        public String errorMessage(int jdk, boolean preview) {
            if (jdk >= this.minJdkLevel) {
                return null;
            }
            return StringUtils.capitalize(displayNameLower(name()))
                + " are a feature of " + versionDisplayName(minJdkLevel)
                + ", you should select your language version accordingly";
        }

    }

    interface LanguageFeature {

        @Nullable
        String errorMessage(int jdk, boolean preview);
    }

    private final class CheckVisitor extends JavaVisitorBase<T, Void> {

        @Override
        protected Void visitChildren(Node node, T data) {
            throw new AssertionError("Shouldn't recurse");
        }

        @Override
        public Void visitNode(Node node, T param) {
            return null;
        }

        @Override
        public Void visit(ASTStringLiteral node, T data) {
            if (node.isStringLiteral() && SPACE_ESCAPE_PATTERN.matcher(node.getImage()).find()) {
                check(node, RegularLanguageFeature.SPACE_STRING_ESCAPES, data);
            }
            if (node.isTextBlock()) {
                check(node, RegularLanguageFeature.TEXT_BLOCK_LITERALS, data);
            }
            return null;
        }

        @Override
        public Void visit(ASTImportDeclaration node, T data) {
            if (node.isStatic()) {
                check(node, RegularLanguageFeature.STATIC_IMPORT, data);
            }
            return null;
        }

        @Override
        public Void visit(ASTYieldStatement node, T data) {
            check(node, RegularLanguageFeature.YIELD_STATEMENTS, data);
            return null;
        }

        @Override
        public Void visit(ASTSwitchExpression node, T data) {
            check(node, RegularLanguageFeature.SWITCH_EXPRESSIONS, data);
            return null;
        }

        @Override
        public Void visit(ASTRecordDeclaration node, T data) {
            check(node, RegularLanguageFeature.RECORD_DECLARATIONS, data);
            return null;
        }

        @Override
        public Void visit(ASTConstructorCall node, T data) {
            if (node.usesDiamondTypeArgs()) {
                if (check(node, RegularLanguageFeature.DIAMOND_TYPE_ARGUMENTS, data) && node.isAnonymousClass()) {
                    check(node, RegularLanguageFeature.DIAMOND_TYPE_ARGUMENTS_FOR_ANONYMOUS_CLASSES, data);
                }
            }
            return null;
        }

        @Override
        public Void visit(ASTTypeArguments node, T data) {
            check(node, RegularLanguageFeature.GENERICS, data);
            return null;
        }

        @Override
        public Void visit(ASTTypeParameters node, T data) {
            check(node, RegularLanguageFeature.GENERICS, data);
            return null;
        }

        @Override
        public Void visit(ASTFormalParameter node, T data) {
            if (node.isVarargs()) {
                check(node, RegularLanguageFeature.VARARGS_PARAMETERS, data);
            }
            return null;
        }


        @Override
        public Void visit(ASTReceiverParameter node, T data) {
            check(node, RegularLanguageFeature.RECEIVER_PARAMETERS, data);
            return null;
        }

        @Override
        public Void visit(ASTAnnotation node, T data) {
            if (node.getParent() instanceof ASTType) {
                check(node, RegularLanguageFeature.TYPE_ANNOTATIONS, data);
            } else {
                check(node, RegularLanguageFeature.ANNOTATIONS, data);
            }
            return null;
        }

        @Override
        public Void visit(ASTForeachStatement node, T data) {
            check(node, RegularLanguageFeature.FOREACH_LOOPS, data);
            return null;
        }

        @Override
        public Void visit(ASTEnumDeclaration node, T data) {
            check(node, RegularLanguageFeature.ENUMS, data);
            visitTypeDecl((ASTAnyTypeDeclaration) node, data);
            return null;
        }

        @Override
        public Void visit(ASTNumericLiteral node, T data) {
            int base = node.getBase();
            if (base == 16 && !node.isIntegral()) {
                check(node, RegularLanguageFeature.HEXADECIMAL_FLOATING_POINT_LITERALS, data);
            } else if (base == 2) {
                check(node, RegularLanguageFeature.BINARY_NUMERIC_LITERALS, data);
            } else if (node.getImage().indexOf('_') >= 0) {
                check(node, RegularLanguageFeature.UNDERSCORES_IN_NUMERIC_LITERALS, data);
            }
            return null;
        }

        @Override
        public Void visit(ASTMethodReference node, T data) {
            check(node, RegularLanguageFeature.METHOD_REFERENCES, data);
            return null;
        }

        @Override
        public Void visit(ASTLambdaExpression node, T data) {
            check(node, RegularLanguageFeature.LAMBDA_EXPRESSIONS, data);
            return null;
        }

        @Override
        public Void visit(ASTMethodDeclaration node, T data) {
            if (node.hasModifiers(JModifier.DEFAULT)) {
                check(node, RegularLanguageFeature.DEFAULT_METHODS, data);
            }

            if (node.isPrivate() && node.getEnclosingType().isInterface()) {
                check(node, RegularLanguageFeature.PRIVATE_METHODS_IN_INTERFACES, data);
            }

            checkIdent(node, node.getMethodName(), data);
            return null;
        }

        @Override
        public Void visit(ASTAssertStatement node, T data) {
            check(node, RegularLanguageFeature.ASSERT_STATEMENTS, data);
            return null;
        }

        @Override
        public Void visit(ASTTypePattern node, T data) {
            check(node, RegularLanguageFeature.TYPE_PATTERNS_IN_INSTANCEOF, data);
            return null;
        }

        @Override
        public Void visit(ASTRecordPattern node, T data) {
            check(node, PreviewFeature.RECORD_PATTERNS, data);
            return null;
        }

        @Override
        public Void visit(ASTGuardedPattern node, T data) {
            check(node, PreviewFeature.GUARDED_PATTERNS, data);
            return null;
        }

        @Override
        public Void visit(ASTSwitchGuard node, T data) {
            check(node, PreviewFeature.CASE_REFINEMENT, data);
            return null;
        }

        @Override
        public Void visit(ASTTryStatement node, T data) {
            if (node.isTryWithResources()) {
                if (check(node, RegularLanguageFeature.TRY_WITH_RESOURCES, data)) {
                    for (ASTResource resource : node.getResources()) {
                        if (resource.isConciseResource()) {
                            check(node, RegularLanguageFeature.CONCISE_RESOURCE_SYNTAX, data);
                            break;
                        }
                    }
                }
            }
            return null;
        }


        @Override
        public Void visit(ASTIntersectionType node, T data) {
            if (node.getParent() instanceof ASTCastExpression) {
                check(node, RegularLanguageFeature.INTERSECTION_TYPES_IN_CASTS, data);
            }
            return null;
        }


        @Override
        public Void visit(ASTCatchClause node, T data) {
            if (node.getParameter().isMulticatch()) {
                check(node, RegularLanguageFeature.COMPOSITE_CATCH_CLAUSES, data);
            }
            return null;
        }

        @Override
        public Void visit(ASTSwitchLabel node, T data) {
            if (IteratorUtil.count(node.iterator()) > 1) {
                check(node, RegularLanguageFeature.COMPOSITE_CASE_LABEL, data);
            }
            if (node.isDefault() && JavaTokenKinds.CASE == node.getFirstToken().getKind()) {
                check(node, PreviewFeature.PATTERN_MATCHING_FOR_SWITCH, data);
            }
            for (ASTExpression expr : node.getExprList()) {
                if (expr instanceof ASTPatternExpression) {
                    check(expr, PreviewFeature.PATTERN_MATCHING_FOR_SWITCH, data);
                    if (((ASTPatternExpression) expr).getPattern() instanceof ASTGuardedPattern) {
                        check(expr, PreviewFeature.GUARDED_PATTERNS, data);
                    }
                } else if (expr instanceof ASTNullLiteral) {
                    check(expr, PreviewFeature.NULL_CASE_LABELS, data);
                }
            }
            return null;
        }

        @Override
        public Void visit(ASTModuleDeclaration node, T data) {
            check(node, RegularLanguageFeature.MODULE_DECLARATIONS, data);
            return null;
        }

        @Override
        public Void visit(ASTSwitchArrowBranch node, T data) {
            check(node, RegularLanguageFeature.SWITCH_RULES, data);
            return null;
        }

        @Override
        public Void visit(ASTVariableDeclaratorId node, T data) {
            checkIdent(node, node.getName(), data);
            return null;
        }

        @Override
        public Void visitTypeDecl(ASTAnyTypeDeclaration node, T data) {
            if (node.getModifiers().hasAnyExplicitly(JModifier.SEALED, JModifier.NON_SEALED)) {
                check(node, RegularLanguageFeature.SEALED_CLASSES, data);
            } else if (node.isLocal() && !node.isRegularClass()) {
                check(node, RegularLanguageFeature.STATIC_LOCAL_TYPE_DECLARATIONS, data);
            }
            String simpleName = node.getSimpleName();
            if ("var".equals(simpleName)) {
                check(node, Keywords.VAR_AS_A_TYPE_NAME, data);
            } else if ("yield".equals(simpleName)) {
                check(node, Keywords.YIELD_AS_A_TYPE_NAME, data);
            } else if ("record".equals(simpleName)) {
                check(node, Keywords.RECORD_AS_A_TYPE_NAME, data);
            } else if ("sealed".equals(simpleName)) {
                check(node, Keywords.SEALED_AS_A_TYPE_NAME, data);
            } else if ("permits".equals(simpleName)) {
                check(node, Keywords.PERMITS_AS_A_TYPE_NAME, data);
            }
            checkIdent(node, simpleName, data);
            return null;
        }

        private void checkIdent(JavaNode node, String simpleName, T acc) {
            if ("enum".equals(simpleName)) {
                check(node, Keywords.ENUM_AS_AN_IDENTIFIER, acc);
            } else if ("assert".equals(simpleName)) {
                check(node, Keywords.ASSERT_AS_AN_IDENTIFIER, acc);
            } else if ("_".equals(simpleName)) {
                check(node, Keywords.UNDERSCORE_AS_AN_IDENTIFIER, acc);
            }
        }

    }

}
