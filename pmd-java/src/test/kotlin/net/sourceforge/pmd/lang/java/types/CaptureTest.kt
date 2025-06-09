/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import net.sourceforge.pmd.lang.java.ast.ASTVariableId
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol
import net.sourceforge.pmd.lang.java.types.TypeConversion.capture
import net.sourceforge.pmd.lang.test.ast.IntelliMarker

/**
 * @author Clément Fournier
 */
class CaptureTest : IntelliMarker, FunSpec({

    with(TypeDslOf(testTypeSystem)) {
        with(gen) {

            test("Capture is a noop on raw types") {

                capture(t_List) shouldBe t_List

            }

            test("Capture merges declared bounds and bounds of wildcard") {

                val t_Scratch = javaParser.parseSomeClass("class Scratch<T extends java.util.List<T>> {}")

                val matcher = captureMatcher(`?`)

                capture(t_Scratch[`?`]) shouldBe t_Scratch[matcher]

                matcher.also {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(`?`) shouldBe true
                    it.upperBound shouldBe t_List[matcher]
                    it.lowerBound shouldBe ts.NULL_TYPE
                }
            }

            test("Capture merges declared bounds and bounds of wildcard (self referential bound)") {

                val t_Scratch = javaParser.parseSomeClass("class Scratch<T extends Scratch<T>> {}")

                val matcher = captureMatcher(`?`)

                capture(t_Scratch[`?`]) shouldBe t_Scratch[matcher]

                matcher.also {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(`?`) shouldBe true
                    it.upperBound shouldBe t_Scratch[matcher]
                    it.lowerBound shouldBe ts.NULL_TYPE
                }
            }

            test("Capture of malformed types") {
                val sym = ts.createUnresolvedAsmSymbol("does.not.Exist")

                val matcher = captureMatcher(`?` extends t_String).also {
                    capture(sym[t_String, `?` extends t_String]) shouldBe sym[t_String, it]
                }

                matcher.also {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(`?` extends t_String) shouldBe true
                }
            }

            test("Capture of recursive types #5006") {
                val acu = javaParser.parse("""
                    class Parent<T extends Parent<T>> {} // Recursive type T

                    class Child extends Parent<Child> {}

                    class Box<T extends Parent<T>> {
                      public void foo(Box<? extends Child> box) {} // <-- The bug is triggered by this line
                    }
                """.trimIndent())

                val (_, child, box) = acu.declaredTypeSignatures()

                val type = acu.methodDeclarations().first()!!.formalParameters.get(0).typeMirror

                 capture(type) shouldBe box[captureMatcher(`?` extends child)]
            }

            test("Capture of recursive types #5096") {
                val acu = javaParser.parse(
                    """

                    class Child<C extends Child<? extends C>> {
                      Child(C t) {
                        super(t); // <-- The bug is triggered by capture of the `t` here
                      } 
                    }
                """.trimIndent()
                )

                val (child) = acu.declaredTypeSignatures()

                val parm = acu.descendants(ASTVariableId::class.java).firstOrThrow()

                parm shouldHaveType child.typeArgs[0]
                val captured = capture(parm.typeMirror)
                captured shouldBeSameInstanceAs parm.typeMirror
            }



        }
    }


})
