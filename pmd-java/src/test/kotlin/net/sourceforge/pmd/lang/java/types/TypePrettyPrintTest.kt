/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint.TypePrettyPrinter

/**
 * @author Clément Fournier
 */
class TypePrettyPrintTest : FunSpec({

    val ts = testTypeSystem

    test("Test toString") {

        ts.arrayType(ts.INT).apply {
            toString() shouldBe "int[]"
        }
    }

    test("wildcards") {

        ts.wildcard(true, ts.OBJECT).apply {
            toString() shouldBe "?"
        }

        ts.wildcard(true, ts.CLONEABLE).apply {
            toString() shouldBe "? extends java.lang.Cloneable"
        }

        ts.wildcard(false, ts.CLONEABLE).apply {
            toString() shouldBe "? super java.lang.Cloneable"
        }
    }

    test("pretty print with simple names") {

        fun JTypeMirror.pp() = TypePrettyPrint.prettyPrintWithSimpleNames(this)

        with(TypeDslOf(ts)) {
            ts.OBJECT.pp() shouldBe "Object"
            ts.OBJECT.toArray().pp() shouldBe "Object[]"
            Map::class[List::class[`?`], ts.INT.box()].pp() shouldBe "Map<List<?>, Integer>"
        }
    }

    test("pretty print with tvar qualifiers") {

        val acu = ParserTestCtx(this).parser.withProcessing(true).parse("""
            package p;
            class Foo<A,B> {
                <T> T method(A a, B b) {}
            }
        """.trimIndent()
        )


        fun JTypeVisitable.pp() = TypePrettyPrint.prettyPrint(this, TypePrettyPrinter().qualifyTvars(true))

        acu.declaredMethodSignatures()[0].pp() shouldBe "p.Foo<Foo#A, Foo#B>.<method#T> method(Foo#A, Foo#B) -> method#T"

    }

    test("pretty print with no method header") {

        val acu = ParserTestCtx(this).parser.withProcessing(true).parse("""
            package p;
            class Foo<A,B> {
                <T> T method(A a, B b) {}
            }
        """.trimIndent()
        )


        fun JMethodSig.pp() = TypePrettyPrint.prettyPrint(this, TypePrettyPrinter().printMethodHeader(false))

        acu.declaredMethodSignatures()[0].pp() shouldBe "method(A, B) -> T"

    }
})


