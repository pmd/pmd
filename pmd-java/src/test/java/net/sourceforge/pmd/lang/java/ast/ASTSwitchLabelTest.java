/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

class ASTSwitchLabelTest extends BaseParserTest {

    private final JavaParsingHelper java = JavaParsingHelper.DEFAULT;

    @Test
    void testDefaultOff() {
        List<ASTSwitchLabel> ops = java.getNodes(ASTSwitchLabel.class, "public class Foo {\n void bar() {\n  switch (x) {\n   case 1: y = 2;\n  }\n }\n}");
        assertFalse(ops.get(0).isDefault());
    }

    @Test
    void testDefaultSet() {
        @NonNull ASTSwitchStatement switchStmt = java.parse(SWITCH_WITH_DEFAULT).descendants(ASTSwitchStatement.class).firstOrThrow();
        assertTrue(switchStmt.hasDefaultCase());
        assertFalse(switchStmt.isExhaustiveEnumSwitch());
        assertTrue(switchStmt.getBranches().firstOrThrow().getLabel().isDefault());
        assertFalse(switchStmt.getBranches().get(1).getLabel().isDefault());
    }

    @Test
    void testExhaustiveEnum() {
        @NonNull ASTSwitchStatement switchStmt = java.parse(EXHAUSTIVE_ENUM).descendants(ASTSwitchStatement.class).firstOrThrow();
        assertFalse(switchStmt.hasDefaultCase());
        assertTrue(switchStmt.isExhaustiveEnumSwitch());
    }

    @Test
    void testNotExhaustiveEnum() {
        @NonNull ASTSwitchStatement switchStmt = java.parse(NOT_EXHAUSTIVE_ENUM).descendants(ASTSwitchStatement.class).firstOrThrow();
        assertFalse(switchStmt.hasDefaultCase());
        assertFalse(switchStmt.isExhaustiveEnumSwitch());
    }

    @Test
    void testEnumWithDefault() {
        @NonNull ASTSwitchStatement switchStmt = java.parse(ENUM_SWITCH_WITH_DEFAULT).descendants(ASTSwitchStatement.class).firstOrThrow();
        assertTrue(switchStmt.hasDefaultCase());
        assertFalse(switchStmt.isExhaustiveEnumSwitch());
    }

    private static final String SWITCH_WITH_DEFAULT =
        "public class Foo {\n"
            + " void bar() {\n"
            + "  switch (x) {\n"
            + "   default: y = 2;\n"
            + "   case 4: break;\n"
            + "  }\n"
            + " }\n"
            + "}";

    private static final String EXHAUSTIVE_ENUM =
        "public class Foo {\n"
            + " void bar() {\n"
            + "  enum LocalEnum { A, B, C } "
            + "  var v = LocalEnum.A; "
            + "  switch (v) {\n"
            + "   case A: break;\n"
            + "   case B: break;\n"
            + "   case C: break;\n"
            + "  }\n"
            + " }\n"
            + "}";

    private static final String NOT_EXHAUSTIVE_ENUM =
        "public class Foo {\n"
            + " void bar() {\n"
            + "  enum LocalEnum { A, B, C } "
            + "  var v = LocalEnum.A; "
            + "  switch (v) {\n"
            + "   case A: break;\n"
            + " //  case B: break;\n"
            + "   case C: break;\n"
            + "  }\n"
            + " }\n"
            + "}";

    private static final String ENUM_SWITCH_WITH_DEFAULT =
        "public class Foo {\n"
            + " void bar() {\n"
            + "  enum LocalEnum { A, B, C } "
            + "  var v = LocalEnum.A; "
            + "  switch (v) {\n"
            + "   case A: break;\n"
            + "   case C: break;\n"
            + "   default: break;\n"
            + "  }\n"
            + " }\n"
            + "}";
}
