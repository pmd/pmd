/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTSwitchLikeTest extends BaseParserTest {

    @Test
    void exhaustiveSwitchSealedClassesAST() {
        Map<String, ASTSwitchLike> switchStatements = java.parse(
                "import net.sourceforge.pmd.lang.java.rule.bestpractices.switchstmtsshouldhavedefault.SimpleEnum;\n"
                        + "public sealed class Foo {\n" + "    public static final class Sub1 extends Foo {}\n"
                        + "    public static final class Sub2 extends Foo {}\n" + "    void exhaustiveSealed(Foo x) {\n"
                        + "        switch (x) {\n"
                        + "            case Sub1 sub1 -> System.out.println(\"x is sub1\");\n"
                        + "            case Sub2 sub2 -> System.out.println(\"x is sub2\");\n" + "        }\n"
                        + "    }\n" + "    void exhaustiveSealedWithDefault(Foo x) {\n" + "        switch (x) {\n"
                        + "            case Sub1 sub1 -> System.out.println(\"x is sub1\");\n"
                        + "            case Sub2 sub2 -> System.out.println(\"x is sub2\");\n"
                        + "            default -> System.out.println(\"unnecessary default\");\n" + "        }\n"
                        + "    }\n" + "    void notExhaustiveSealed(Foo x) {\n" + "        switch (x) {\n"
                        + "            case Sub1 sub1 -> System.out.println(\"x is sub1\");\n"
                        + "            // sub2 is missing -> would be a compile error\n" + "        }\n" + "    }\n"
                        + "    void notExhaustiveSealedWithDefault(Foo x) {\n" + "        switch (x) {\n"
                        + "            case Sub1 sub1 -> System.out.println(\"x is sub1\");\n"
                        + "            // sub2 is missing\n"
                        + "            default -> System.out.println(\"anything else\");\n" + "        }\n" + "    }\n"
                        + "    void exhaustiveEnum(SimpleEnum x) {\n" + "        switch (x) {\n"
                        + "            case FOO -> System.out.println(\"x is foo\");\n"
                        + "            case BAR -> System.out.println(\"x is bar\");\n"
                        + "            case BZAZ -> System.out.println(\" x is bzaz\");\n" + "        }\n" + "    }\n"
                        + "    void exhaustiveEnumWithDefault(SimpleEnum x) {\n" + "        switch (x) {\n"
                        + "            case FOO -> System.out.println(\"x is foo\");\n"
                        + "            case BAR -> System.out.println(\"x is bar\");\n"
                        + "            case BZAZ -> System.out.println(\" x is bzaz\");\n"
                        + "            default -> System.out.println(\"unnecessary default\");\n" + "        }\n"
                        + "    }\n" + "    void notExhaustiveEnum(SimpleEnum x) {\n" + "        switch (x) {\n"
                        + "            case FOO -> System.out.println(\"x is foo\");\n"
                        + "            // missing: case BAR -> System.out.println(\"x is bar\");\n"
                        + "            case BZAZ -> System.out.println(\" x is bzaz\");\n" + "        }\n" + "    }\n"
                        + "    void notExhaustiveEnumWithDefault(SimpleEnum x) {\n" + "        switch (x) {\n"
                        + "            case FOO -> System.out.println(\"x is foo\");\n"
                        + "            //missing: case BAR -> System.out.println(\"x is bar\");\n"
                        + "            case BZAZ -> System.out.println(\" x is bzaz\");\n"
                        + "            default -> System.out.println(\"unnecessary default\");\n" + "        }\n"
                        + "    }\n" + "}")
                .descendants(ASTSwitchLike.class).collect(Collectors.toMap(
                        s -> s.ancestors(ASTMethodDeclaration.class).firstOrThrow().getName(), Function.identity()));

        assertAll(() -> assertSwitch(switchStatements.get("exhaustiveSealed"), false, true, false),
                () -> assertSwitch(switchStatements.get("exhaustiveSealedWithDefault"), false, true, true),
                // Note: the method "notExhaustiveSealed" doesn't actually compile - it is not
                // exhaustive and doesn't have a default
                // the implementation of #isExhaustive uses a shortcut by assuming, the code it
                // sees compiles and assumes it is exhausive...
                // () -> assertSwitch(switchStatements.get("notExhaustiveSealed"), false, false,
                // false),
                () -> assertSwitch(switchStatements.get("notExhaustiveSealedWithDefault"), false, false, true),
                () -> assertSwitch(switchStatements.get("exhaustiveEnum"), true, true, false),
                () -> assertSwitch(switchStatements.get("exhaustiveEnumWithDefault"), true, true, true),
                () -> assertSwitch(switchStatements.get("notExhaustiveEnum"), true, false, false),
                () -> assertSwitch(switchStatements.get("notExhaustiveEnumWithDefault"), true, false, true));
    }

    @Test
    void exhaustiveSwitchExpressionSealedClassesAST() {
        Map<String, ASTSwitchLike> switchExpressions = java.parse("public sealed class Foo {\n"
                + "    public static final class Sub1 extends Foo {}\n"
                + "    public static final class Sub2 extends Foo {}\n" + "    String exhaustiveSealed(Foo x) {\n"
                + "        return switch (x) {\n" + "            case Sub1 sub1 -> \"x is sub1\";\n"
                + "            case Sub2 sub2 -> \"x is sub2\";\n" + "        };\n" + "    }\n"
                + "    String exhaustiveSealedWithDefault(Foo x) {\n" + "        return switch (x) {\n"
                + "            case Sub1 sub1 -> \"x is sub1\";\n" + "            case Sub2 sub2 -> \"x is sub2\";\n"
                + "            default -> \"unnecessary default\";\n" + "        };\n" + "    }\n"
                + "    String notExhaustiveSealedWithDefault(Foo x) {\n" + "        return switch (x) {\n"
                + "            case Sub1 sub1 -> \"x is sub1\";\n" + "            // sub2 is missing\n"
                + "            default -> \"anything else\";\n" + "        };\n" + "    }\n" + "}")
                .descendants(ASTSwitchLike.class).collect(Collectors.toMap(
                        s -> s.ancestors(ASTMethodDeclaration.class).firstOrThrow().getName(), Function.identity()));

        assertAll(() -> assertSwitch(switchExpressions.get("exhaustiveSealed"), false, true, false),
                () -> assertSwitch(switchExpressions.get("exhaustiveSealedWithDefault"), false, true, true),
                () -> assertSwitch(switchExpressions.get("notExhaustiveSealedWithDefault"), false, false, true));
    }

    @Test
    void exhaustiveSwitchStatementSealedASM() {
        // net.sourceforge.pmd.lang.java.symbols.testdata.sealed.SealedTypesTestData
        Map<String, ASTSwitchLike> switchStatements = java
                .parse("import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.SealedTypesTestData;\n"
                        + "import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.A;\n"
                        + "import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.B;\n"
                        + "import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.C;\n"
                        + "import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.X;\n" + "public class Foo {\n"
                        + "    void exhaustiveSealed(SealedTypesTestData x) {\n" + "        switch (x) {\n"
                        + "            case A a -> System.out.println(\"x is a\");\n"
                        + "            case B b -> System.out.println(\"x is b\");\n"
                        + "            case C c -> System.out.println(\"x is c\");\n" + "        }\n" + "    }\n"
                        + "    void exhaustiveSealedX(SealedTypesTestData x) {\n" + "        switch (x) {\n"
                        + "            // X is the only subtype of A, so this switch is still exhaustive\n"
                        + "            case X x2 -> System.out.println(\"x is x\");\n"
                        + "            case B b -> System.out.println(\"x is b\");\n"
                        + "            case C c -> System.out.println(\"x is c\");\n" + "        }\n" + "    }\n" + "}")
                .descendants(ASTSwitchLike.class).collect(Collectors.toMap(
                        s -> s.ancestors(ASTMethodDeclaration.class).firstOrThrow().getName(), Function.identity()));

        assertAll(() -> assertSwitch(switchStatements.get("exhaustiveSealed"), false, true, false),
                () -> assertSwitch(switchStatements.get("exhaustiveSealedX"), false, true, false));
    }

    private static void assertSwitch(ASTSwitchLike switchLike, boolean isEnum, boolean isExhaustive,
            boolean hasDefault) {
        assertEquals(isEnum, switchLike.isEnumSwitch(), "wrong isEnumSwitch");
        assertEquals(isEnum & isExhaustive, switchLike.isExhaustiveEnumSwitch(), "wrong isExhaustiveEnumSwitch");
        assertEquals(isExhaustive, switchLike.isExhaustive(), "wrong isExhaustive");
        assertEquals(hasDefault, switchLike.hasDefaultCase(), "wrong hasDefaultCase");
    }
}
