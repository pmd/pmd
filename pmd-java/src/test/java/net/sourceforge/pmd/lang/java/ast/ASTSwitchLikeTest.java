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
        Map<String, ASTSwitchLike> switchStatements =
                java.parse(
                                """
                                import net.sourceforge.pmd.lang.java.rule.bestpractices.switchstmtsshouldhavedefault.SimpleEnum;
                                public sealed class Foo {
                                    public static final class Sub1 extends Foo {}
                                    public static final class Sub2 extends Foo {}
                                    void exhaustiveSealed(Foo x) {
                                        switch (x) {
                                            case Sub1 sub1 -> System.out.println("x is sub1");
                                            case Sub2 sub2 -> System.out.println("x is sub2");
                                        }
                                    }
                                    void exhaustiveSealedWithDefault(Foo x) {
                                        switch (x) {
                                            case Sub1 sub1 -> System.out.println("x is sub1");
                                            case Sub2 sub2 -> System.out.println("x is sub2");
                                            default -> System.out.println("unnecessary default");
                                        }
                                    }
                                    void notExhaustiveSealed(Foo x) {
                                        switch (x) {
                                            case Sub1 sub1 -> System.out.println("x is sub1");
                                            // sub2 is missing -> would be a compile error
                                        }
                                    }
                                    void notExhaustiveSealedWithDefault(Foo x) {
                                        switch (x) {
                                            case Sub1 sub1 -> System.out.println("x is sub1");
                                            // sub2 is missing
                                            default -> System.out.println("anything else");
                                        }
                                    }
                                    void exhaustiveEnum(SimpleEnum x) {
                                        switch (x) {
                                            case FOO -> System.out.println("x is foo");
                                            case BAR -> System.out.println("x is bar");
                                            case BZAZ -> System.out.println(" x is bzaz");
                                        }
                                    }
                                    void exhaustiveEnumWithDefault(SimpleEnum x) {
                                        switch (x) {
                                            case FOO -> System.out.println("x is foo");
                                            case BAR -> System.out.println("x is bar");
                                            case BZAZ -> System.out.println(" x is bzaz");
                                            default -> System.out.println("unnecessary default");
                                        }
                                    }
                                    void notExhaustiveEnum(SimpleEnum x) {
                                        switch (x) {
                                            case FOO -> System.out.println("x is foo");
                                            // missing: case BAR -> System.out.println("x is bar");
                                            case BZAZ -> System.out.println(" x is bzaz");
                                        }
                                    }
                                    void notExhaustiveEnumWithDefault(SimpleEnum x) {
                                        switch (x) {
                                            case FOO -> System.out.println("x is foo");
                                            //missing: case BAR -> System.out.println("x is bar");
                                            case BZAZ -> System.out.println(" x is bzaz");
                                            default -> System.out.println("unnecessary default");
                                        }
                                    }
                                }\
                                """)
                        .descendants(ASTSwitchLike.class)
                        .collect(Collectors.toMap(s -> s.ancestors(ASTMethodDeclaration.class).firstOrThrow().getName(),
                                Function.identity()));

        assertAll(
                () -> assertSwitch(switchStatements.get("exhaustiveSealed"), false, true, false),
                () -> assertSwitch(switchStatements.get("exhaustiveSealedWithDefault"), false, true, true),
                // Note: the method "notExhaustiveSealed" doesn't actually compile - it is not exhaustive and doesn't have a default
                // the implementation of #isExhaustive uses a shortcut by assuming, the code it sees compiles and assumes it is exhausive...
                //() -> assertSwitch(switchStatements.get("notExhaustiveSealed"), false, false, false),
                () -> assertSwitch(switchStatements.get("notExhaustiveSealedWithDefault"), false, false, true),
                () -> assertSwitch(switchStatements.get("exhaustiveEnum"), true, true, false),
                () -> assertSwitch(switchStatements.get("exhaustiveEnumWithDefault"), true, true, true),
                () -> assertSwitch(switchStatements.get("notExhaustiveEnum"), true, false, false),
                () -> assertSwitch(switchStatements.get("notExhaustiveEnumWithDefault"), true, false, true)
        );
    }

    @Test
    void exhaustiveSwitchExpressionSealedClassesAST() {
        Map<String, ASTSwitchLike> switchExpressions =
                java.parse(
                                        """
                                        public sealed class Foo {
                                            public static final class Sub1 extends Foo {}
                                            public static final class Sub2 extends Foo {}
                                            String exhaustiveSealed(Foo x) {
                                                return switch (x) {
                                                    case Sub1 sub1 -> "x is sub1";
                                                    case Sub2 sub2 -> "x is sub2";
                                                };
                                            }
                                            String exhaustiveSealedWithDefault(Foo x) {
                                                return switch (x) {
                                                    case Sub1 sub1 -> "x is sub1";
                                                    case Sub2 sub2 -> "x is sub2";
                                                    default -> "unnecessary default";
                                                };
                                            }
                                            String notExhaustiveSealedWithDefault(Foo x) {
                                                return switch (x) {
                                                    case Sub1 sub1 -> "x is sub1";
                                                    // sub2 is missing
                                                    default -> "anything else";
                                                };
                                            }
                                        }\
                                        """)
                        .descendants(ASTSwitchLike.class)
                        .collect(Collectors.toMap(s -> s.ancestors(ASTMethodDeclaration.class).firstOrThrow().getName(),
                                Function.identity()));

        assertAll(
                () -> assertSwitch(switchExpressions.get("exhaustiveSealed"), false, true, false),
                () -> assertSwitch(switchExpressions.get("exhaustiveSealedWithDefault"), false, true, true),
                () -> assertSwitch(switchExpressions.get("notExhaustiveSealedWithDefault"), false, false, true)
        );
    }

    @Test
    void exhaustiveSwitchStatementSealedASM() {
        // net.sourceforge.pmd.lang.java.symbols.testdata.sealed.SealedTypesTestData
        Map<String, ASTSwitchLike> switchStatements =
                java.parse(
                                """
                                import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.SealedTypesTestData;
                                import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.A;
                                import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.B;
                                import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.C;
                                import net.sourceforge.pmd.lang.java.symbols.testdata.sealed.X;
                                public class Foo {
                                    void exhaustiveSealed(SealedTypesTestData x) {
                                        switch (x) {
                                            case A a -> System.out.println("x is a");
                                            case B b -> System.out.println("x is b");
                                            case C c -> System.out.println("x is c");
                                        }
                                    }
                                    void exhaustiveSealedX(SealedTypesTestData x) {
                                        switch (x) {
                                            // X is the only subtype of A, so this switch is still exhaustive
                                            case X x2 -> System.out.println("x is x");
                                            case B b -> System.out.println("x is b");
                                            case C c -> System.out.println("x is c");
                                        }
                                    }
                                }\
                                """)
                        .descendants(ASTSwitchLike.class)
                        .collect(Collectors.toMap(s -> s.ancestors(ASTMethodDeclaration.class).firstOrThrow().getName(),
                                Function.identity()));

        assertAll(
                () -> assertSwitch(switchStatements.get("exhaustiveSealed"), false, true, false),
                () -> assertSwitch(switchStatements.get("exhaustiveSealedX"), false, true, false)
        );
    }

    private static void assertSwitch(ASTSwitchLike switchLike, boolean isEnum, boolean isExhaustive, boolean hasDefault) {
        assertEquals(isEnum, switchLike.isEnumSwitch(), "wrong isEnumSwitch");
        assertEquals(isEnum & isExhaustive, switchLike.isExhaustiveEnumSwitch(), "wrong isExhaustiveEnumSwitch");
        assertEquals(isExhaustive, switchLike.isExhaustive(), "wrong isExhaustive");
        assertEquals(hasDefault, switchLike.hasDefaultCase(), "wrong hasDefaultCase");
    }
}
