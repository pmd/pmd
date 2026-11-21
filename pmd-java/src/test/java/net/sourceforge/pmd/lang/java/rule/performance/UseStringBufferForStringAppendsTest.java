/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import static net.sourceforge.pmd.lang.java.rule.performance.UseStringBufferForStringAppendsRule.isLambdaLoop;
import static net.sourceforge.pmd.lang.java.rule.performance.UseStringBufferForStringAppendsRule.isWithinLoop;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.test.PmdRuleTst;

class UseStringBufferForStringAppendsTest extends PmdRuleTst {

    private final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());

    @Nested
    @DisplayName("Tests for isLambdaLoop matching iterative lambda methods (#7271)")
    class IsLambdaLoopTests {

        @Test
        @DisplayName("Iterable.forEach lambda is identified as loop")
        void testIterableForEach() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.List;\n"
                    + "class Foo {\n"
                    + "  void bar(List<String> list) {\n"
                    + "    list.forEach(s -> System.out.println(s));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertTrue(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("Map.forEach lambda is identified as loop")
        void testMapForEach() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.Map;\n"
                    + "class Foo {\n"
                    + "  void bar(Map<String, String> map) {\n"
                    + "    map.forEach((k, v) -> System.out.println(k + v));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertTrue(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("Stream.forEach lambda is identified as loop")
        void testStreamForEach() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.stream.Stream;\n"
                    + "class Foo {\n"
                    + "  void bar(Stream<String> stream) {\n"
                    + "    stream.forEach(s -> System.out.println(s));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertTrue(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("Stream.forEachOrdered lambda is identified as loop")
        void testStreamForEachOrdered() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.stream.Stream;\n"
                    + "class Foo {\n"
                    + "  void bar(Stream<String> stream) {\n"
                    + "    stream.forEachOrdered(s -> System.out.println(s));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertTrue(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("IntStream.forEach lambda is identified as loop")
        void testIntStreamForEach() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.stream.IntStream;\n"
                    + "class Foo {\n"
                    + "  void bar(IntStream stream) {\n"
                    + "    stream.forEach(i -> System.out.println(i));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertTrue(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("Iterator.forEachRemaining lambda is identified as loop")
        void testIteratorForEachRemaining() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.Iterator;\n"
                    + "class Foo {\n"
                    + "  void bar(Iterator<String> it) {\n"
                    + "    it.forEachRemaining(s -> System.out.println(s));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertTrue(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("Spliterator.forEachRemaining lambda is identified as loop")
        void testSpliteratorForEachRemaining() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.Spliterator;\n"
                    + "class Foo {\n"
                    + "  void bar(Spliterator<String> sp) {\n"
                    + "    sp.forEachRemaining(s -> System.out.println(s));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertTrue(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("Non-loop lambda (Optional.ifPresent) is NOT identified as loop")
        void testOptionalIfPresentNotLoop() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.Optional;\n"
                    + "class Foo {\n"
                    + "  void bar(Optional<String> opt) {\n"
                    + "    opt.ifPresent(s -> System.out.println(s));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertFalse(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("Custom class forEach method is NOT identified as standard loop")
        void testCustomForEachNotLoop() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.function.Consumer;\n"
                    + "class Custom {\n"
                    + "  void forEach(Consumer<String> action) {}\n"
                    + "}\n"
                    + "class Foo {\n"
                    + "  void bar(Custom c) {\n"
                    + "    c.forEach(s -> System.out.println(s));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertFalse(isLambdaLoop(lambda));
        }

        @Test
        @DisplayName("Cast lambda passed to forEach is identified as loop")
        void testCastLambdaForEach() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.List;\n"
                    + "import java.util.function.Consumer;\n"
                    + "class Foo {\n"
                    + "  void bar(List<String> list) {\n"
                    + "    list.forEach((Consumer<String>) s -> System.out.println(s));\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            assertTrue(isLambdaLoop(lambda));
        }
    }


    @Nested
    @DisplayName("Tests for isWithinLoop ancestor detection")
    class IsWithinLoopTests {

        @Test
        @DisplayName("Node inside forEach lambda is within loop")
        void testInsideForEachLambda() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.List;\n"
                    + "class Foo {\n"
                    + "  String log = \"\";\n"
                    + "  void bar(List<String> list) {\n"
                    + "    list.forEach(s -> { log = log + s; });\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            ASTAssignmentExpression assignment = lambda.descendants(ASTAssignmentExpression.class).first();
            assertTrue(isWithinLoop(assignment));
        }

        @Test
        @DisplayName("Node inside traditional for loop is within loop")
        void testInsideTraditionalForLoop() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.List;\n"
                    + "class Foo {\n"
                    + "  String log = \"\";\n"
                    + "  void bar(List<String> list) {\n"
                    + "    for (String s : list) { log = log + s; }\n"
                    + "  }\n"
                    + "}\n");
            ASTAssignmentExpression assignment = root.descendants(ASTAssignmentExpression.class).first();
            assertTrue(isWithinLoop(assignment));
        }

        @Test
        @DisplayName("Node inside non-loop lambda (Optional.ifPresent) is NOT within loop")
        void testInsideNonLoopLambda() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.Optional;\n"
                    + "class Foo {\n"
                    + "  String log = \"\";\n"
                    + "  void bar(Optional<String> opt) {\n"
                    + "    opt.ifPresent(s -> { log = log + s; });\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            ASTAssignmentExpression assignment = lambda.descendants(ASTAssignmentExpression.class).first();
            assertFalse(isWithinLoop(assignment));
        }

        @Test
        @DisplayName("Node inside non-loop lambda enclosed by a loop IS within loop")
        void testNonLoopLambdaInsideLoop() {
            ASTCompilationUnit root = java.parse(""
                    + "import java.util.List;\n"
                    + "import java.util.Optional;\n"
                    + "class Foo {\n"
                    + "  String log = \"\";\n"
                    + "  void bar(List<Optional<String>> list) {\n"
                    + "    for (Optional<String> opt : list) {\n"
                    + "      opt.ifPresent(s -> { log = log + s; });\n"
                    + "    }\n"
                    + "  }\n"
                    + "}\n");
            ASTLambdaExpression lambda = root.descendants(ASTLambdaExpression.class).first();
            ASTAssignmentExpression assignment = lambda.descendants(ASTAssignmentExpression.class).first();
            assertTrue(isWithinLoop(assignment));
        }

        @Test
        @DisplayName("Node directly in method body is NOT within loop")
        void testDirectlyInMethod() {
            ASTCompilationUnit root = java.parse(""
                    + "class Foo {\n"
                    + "  String log = \"\";\n"
                    + "  void bar() {\n"
                    + "    log = log + \"abc\";\n"
                    + "  }\n"
                    + "}\n");
            ASTAssignmentExpression assignment = root.descendants(ASTAssignmentExpression.class).first();
            assertFalse(isWithinLoop(assignment));
        }
    }

    @Nested
    @DisplayName("End-to-end rule violation tests for issue #7271")
    class Issue7271RuleExecutionTests {

        @Test
        @DisplayName("Append inside forEach lambda reports UseStringBufferForStringAppends violation")
        void testIssue7271VariantReportsViolation() {
            String code = ""
                    + "import java.util.List;\n"
                    + "public class AppendInLoop {\n"
                    + "    private String log = \"\";\n"
                    + "    void collect(List<String> values) {\n"
                    + "        values.forEach(val -> {\n"
                    + "            log = log + val + \", \";\n"
                    + "        });\n"
                    + "    }\n"
                    + "}\n";

            List<RuleViolation> violations = analyzeCode(code);
            assertEquals(1, violations.size());
            assertEquals(6, violations.get(0).getBeginLine());
        }

        @Test
        @DisplayName("Append inside traditional for-each loop reports UseStringBufferForStringAppends violation")
        void testIssue7271SeedReportsViolation() {
            String code = ""
                    + "import java.util.List;\n"
                    + "public class AppendInLoop {\n"
                    + "    private String log = \"\";\n"
                    + "    void collect(List<String> values) {\n"
                    + "        for (String val : values) {\n"
                    + "            log = log + val + \", \";\n"
                    + "        }\n"
                    + "    }\n"
                    + "}\n";

            List<RuleViolation> violations = analyzeCode(code);
            assertEquals(1, violations.size());
            assertEquals(6, violations.get(0).getBeginLine());
        }

        @Test
        @DisplayName("Append inside non-loop lambda (Optional.ifPresent) does NOT report violation for field")
        void testNonLoopLambdaDoesNotReportViolation() {
            String code = ""
                    + "import java.util.Optional;\n"
                    + "public class AppendNonLoop {\n"
                    + "    private String log = \"\";\n"
                    + "    void collect(Optional<String> value) {\n"
                    + "        value.ifPresent(val -> {\n"
                    + "            log = log + val + \", \";\n"
                    + "        });\n"
                    + "    }\n"
                    + "}\n";

            List<RuleViolation> violations = analyzeCode(code);
            assertEquals(0, violations.size());
        }
    }

    private List<RuleViolation> analyzeCode(String code) {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setIgnoreIncrementalAnalysis(true);
        configuration.setThreads(0);
        final List<Rule> rules = new ArrayList<>(getRules());

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.files().addFile(TextFile.forCharSeq(code, FileId.fromPathLikeString("Test.java"),
                    configuration.getLanguageVersionOfFile("Test.java")));

            pmd.addRuleSet(RuleSet.forSingleRule(rules.get(0)));
            pmd.addListener(GlobalAnalysisListener.exceptionThrower());
            Report report = pmd.performAnalysisAndCollectReport();
            return report.getViolations();
        }
    }
}

