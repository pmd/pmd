/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldContainIgnoringCase
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.types.*

/**
 */
class UnresolvedTypesRecoveryTest : ProcessorTestSpec({

    parserTest("Test failed invoc context lets args be inferred as standalones") {

        val acu = parser.parse(
                """
import java.io.IOException;
import ooo.Unresolved;

class C {

    static {
        try { } catch (IOException ioe) {
            throw new Unresolved(ioe.getMessage(), ioe);
        }
    }
}

                """.trimIndent()
        )


        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        call.shouldMatchN {
            constructorCall {
                classType("Unresolved") {
                    TypeOps.isUnresolved(it.typeMirror) shouldBe true
                    it.typeMirror.symbol.shouldBeA<JClassSymbol> {
                        it.binaryName shouldBe "ooo.Unresolved"
                    }
                }

                it.methodType shouldBe it.typeSystem.UNRESOLVED_METHOD
                it.typeMirror shouldBe it.typeNode.typeMirror

                argList {
                    methodCall("getMessage") {
                        it.typeMirror shouldBe it.typeSystem.STRING
                        variableAccess("ioe")
                        argList {}
                    }
                    variableAccess("ioe")
                }
            }
        }
    }


    parserTest("Test constructor call fallback") {

        val acu = parser.parse(
                """
import java.io.IOException;
import ooo.Unresolved;

class C {

    static {
        new Unresolved();
    }
}

                """.trimIndent()
        )


        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        call.shouldMatchN {
            constructorCall {
                classType("Unresolved") {
                    TypeOps.isUnresolved(it.typeMirror) shouldBe true
                    it.typeMirror.symbol.shouldBeA<JClassSymbol> {
                        it.binaryName shouldBe "ooo.Unresolved"
                    }
                }

                it.methodType shouldBe it.typeSystem.UNRESOLVED_METHOD
                it.typeMirror shouldBe it.typeNode.typeMirror

                argList {}
            }
        }
    }

    parserTest("Test ctor fallback in invoc ctx") {

        val acu = parser.parse(
                """
import java.io.IOException;
import ooo.Unresolved;

class C {

    static <T> T id(T t) { return t; }

    static {
        // the ctor call is failed during inference of the outer method
        // The variable should be instantiated to *ooo.Unresolved anyway.
        id(new Unresolved());
    }
}

                """.trimIndent()
        )


        val t_Unresolved = acu.descendants(ASTConstructorCall::class.java).firstOrThrow().typeNode.typeMirror as JClassType

        TypeOps.isUnresolved(t_Unresolved) shouldBe true

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("id") {

                it.methodType.shouldMatchMethod("id", withFormals = listOf(t_Unresolved), returning = t_Unresolved)

                argList {
                    constructorCall {
                        classType("Unresolved")

                        it.methodType shouldBe it.typeSystem.UNRESOLVED_METHOD
                        it.typeMirror shouldBe it.typeNode.typeMirror

                        argList {}
                    }
                }
            }
        }
    }

    parserTest("Test diamond ctor for unresolved") {

        val acu = parser.parse(
                """
import java.io.IOException;
import ooo.Unresolved;

class C {

    static {
        Unresolved<String> s = new Unresolved<>();
    }
}

                """.trimIndent()
        )


        val t_UnresolvedOfString = acu.descendants(ASTClassOrInterfaceType::class.java)
                .first { it.simpleName == "Unresolved" }!!.typeMirror.shouldBeA<JClassType> {
                    it.isParameterizedType shouldBe true
                    it.typeArgs shouldBe listOf(it.typeSystem.STRING)
                }

        TypeOps.isUnresolved(t_UnresolvedOfString) shouldBe true


        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        call.shouldMatchN {
            constructorCall {
                classType("Unresolved")

                it.isDiamond shouldBe true

                it.methodType shouldBe it.typeSystem.UNRESOLVED_METHOD
                it.overloadSelectionInfo.isFailed shouldBe true
                it.typeMirror shouldBe t_UnresolvedOfString

                argList {}
            }
        }
    }


    parserTest("Recovery for primitives in strict invoc") {

        val acu = parser.parse(
                """
import ooo.Unresolved;

class C {

    static void id(int i) { }

    static {
        id(Unresolved.SOME_INT);
    }
}

                """.trimIndent()
        )


        val idMethod = acu.descendants(ASTMethodDeclaration::class.java).firstOrThrow().symbol

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("id") {

                with (it.typeDsl) {
                    it.methodType.shouldMatchMethod("id", withFormals = listOf(int), returning = void)
                    it.overloadSelectionInfo.isFailed shouldBe false // it succeeded
                    it.methodType.symbol shouldBe idMethod
                }

                argList {
                    fieldAccess("SOME_INT") {
                        it.typeMirror shouldBe it.typeSystem.UNRESOLVED_TYPE
                        typeExpr {
                            classType("Unresolved")
                        }
                    }
                }
            }
        }
    }

    parserTest("f:Recovery when there are several applicable overloads") {

        val logGetter = logTypeInference()

        val acu = parser.parse(
                """
import ooo.Unresolved;

class C {

    static {
        // What should be the correct behavior?
        // Select the most general overload, append(Object)?
        // Just inverting the specificity relation, to select the most general,
        // would not work well when there are several parameters.
        // Note that /*unresolved*/ and /*error*/ are the only types for which 
        // there is ambiguity
        
        // For now, report an ambiguity error
        new StringBuilder().append(Unresolved.SOMETHING);
    }
}

                """.trimIndent()
        )

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        logGetter().shouldBeEmpty()
        call.shouldMatchN {
            methodCall("append") {

                with (it.typeDsl) {
                    it.methodType.shouldMatchMethod("append", withFormals = listOf(ts.OBJECT), returning = gen.t_StringBuilder)
                    it.overloadSelectionInfo.isFailed shouldBe true // ambiguity
                }

                skipQualifier()

                argList {
                    fieldAccess("SOMETHING") {
                        it.typeMirror shouldBe it.typeSystem.UNRESOLVED_TYPE
                        typeExpr {
                            classType("Unresolved")
                        }
                    }
                }
            }
        }
        logGetter().shouldContainIgnoringCase("ambiguity")
    }

})
