/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*

/**
 * @author Clément Fournier
 */
class CtorInferenceTest : ProcessorTestSpec({


    parserTest("Results of diamond invoc and parameterized invoc are identical (normal classes)") {

        val acu = parser.parse(
                """
            class Gen<T> {

                static {

                    Gen<String> g = new Gen<String>(); 
                    g = new Gen<>(); 
                }
            }
            """)

        val (t_Gen) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val (paramCall, genCall) = acu.descendants(ASTConstructorCall::class.java).toList()

        with(acu.typeDsl) {

            listOf(paramCall, genCall).forAll { call ->

                call.methodType.shouldMatchMethod(
                        named = JConstructorSymbol.CTOR_NAME,
                        declaredIn = t_Gen[gen.t_String],
                        withFormals = emptyList(),
                        returning = t_Gen[gen.t_String]
                ).also {
                    it.typeParameters shouldBe emptyList()
                    it.isGeneric shouldBe false
                }
            }
        }
    }

    parserTest("Enum constant ctors") {

        val acu = parser.parse(
                """

            import java.util.function.Function;
            enum E {
                A,
                B(),
                C(1),
                D(1.0),
                ;

                E(int i) {}
                E(double c) {}
                E() {}
            }
            """)

        val (t_E) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
        val (intCtor, doubleCtor, defaultCtor) = acu.descendants(ASTConstructorDeclaration::class.java).toList { it.symbol }

        val (a, b, c, d) = acu.descendants(ASTEnumConstant::class.java).toList()

        with(acu.typeDsl) {

            listOf(a, b).forAll {
                it.methodType.symbol shouldBe defaultCtor
            }

            c.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_E,
                    withFormals = listOf(int),
                    returning = t_E
            ).also { it.symbol shouldBe intCtor }

            d.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_E,
                    withFormals = listOf(double),
                    returning = t_E
            ).also { it.symbol shouldBe doubleCtor }

        }
    }


    parserTest("Generic enum constant ctors") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

            import java.util.function.Function;
            enum E {
                A(1.0, i -> i + 1),
                ;

                E() {}
                E(double c, double k) {}
                <T> E(T c, Function<? super T, ? extends T> fun) {}
            }
            """)

        val (t_E) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
        val (_, _, genericCtor) = acu.descendants(ASTConstructorDeclaration::class.java).toList { it.symbol }

        val (a) = acu.descendants(ASTEnumConstant::class.java).toList()

        spy.shouldBeOk {

            a.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_E,
                    withFormals = listOf(double.box(), gen.t_Function[`?` `super` double.box(), `?` extends double.box()]),
                    returning = t_E
            ).also { it.symbol shouldBe genericCtor }
        }

    }

    parserTest("Anonymous enum ctor") {

        val acu = parser.parse(
                """

            import java.util.function.Function;
            enum E {
                A { }
            }
            """)

        val (t_E) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val (a) = acu.descendants(ASTEnumConstant::class.java).toList()


        a.methodType.shouldMatchMethod(
                named = JConstructorSymbol.CTOR_NAME,
                declaredIn = t_E,
                withFormals = emptyList(),
                returning = t_E // not the anonymous type
        )

    }
    parserTest("Generic superclass ctor") {

        val acu = parser.parse(
                """

            class Sup<T> {
                public Sup(T referent, String cleaner) { }
            }

            class Sub extends Sup<String> {
                Sub(String s) {
                    super(s, s);
                }
            }

            """)

        val (t_Sup) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val (supCtor) = acu.descendants(ASTConstructorDeclaration::class.java).toList()
        val (ctor) = acu.descendants(ASTExplicitConstructorInvocation::class.java).toList()


        with (ctor.typeDsl) {
            ctor.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_Sup[ts.STRING],
                    withFormals = listOf(ts.STRING, ts.STRING),
                    returning = t_Sup[ts.STRING] // the superclass type
            ).also {
                it.symbol shouldBe supCtor.symbol
                it.symbol.tryGetNode() shouldBe supCtor
            }
        }

    }

    parserTest("Qualified superclass ctor") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """


            class Outer {
                class Inner<T> {
                    public Inner(T value) { }
                }
            }


            class Scratch extends Outer.Inner<String> {
            
                public Scratch(Outer o) {
                    o.super("value");
                }
            }

            """)

        val (t_Outer, t_Inner, t_Scratch) = acu.declaredTypeSignatures()

        val (innerCtor) = acu.ctorDeclarations().toList()
        val (ctor) = acu.descendants(ASTExplicitConstructorInvocation::class.java).toList()

        spy.shouldBeOk {
            ctor.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_Outer select t_Inner[ts.STRING],
                    withFormals = listOf(ts.STRING),
                    returning = t_Outer select t_Inner[ts.STRING]
            )

            ctor.methodType.let {
                it.symbol shouldBe innerCtor.symbol
                it.symbol.tryGetNode() shouldBe innerCtor
            }
        }
    }

    parserTest("Qualified generic superclass ctor") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """


            class Outer<O> {
                class Inner {
                    public Inner(O value) { }
                }
            }


            class Scratch extends Outer<Integer>.Inner {
                // TODO make a test where the super is the raw type Outer.Inner
                // then the explicit ctor invoc may be different

                public Scratch() {
                    new Outer<Integer>().super(4);
                }
            }

            """)


        val (t_Outer, t_Inner, t_Scratch) = acu.declaredTypeSignatures()

        val (innerCtor) = acu.ctorDeclarations().toList()
        val (ctor) = acu.descendants(ASTExplicitConstructorInvocation::class.java).toList()

        spy.shouldBeOk {
            ctor.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_Outer[ts.INT.box()] select t_Inner,
                    withFormals = listOf(ts.INT.box()),
                    returning = t_Outer[ts.INT.box()] select t_Inner
            )

            ctor.methodType.let {
                it.symbol shouldBe innerCtor.symbol
                it.symbol.tryGetNode() shouldBe innerCtor
            }
        }
    }

    parserTest("Unresolved enclosing type for inner class ctor") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
            class Scratch  {{
                    someUnresolvedQualifier().new Inner();
            }}
            """)

        val (ctor) = acu.ctorCalls().toList()

        spy.shouldBeOk {
            ctor.qualifier!!.shouldHaveType(ts.UNKNOWN)
            ctor.typeNode.simpleName shouldBe "Inner"
            ctor.typeNode.typeMirror.shouldBeSameInstanceAs(ctor.typeMirror)
            ctor.typeNode shouldHaveType ts.UNKNOWN
            // if we ever switch to creating a fake symbol
            // .typeMirror.shouldBeA<JClassType> {
            //     it.symbol.isUnresolved shouldBe true
            //     it.symbol.simpleName shouldBe "Inner"
            // }

            ctor.methodType.shouldBe(ts.UNRESOLVED_METHOD)
        }
    }

    parserTest("Failed overload resolution of context doesn't let types dangle") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

                public class Generic<T> {
                    static class Inner<K> {}
                    <E> Generic<E> method(Generic<E> e) { return e; }
                    public Generic<T> test() {
                        return method(new Generic.Inner<T>());
                    }
                }
            """
        )

        val (_, inner) = acu.declaredTypeSignatures()
        val enclosingMethodCall = acu.firstMethodCall()
        val ctorCall = acu.firstCtorCall()
        val ctorSymbol = inner.constructors.first().symbol

        spy.shouldHaveMissingCtDecl(enclosingMethodCall)

        ctorCall.withTypeDsl { // for the enclosing method call
            ctorCall.methodType.symbol shouldBe ctorSymbol
        }
    }

    parserTest("Mapping of type params doesn't fail") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

    class Gen<A,B> {
      <E> Gen(Class<E> c, E inst) {}
      static Gen<Integer, String> field;
      static {
        // This used to fail because the adapted ctor has type params <E,A,B>
        // and we were calling for a substitution mapping <E> to <E,A,B>.
        field = new Gen<>(int.class, 2);
      }
    }
            """
        )

        val (t_Gen) = acu.declaredTypeSignatures()
        val ctorCall = acu.firstCtorCall()
        val ctorSymbol = t_Gen.constructors.first().symbol

        ctorCall.withTypeDsl { // for the enclosing method call
            ctorCall.methodType.symbol shouldBe ctorSymbol
        }
    }

})
