/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*

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
                            it.typeMirror shouldBe t_Gen.erasure
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

    parserTest("Test qualified anonymous class constructor") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

            class Scratch {

                class Inner {}

                public static void main(String[] args) {
                    new Scratch().new Inner() {

                    };
                }
            }
            """)

        val (t_Scratch, t_Inner, t_Anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        spy.shouldBeOk {
            call.shouldMatchN {
                constructorCall {
                    unspecifiedChildren(2)

                    it.typeMirror shouldBe t_Inner

                    t_Inner.shouldBeA<JClassType> {
                        it.enclosingType shouldBe t_Scratch
                    }

                    it.methodType.shouldMatchMethod(
                            named = JConstructorSymbol.CTOR_NAME,
                            declaredIn = t_Inner,
                            withFormals = emptyList(),
                            returning = t_Inner
                    ).also {
                        it.symbol shouldBe t_Inner.symbol.constructors[0]
                    }


                    argList(0)

                    child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {
                        it.typeMirror shouldBe t_Anon // though
                    }
                }
            }
        }
    }



    parserTest("Test qualified diamond anonymous class constructor") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

            class Scratch<S> {

                class Inner<T> {}

                public void main(String[] args) {
                    // note: this is invalid, because the Scratch<> diamond doesn't have context
                    // Inner<String> invalid = new Scratch<>().new Inner<>() {};

                    Scratch<S> s = null; // this type node needs to be disambiged early
                    Inner<String> invalid = s.new Inner<>() {

                    };
                }
            }
            """)

        val (t_Scratch, t_Inner, t_Anon) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        spy.shouldBeOk {
            call.shouldMatchN {
                constructorCall {
                    unspecifiedChildren(2)

                    it.typeMirror shouldBe t_Inner[gen.t_String]

                    t_Inner.shouldBeA<JClassType> {
                        it.enclosingType shouldBe t_Scratch
                    }

                    it.methodType.shouldMatchMethod(
                            named = JConstructorSymbol.CTOR_NAME,
                            declaredIn = t_Inner[gen.t_String],
                            withFormals = emptyList(),
                            returning = t_Inner[gen.t_String]
                    ).also {
                        it.symbol shouldBe t_Inner.symbol.constructors[0]
                    }


                    argList(0)

                    child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {
                        it.typeMirror shouldBe t_Anon // though
                    }
                }
            }
        }
    }



    parserTest("Test qualified diamond anonymous class constructor, depending on disambig in sibling tree") {


        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

            package p.q;

            class Scratch<S> {

                class Inner<T> extends Foo<S> { }

                public void main(String[] args) {
                    // getScratch's return type is ambiguous, it needs to be disambiged
                    // before the symbol table for inherited members of the new Inner is
                    // set, otherwise we don't know the actual type of the LHS
                    Scratch<Integer>.Inner<String> invalid = Foo.<java.lang.Integer>getScratch().new Inner<>() {
                        // stuff inherited here needs
                        {
                            // fooField is inherited through superclass Foo<S>
                            // it's declared later in the compilation unit
                            fooField.toString();
                        }
                    };
                }

                static class Foo<Q> {

                    // this return type is ambiguous
                    static <T> p.q.Scratch<T> getScratch() { return new Scratch<>(); }

                    Q fooField;
                }
            }
            """)

        val (t_Scratch, t_Inner, t_Anon, t_Foo) = acu.declaredTypeSignatures()


        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()
        val fieldAccess = acu.descendants(ASTVariableAccess::class.java).crossFindBoundaries().firstOrThrow()

        spy.shouldBeOk {

            // Scratch<Integer>.Inner<String>
            val innerT = t_Scratch[int.box()] / t_Inner[gen.t_String]

            call.shouldMatchN {
                constructorCall {
                    unspecifiedChildren(2)


                    it.typeMirror shouldBe innerT

                    it.methodType.shouldMatchMethod(
                            named = JConstructorSymbol.CTOR_NAME,
                            declaredIn = innerT,
                            withFormals = emptyList(),
                            returning = innerT
                    ).also {
                        it.symbol shouldBe t_Inner.symbol.constructors[0]
                    }


                    argList(0)

                    child<ASTAnonymousClassDeclaration>(ignoreChildren = true) {
                        it.typeMirror shouldBe t_Anon // though
                    }
                }
            }

            fieldAccess.shouldMatchN {
                variableAccess("fooField") {
                    it shouldHaveType int.box()
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


    parserTest("Test new method in anonymous class") {


        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
            interface Scratch {

                int k = new Scratch() {
                    int someNewMethod() { return 2; }
                }.someNewMethod();
            }
            """)

        val (t_Scratch, t_Anon) = acu.declaredTypeSignatures()

        val (methodDecl) = acu.declaredMethodSignatures()
        val call = acu.firstMethodCall()

        methodDecl.modifiers shouldBe 0

        spy.shouldBeOk {
            call.shouldMatchN {
                methodCall("someNewMethod") {

                    it.qualifier!! shouldHaveType t_Scratch
                    it.methodType shouldBeSomeInstantiationOf methodDecl

                    it::getQualifier shouldBe unspecifiedChild()

                    argList(0)
                }
            }
        }
    }

    parserTest("Anon in anon") {
        // this used to be a stackoverflow

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
            public class InputMissingOverrideBadAnnotation {

                Runnable r = new Runnable() {
                    public void run() {
                        Throwable t = new Throwable() {
                            public String toString() {
                                return "junk";
                            }
                        };
                    }
                };
            }
            """)

        val call = acu.firstCtorCall()
                .firstCtorCall()

        spy.shouldBeOk {
            call.typeMirror shouldBe java.lang.Throwable::class.decl
        }
    }

    parserTest("Disambiguation of foreach when deferred") {
        enableProcessing()

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
package p;
import java.util.function.Consumer;
class Assert {
    static void main(String... args) {
        for (String s : args) {
            // body of the runnable is deferred,
            // when it's processed, the type of the consumer
            // depends on the type of `s`, so the foreach node
            // needs to have been disambiged
            foo(s, new Consumer<>() { });
        }
    }

    static <T> void foo(T a, Consumer<T> i) {}
}
        """)

        spy.shouldBeOk {
            acu.descendants(ASTConstructorCall::class.java)
                    .firstOrThrow() shouldHaveType java.util.function.Consumer::class[gen.t_String]
        }
    }

    parserTest("Disambiguation of when deferred, local var decl") {
        enableProcessing()

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
package p;
import java.util.function.Consumer;
class Assert {
    static void main(String... args) {
        String s = args[0];
        foo(s, new Consumer<>() { });
    }

    static <T> void foo(T a, Consumer<T> i) {}
}
        """)

        spy.shouldBeOk {
            acu.descendants(ASTConstructorCall::class.java)
                    .firstOrThrow() shouldHaveType java.util.function.Consumer::class[gen.t_String]
        }
    }
})
