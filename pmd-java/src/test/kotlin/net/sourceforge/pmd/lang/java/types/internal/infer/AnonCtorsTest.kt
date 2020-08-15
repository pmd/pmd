/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.typeDsl

/**
 * @author Clément Fournier
 */
class AnonCtorsTest : ProcessorTestSpec({


    parserTest("Diamond anonymous class constructor") {
        inContext(ExpressionParsingCtx) {

            val acu = parser.parse(
                    """
            class Scratch {
            
                interface Gen<T> { T get(); }
            
                static <T> T useGen(Gen<? extends T> gen) {
                    return gen.get();
                }
            
                {
                 Integer result2 = useGen(new Gen<>() { public Integer get() { return 1; } });
                }
            }
            """)

            val (scratch, gen, anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

            val call = acu.descendants(ASTMethodCall::class.java).get(1)!!

            call.shouldMatchN {
                methodCall("useGen") {

                    it.methodType.formalParameters.shouldBe(listOf(with(it.typeDsl) {
                        gen[`?` extends int.box()] // Gen<? extends Integer>
                    }))

                    argList {
                        constructorCall {
                            classType("Gen") {
                                it.typeMirror.symbol shouldBe gen
                                diamond()
                            }

                            argList(0)

                            it.methodType.apply {
                                symbol shouldBe call.typeSystem.OBJECT.symbol.constructors[0]
                                returnType shouldBe with(it.typeDsl) {
                                    gen[int.box()] // Gen<Integer>
                                }
                            }

                            child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {
                                it.typeMirror shouldBe it.typeSystem.declaration(anon)
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test anonymous interface constructor") {
        inContext(ExpressionParsingCtx) {

            val acu = parser.parse(
                    """
            class Scratch {
                public interface BitMetric {
                    public double getBitLength(int value);
                }

                private final BitMetric bitMetric = new BitMetric() {
                    public double getBitLength(int value) {
                        return value;
                    }
                };
            }
            """)

            val (scratch, bitMetric, anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList()

            val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

            call.shouldMatchN {
                constructorCall {
                    classType("BitMetric") {
                        it.typeMirror.symbol shouldBe bitMetric.symbol
                    }

                    it.methodType.apply {
                        formalParameters shouldBe emptyList()
                        symbol shouldBe call.typeSystem.OBJECT.symbol.constructors[0]
                        returnType shouldBe call.typeSystem.OBJECT
                    }

                    it.typeMirror shouldBe anon.typeMirror

                    argList {}

                    child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {}
                }
            }

        }
    }

    parserTest("Test anonymous class constructor") {
        inContext(ExpressionParsingCtx) {

            val acu = parser.parse(
                    """
            class Scratch {
                public abstract class BitMetric {
                    public BitMetric(int i) {}

                    public abstract double getBitLength(int value);
                }

                private final BitMetric bitMetric = new BitMetric(4) {
                    public double getBitLength(int value) {
                        return value;
                    }
                };
            }
            """)

            val (scratch, bitMetric, anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList()

            val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

            call.shouldMatchN {
                constructorCall {
                    classType("BitMetric") {
                        it.typeMirror.symbol shouldBe bitMetric.symbol
                    }

                    it.methodType.apply {
                        symbol shouldBe bitMetric.symbol.constructors[0]
                        returnType shouldBe bitMetric.typeMirror
                    }

                    it.typeMirror shouldBe anon.typeMirror // though

                    argList {
                        int(4)
                    }

                    child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {
                        it.typeMirror shouldBe anon.typeMirror // though

                    }
                }
            }
        }
    }


})
