/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.test.ast.shouldBeA

class ConstValuesKotlinTest : ProcessorTestSpec({
    parserTest("Reference cycles do not crash constant folding") {
        val acu = parser.parse(
            """
            class Foo {
                static final int I1 = I2;
                static final int I2 = I1;
                static final int I3 = 0;
            }
            """.trimIndent()
        )

        acu.initializerOf("I1").constValue shouldBe null
        acu.initializerOf("I2").constValue shouldBe null
        acu.initializerOf("I3").constValue shouldBe 0
        acu.descendants(ASTVariableId::class.java).last()!!.symbol.shouldBeA<JFieldSymbol> {
            it.constValue shouldBe 0
        }
    }

    parserTest("Constant expressions can combine local and instance constants") {
        val acu = parser.parse(
            """
            class Foo {
                final int base = 2;
                static final int OFFSET = 2;
                {
                    final int factor = 4;
                    int combined = factor * 4 + base;
                    int fromStatic = 4 + OFFSET;
                }
            }
            """.trimIndent()
        )

        acu.initializerOf("combined").constValue shouldBe 18
        acu.initializerOf("fromStatic").constValue shouldBe 6
    }

    parserTest("Array lengths retain their constant classification (#7060)") {
        val acu = parser.parse(
            """
            class Foo {
                void test() {
                    final int size = 8;
                    byte[] array = new byte[size * 2];
                }
            }
            """.trimIndent()
        )

        val length = acu.descendants(ASTArrayDimExpr::class.java).first()!!.lengthExpression
        // A folded value alone is insufficient: getConstValue must expose it to rules.
        length.constFoldingResult.value shouldBe 16
        length.isCompileTimeConstant shouldBe true
        length.constValue shouldBe 16
    }

    parserTest("Local String constants retain their classification through initializer chains") {
        val acu = parser.parse(
            """
            class Foo {
                void test() {
                    final String prefix = "hello";
                    final String message = prefix + " world";
                    String result = message;
                }
            }
            """.trimIndent()
        )

        acu.initializerOf("result").constValue shouldBe "hello world"
    }

    parserTest("Unboxing a local variable is foldable but is not a constant expression") {
        val acu = parser.parse(
            """
            class Foo {
                void test() {
                    final Integer boxed = 8;
                    final int unboxed = boxed;
                    int result = unboxed * 2;
                }
            }
            """.trimIndent()
        )

        acu.initializerOf("unboxed").constFoldingResult.value shouldBe 8
        acu.initializerOf("unboxed").constValue shouldBe null
        acu.initializerOf("result").constFoldingResult.value shouldBe 16
        acu.initializerOf("result").constValue shouldBe null
    }

    parserTest("Unboxing a static field is foldable but is not a constant expression") {
        val acu = parser.parse(
            """
            class Foo {
                static final Integer BOXED = 8;
                final int unboxed = BOXED;
                int result = unboxed * 2;
            }
            """.trimIndent()
        )

        acu.initializerOf("unboxed").constFoldingResult.value shouldBe 8
        acu.initializerOf("unboxed").constValue shouldBe null
        acu.initializerOf("result").constFoldingResult.value shouldBe 16
        acu.initializerOf("result").constValue shouldBe null
    }

    parserTest("Local numeric constants use their declared types") {
        val acu = parser.parse(
            """
            class Foo {
                void test() {
                    final long value = 1;
                    final char letter = 65;
                    final double number = 1;
                    long shifted = value << 40;
                    String text = "" + letter;
                    double half = number / 2;
                }
            }
            """.trimIndent()
        )

        // The int initializers must be converted before evaluating references to the variables.
        acu.initializerOf("shifted").constValue shouldBe 1099511627776L
        acu.initializerOf("text").constValue shouldBe "A"
        acu.initializerOf("half").constValue shouldBe 0.5
    }

    parserTest("Instance numeric constants use their declared types") {
        val acu = parser.parse(
            """
            class Foo {
                final long value = 1;
                final char letter = 65;
                final double number = 1;
                long shifted = value << 40;
                String text = "" + letter;
                double half = number / 2;
            }
            """.trimIndent()
        )

        acu.initializerOf("shifted").constValue shouldBe 1099511627776L
        acu.initializerOf("text").constValue shouldBe "A"
        acu.initializerOf("half").constValue shouldBe 0.5
    }

    parserTest("Static numeric constants use their declared types") {
        val acu = parser.parse(
            """
            class Foo {
                static final long VALUE = 1;
                static final char LETTER = 65;
                static final double NUMBER = 1;
                long shifted = VALUE << 40;
                String text = "" + LETTER;
                double half = NUMBER / 2;
            }
            """.trimIndent()
        )

        acu.initializerOf("shifted").constValue shouldBe 1099511627776L
        acu.initializerOf("text").constValue shouldBe "A"
        acu.initializerOf("half").constValue shouldBe 0.5
    }

    parserTest("Qualifying a boxed field with a type name does not make it constant") {
        val acu = parser.parse(
            """
            class Foo {
                static final Integer BOXED = 8;
                Integer simple = BOXED;
                Integer qualified = Foo.BOXED;
            }
            """.trimIndent()
        )

        acu.initializerOf("simple").constValue shouldBe null
        acu.initializerOf("qualified").constValue shouldBe null
    }

    parserTest("Boxed field symbols do not expose a compile-time constant value") {
        val acu = parser.parse(
            """
            class Foo {
                static final Integer BOXED = 8;
            }
            """.trimIndent()
        )

        acu.descendants(ASTVariableId::class.java).toList().single().symbol.shouldBeA<JFieldSymbol> {
            it.constValue shouldBe null
        }
    }

    parserTest("Type-qualified numeric constants use their declared types") {
        val acu = parser.parse(
            """
            class Foo {
                static final long VALUE = 1;
                long result = Foo.VALUE << 40;
            }
            """.trimIndent()
        )

        acu.initializerOf("result").constValue shouldBe 1099511627776L
    }

    parserTest("This-qualified fields are not constant expressions") {
        val acu = parser.parse(
            """
            class Foo {
                final int instance = 8;
                static final int STATIC = 8;
                int fromInstance = this.instance;
                int fromStatic = this.STATIC;
            }
            """.trimIndent()
        )

        // JLS 15.29 permits a type name as qualifier, but not this.
        acu.initializerOf("fromInstance").constValue shouldBe null
        acu.initializerOf("fromStatic").constValue shouldBe null
    }

    parserTest("A method call qualifier prevents a field access from being a constant expression") {
        val acu = parser.parse(
            """
            class Foo {
                static final int VALUE = 8;
                int result = make().VALUE;

                static Foo make() {
                    return new Foo();
                }
            }
            """.trimIndent()
        )

        acu.initializerOf("result").constValue shouldBe null
    }

    parserTest("Final inferred locals can be constant variables") {
        val acu = parser.parse(
            """
            class Foo {
                void test() {
                    final var size = 8;
                    int result = size * 2;
                }
            }
            """.trimIndent()
        )

        acu.initializerOf("result").constValue shouldBe 16
    }

    parserTest("Effective finality does not make a variable constant") {
        val acu = parser.parse(
            """
            class Foo {
                void test() {
                    int value = 8;
                    int result = value;
                }
            }
            """.trimIndent()
        )

        acu.initializerOf("result").constValue shouldBe null
    }

    parserTest("A method call initializer does not make a final variable constant") {
        val acu = parser.parse(
            """
            class Foo {
                void test() {
                    final int called = value();
                    int result = called;
                }

                int value() {
                    return 8;
                }
            }
            """.trimIndent()
        )

        acu.initializerOf("result").constValue shouldBe null
    }

    parserTest("A later assignment does not make a blank final variable constant") {
        val acu = parser.parse(
            """
            class Foo {
                void test() {
                    final int blank;
                    blank = 8;
                    int result = blank;
                }
            }
            """.trimIndent()
        )

        acu.initializerOf("result").constValue shouldBe null
    }
})

private fun ASTCompilationUnit.initializerOf(name: String): ASTExpression =
    requireNotNull(descendants(ASTVariableId::class.java).first { it.name == name }?.initializer) {
        "Missing initializer for variable '$name'"
    }
