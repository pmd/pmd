/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.shouldMatchMethod
import net.sourceforge.pmd.lang.java.types.typeDsl

/**
 * @author Clément Fournier
 */
class AnonCtorsTest : ProcessorTestSpec({


    parserTest("Diamond anonymous class constructor") {

        val acu = parser.parse(
                """
            class Scratch {

                interface Gen<T> { T get(); }

                static <T> T useGen(Gen<? extends T> t_Gen) {
                    return t_Gen.get();
                }

                {
                 Integer result2 = useGen(new Gen<>() { public Integer get() { return 1; } });
                }
            }
            """)

        val (t_Scratch, t_Gen, t_Anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTMethodCall::class.java).get(1)!!

        call.shouldMatchN {
            methodCall("useGen") {

                it.methodType.formalParameters.shouldBe(listOf(with(it.typeDsl) {
                    t_Gen[`?` extends int.box()] // Gen<? extends Integer>
                }))

                argList {
                    constructorCall {
                        classType("Gen") {
                            it.typeMirror shouldBe t_Gen
                            diamond()
                        }

                        argList(0)

                        with(it.typeDsl) {
                            it.methodType.shouldMatchMethod(
                                    named = JConstructorSymbol.CTOR_NAME,
                                    declaredIn = ts.OBJECT,
                                    withFormals = emptyList(),
                                    returning = t_Gen[int.box()] // Gen<Integer>
                            ).also {
                                it.symbol shouldBe ts.OBJECT.symbol.constructors[0]
                            }
                        }

                        child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {
                            it.typeMirror shouldBe t_Anon
                        }
                    }
                }
            }
        }
    }


    parserTest("Test anonymous interface constructor") {

        val acu = parser.parse(
                """
            class Scratch {
                public interface BitMetric {
                    public double getBitLength(int value);
                }

                private final BitMetric t_BitMetric = new BitMetric() {
                    public double getBitLength(int value) {
                        return value;
                    }
                };
            }
            """)

        val (t_Scratch, t_BitMetric, t_Anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        call.shouldMatchN {
            constructorCall {
                classType("BitMetric") {
                    it.typeMirror.symbol shouldBe t_BitMetric.symbol
                }

                with(it.typeDsl) {
                    it.methodType.shouldMatchMethod(
                            named = JConstructorSymbol.CTOR_NAME,
                            declaredIn = ts.OBJECT,
                            withFormals = emptyList(),
                            returning = t_BitMetric
                    ).also {
                        it.symbol shouldBe ts.OBJECT.symbol.constructors[0]
                    }
                }
                it.typeMirror shouldBe t_BitMetric

                argList {}

                child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {}
            }
        }

    }


    parserTest("Test anonymous class constructor") {

        val acu = parser.parse(
                """
            class Scratch {
                public abstract class BitMetric {
                    public BitMetric(int i) {}

                    public abstract double getBitLength(int value);
                }

                private final BitMetric t_BitMetric = new BitMetric(4) {
                    public double getBitLength(int value) {
                        return value;
                    }
                };
            }
            """)

        val (t_Scratch, t_BitMetric, t_Anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        call.shouldMatchN {
            constructorCall {
                classType("BitMetric") {
                    it.typeMirror.symbol shouldBe t_BitMetric.symbol
                }

                with(it.typeDsl) {
                    it.methodType.shouldMatchMethod(
                            named = JConstructorSymbol.CTOR_NAME,
                            declaredIn = t_BitMetric,
                            withFormals = listOf(int),
                            returning = t_BitMetric
                    ).also {
                        it.symbol shouldBe t_BitMetric.symbol.constructors[0]
                    }
                }

                argList {
                    int(4)
                }

                child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {
                    it.typeMirror shouldBe t_Anon // though

                }
            }
        }
    }



    parserTest("Test anonymous interface constructor in invocation ctx") {


        val acu = parser.parse(
                """
            class Scratch {
                public interface BitMetric {
                    public double getBitLength(int value);
                }

                static <T> T generic(T t) { return t; }

                private final BitMetric t_BitMetric = generic(new BitMetric() {
                    public double getBitLength(int value) {
                        return value;
                    }
                });
            }
            """)

        val (t_Scratch, t_BitMetric, t_Anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }


        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("generic") {

                it.methodType.shouldMatchMethod(
                        named = "generic",
                        declaredIn = t_Scratch,
                        withFormals = listOf(t_BitMetric),
                        returning = t_BitMetric
                )

                argList {

                    constructorCall {
                        classType("BitMetric") {
                            it.typeMirror.symbol shouldBe t_BitMetric.symbol
                        }

                        it.methodType.shouldMatchMethod(
                                named = JConstructorSymbol.CTOR_NAME,
                                declaredIn = call.typeSystem.OBJECT,
                                withFormals = emptyList(),
                                returning = t_BitMetric
                        ).also {
                            it.symbol shouldBe call.typeSystem.OBJECT.symbol.constructors[0]
                        }

                        it.typeMirror shouldBe t_BitMetric

                        argList {}

                        child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {}
                    }
                }
            }
        }
    }

})
