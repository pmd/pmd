/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger.VerboseLogger
import java.util.*

/**
 */
class TypeAnnotationsInferenceTest : ProcessorTestSpec({

    parserTest("Test type annotation propagate even with boxing") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
import java.lang.annotation.*;
class Foo {
    @Target(ElementType.TYPE_USE)
    @interface A {}

    <T> T genericMethod(T t) {
        return t;
    }

    void someMethod(@A int i) {
        var i2 = genericMethod(i);
    }

    void someMethod(@A String s) {
        String s2 = genericMethod(s);
    }
}

        """.trimIndent()
        )

        val (_, A) = acu.typeDeclarations().toList { it.symbol }


        spy.shouldBeOk {
            val `@A Integer` = `@`(A) on int.box()
            acu.firstMethodCall() shouldHaveType `@A Integer`
            acu.varId("i2") shouldHaveType `@A Integer`

            acu.methodCalls().get(1)!! shouldHaveType (`@`(A) on ts.STRING)
            acu.varId("s2") shouldHaveType ts.STRING
        }
    }

    parserTest("Test type annotations do not break wildcard capture") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
import java.lang.annotation.*;
class Foo {
    @Target(ElementType.TYPE_USE)
    @interface A {}

    interface Predicate<E> { boolean test(E e); }

    <T> boolean someMethod(T t, Predicate<? super @A T> predicate) {
        return predicate.test(t);
    }
}

        """.trimIndent()
        )

        val (_, A, t_Predicate) = acu.typeDeclarations().toList { it.symbol }


        spy.shouldBeOk {
            val `@A` = `@`(A)
            acu.firstMethodCall() shouldHaveType boolean
            acu.firstMethodCall().methodType.shouldMatchMethod(
                named = "test",
                declaredIn = t_Predicate[captureMatcher( `?` `super` (`@A` on acu.typeVar("T")))]
            )
        }
    }


})
