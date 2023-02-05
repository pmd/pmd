/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldNotContainIgnoringCase
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
                it shouldHaveType it.typeNode.typeMirror

                argList {
                    methodCall("getMessage") {
                        it shouldHaveType it.typeSystem.STRING
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
                it shouldHaveType it.typeNode.typeMirror

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
                        it shouldHaveType it.typeNode.typeMirror

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

                it.usesDiamondTypeArgs() shouldBe true

                it.methodType shouldBe it.typeSystem.UNRESOLVED_METHOD
                it.overloadSelectionInfo.isFailed shouldBe true
                it shouldHaveType t_UnresolvedOfString

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

                with(it.typeDsl) {
                    it.methodType.shouldMatchMethod("id", withFormals = listOf(int), returning = void)
                    it.overloadSelectionInfo.isFailed shouldBe false // it succeeded
                    it.methodType.symbol shouldBe idMethod
                }

                argList {
                    fieldAccess("SOME_INT") {
                        it shouldHaveType it.typeSystem.UNKNOWN
                        typeExpr {
                            classType("Unresolved")
                        }
                    }
                }
            }
        }
    }

    parserTest("Unresolved types are compatible in type variable bounds") {

        val acu = parser.parse(
                """

class C {

    static ooo.Foo foo() { return null; }
    static <T extends ooo.Bound> T id(T t) { return t; }

    static {
        // Creates bounds: `T >: ooo.Foo` and `T <: ooo.Bound`
        // Should not be deemed incompatible
        id(foo()); 
    }
}

                """.trimIndent()
        )


        val (fooM, idM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.symbol }

        val t_Foo = fooM.getReturnType(Substitution.EMPTY).shouldBeUnresolvedClass("ooo.Foo")
        val t_Bound = idM.typeParameters[0].upperBound.shouldBeUnresolvedClass("ooo.Bound")

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("id") {

                it.methodType.shouldMatchMethod("id", withFormals = listOf(t_Foo), returning = t_Foo)
                it.overloadSelectionInfo.isFailed shouldBe false // it succeeded
                it.methodType.symbol shouldBe idM


                argList(1)
            }
        }
    }

    parserTest("Unresolved types are used in overload specificity tests") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

class C {

    static void foo(U1 u) { }
    static void foo(U2 u) { }

    static {
        U1 u = null;
        foo(u);
    }
}

                """.trimIndent()
        )


        val (foo1) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.symbol }
        val (t_U1, t_U2) = acu.descendants(ASTClassOrInterfaceType::class.java).toList { it.typeMirror }

        t_U1.shouldBeUnresolvedClass("U1")
        t_U2.shouldBeUnresolvedClass("U2")

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        spy.shouldBeOk {
            call.shouldMatchN {
                methodCall("foo") {

                    it.methodType.shouldMatchMethod("foo", withFormals = listOf(t_U1), returning = it.typeSystem.NO_TYPE)
                    it.overloadSelectionInfo.isFailed shouldBe false // it succeeded
                    it.methodType.symbol shouldBe foo1

                    argList(1)
                }
            }
        }

    }

    parserTest("Superclass type is known in the subclass") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

class C extends U1 {

    static void foo(U1 u) { }
    static void foo(String u) { }

    static {
        foo(this);
    }
}

                """.trimIndent()
        )


        val (foo1) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.symbol }
        val (t_U1) = acu.descendants(ASTClassOrInterfaceType::class.java).toList { it.typeMirror }

        t_U1.shouldBeUnresolvedClass("U1")

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        spy.shouldBeOk {
            call.shouldMatchN {
                methodCall("foo") {

                    it.methodType.shouldMatchMethod("foo", withFormals = listOf(t_U1), returning = it.typeSystem.NO_TYPE)
                    it.overloadSelectionInfo.isFailed shouldBe false // it succeeded
                    it.methodType.symbol shouldBe foo1

                    argList(1)
                }
            }
        }

    }

    parserTest("Recovery when there are several applicable overloads") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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

        val call = acu.firstMethodCall()

        spy.shouldBeAmbiguous(call)
        acu.withTypeDsl {
            call.shouldMatchN {
                methodCall("append") {

                    with(it.typeDsl) {
                        it.methodType.shouldMatchMethod("append", returning = gen.t_StringBuilder)
                        it.overloadSelectionInfo.isFailed shouldBe true // ambiguity
                    }

                    skipQualifier()

                    argList {
                        fieldAccess("SOMETHING") {
                            it shouldHaveType it.typeSystem.UNKNOWN
                            typeExpr {
                                classType("Unresolved")
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Recovery of unknown field using invocation context") {
        // This is ignored as doing this kind of thing would be impossible with
        // laziness. If we want to resolve stuff like that, we have to resolve
        // context first, then push it down into unresolved slots. But standalone
        // exprs are used for disambiguation. So we'd probably have to split getTypeMirror
        // into a top-down only and a user-facing one.

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
import ooo.Unresolved;

class C {

    void foo(int i) {}

    static {
        foo(Unresolved.SOMETHING); // for now, this is unresolved.
    }
}

                """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.shouldMatchN {
                methodCall("foo") {

                    with(it.typeDsl) {
                        it.methodType.shouldMatchMethod("foo", withFormals = listOf(int), returning = void)
                        it.overloadSelectionInfo.isFailed shouldBe false
                    }

                    argList {
                        fieldAccess("SOMETHING") {
                            with(it.typeDsl) {
                                it shouldHaveType ts.UNKNOWN
                                it.referencedSym shouldBe null
                            }
                            typeExpr {
                                classType("Unresolved")
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Recovery of unknown field/var using assignment context") {

        val acu = parser.parse(
                """
import ooo.Unresolved;

class C {

    void foo(int i) {}

    static {
        int i = Unresolved.SOMETHING;
        String k = SOMETHING;
    }
}

                """.trimIndent()
        )

        val (field, unqual) = acu.descendants(ASTAssignableExpr.ASTNamedReferenceExpr::class.java).toList()

        field.shouldMatchN {
            fieldAccess("SOMETHING") {
                with(it.typeDsl) {
                    it shouldHaveType int
                    it.referencedSym shouldBe null
                }
                typeExpr {
                    classType("Unresolved")
                }
            }
        }
        unqual.shouldMatchN {
            variableAccess("SOMETHING") {
                with(it.typeDsl) {
                    it shouldHaveType ts.STRING
                    it.referencedSym shouldBe null
                }
            }
        }
    }

    parserTest("Unresolved type in primitive switch label") {

        val acu = parser.parse(
                """
import ooo.Opcodes.*;

class C {
    void foo(int i) {
        switch (i) {
        case A: break;
        case B: break;
        }
    }
}

                """.trimIndent()
        )

        val (_, a, b) = acu.descendants(ASTVariableAccess::class.java).toList()

        a.shouldMatchN {
            variableAccess("A") {
                with(it.typeDsl) {
                    it shouldHaveType int
                    it.referencedSym shouldBe null
                }
            }
        }
        b.shouldMatchN {
            variableAccess("B") {
                with(it.typeDsl) {
                    it shouldHaveType int
                    it.referencedSym shouldBe null
                }
            }
        }
    }

    parserTest("Unresolved lambda/mref target type has non-null functional method") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
                class Foo {

                    void foo(UnresolvedLambdaTarget lambda) { }

                    void bar() {
                        foo(() -> null); // the target type is unresolved
                        foo(this::foo);  // same
                    }

                }
                """.trimIndent()
        )

        val (lambda) = acu.descendants(ASTLambdaExpression::class.java).toList()
        val (mref) = acu.descendants(ASTMethodReference::class.java).toList()

        val (lambdaCall, mrefCall) = acu.descendants(ASTMethodCall::class.java).toList()

        spy.shouldHaveNoApplicableMethods(lambdaCall)
        spy.shouldHaveNoApplicableMethods(mrefCall)

        acu.withTypeDsl {
            lambda shouldHaveType ts.UNKNOWN
            lambda.functionalMethod shouldBe ts.UNRESOLVED_METHOD

            mref shouldHaveType ts.UNKNOWN
            mref.functionalMethod shouldBe ts.UNRESOLVED_METHOD
            mref.referencedMethod shouldBe ts.UNRESOLVED_METHOD
        }
    }

    parserTest("No context for lambda/mref") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
                class Foo {

                    void foo(UnresolvedLambdaTarget lambda) { }

                    void bar() {
                        () -> null;
                        return this::foo; // return onto void
                    }

                }
                """.trimIndent()
        )

        val (lambda) = acu.descendants(ASTLambdaExpression::class.java).toList()
        val (mref) = acu.descendants(ASTMethodReference::class.java).toList()

        spy.shouldHaveNoLambdaCtx(lambda)
        spy.shouldHaveNoLambdaCtx(mref)

        acu.withTypeDsl {
            lambda shouldHaveType ts.UNKNOWN
            lambda.functionalMethod shouldBe ts.UNRESOLVED_METHOD

            mref shouldHaveType ts.UNKNOWN
            mref.functionalMethod shouldBe ts.UNRESOLVED_METHOD
            mref.referencedMethod shouldBe ts.UNRESOLVED_METHOD
        }
    }


    parserTest("Wrong syntax, return with expr in void method") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
                class Foo {
                    void foo() { return foo; }
                    static { return p1; }
                    Foo() { return p2; }
                }
        """)

        for (vaccess in acu.descendants(ASTVariableAccess::class.java)) {
            spy.shouldBeOk {
                vaccess shouldHaveType ts.UNKNOWN
            }
        }
    }

    parserTest("Lambda with wrong form") {

        val (acu, _) = parser.parseWithTypeInferenceSpy("""
                interface Lambda {
                    void call();
                }
                class Foo {
                    {
                        Lambda l = () -> {}; // ok
                        Lambda l = x -> {};  // wrong form!
                    }
                }
        """)

        val (ok, wrong) = acu.descendants(ASTLambdaExpression::class.java).toList()
        val t_Lambda = acu.typeDeclarations.firstOrThrow().typeMirror

        acu.withTypeDsl {
            ok shouldHaveType t_Lambda
            wrong shouldHaveType t_Lambda
            wrong.parameters[0] shouldHaveType ts.UNKNOWN
        }
    }

})
