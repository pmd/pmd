/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeParamsParser.BaseTypeParamsBuilder
import net.sourceforge.pmd.lang.java.types.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


fun TypeSystem.mockTypeVar(name: String): JTypeVar {
    val sym = mock(JTypeParameterSymbol::class.java).apply {
        `when`(this.simpleName).thenReturn(name)
        `when`(this.computeUpperBound()).thenReturn(OBJECT)
    }

    val t = newTypeVar(sym)

    `when`(sym.typeMirror).thenReturn(t)

    return t
}

fun TypeSystem.mockLexicalScope(vararg names: String): LexicalScope =
        LexicalScope.EMPTY.andThen(names.map { mockTypeVar(name = it) })

operator fun LexicalScope.get(name: String): SubstVar = apply(name)!!

fun TypeSystem.shouldParseType(scope: LexicalScope, sig: String, t: TypeDslOf.() -> JTypeMirror) {
    val parsed = asmLoader.sigParser.parseFieldType(scope, sig)

    parsed shouldBe TypeDslOf(this).t()
}

class SigParserTest : FunSpec({

    test("Test type sig parsing with type vars") {

        with(testTypeSystem) {
            val scope = mockLexicalScope("T", "V")

            shouldParseType(scope, "Ljava/util/Map<TT;Ljava/util/List<TV;>;>;") {
                java.util.Map::class[scope["T"], java.util.List::class[scope["V"]]]
            }
        }
    }

    test("Test wildcards") {

        with(testTypeSystem) {
            val scope = mockLexicalScope("T", "V")

            shouldParseType(scope, "Ljava/util/Map<TT;Ljava/util/List<TV;>;>;") {
                java.util.Map::class[scope["T"], java.util.List::class[scope["V"]]]
            }
        }
    }

    test("Test type params builder") {

        // <T>
        val aMethodSig = "<T:Ljava/lang/Object;>"

        with(MockTypeParamsScanner(aMethodSig)) {
            val e = TypeParamsParser.typeParams(0, this)
            withClue("End offset") {
                e shouldBe aMethodSig.length
            }

            this.tparams shouldBe listOf(
                    "T" to ":Ljava/lang/Object;"
            )
        }
    }

    test("Test more complicated bounds on type param parser") {

        // <T extends Map<? super K, ?>, K>
        val aMethodSig = "<T:Ljava/util/List<-TK;*>;K:>"

        with(MockTypeParamsScanner(aMethodSig)) {
            val e = TypeParamsParser.typeParams(0, this)
            withClue("End offset") {
                e shouldBe aMethodSig.length
            }

            this.tparams shouldBe listOf(
                    "T" to ":Ljava/util/List<-TK;*>;",
                    "K" to ":"
            )
        }
    }

})

internal class MockTypeParamsScanner(sig: String) : BaseTypeParamsBuilder(sig) {

    val tparams = mutableListOf<Pair<String, String>>()

    override fun addTypeParam(id: String, bound: String) {
        tparams += (id to bound)
    }
}

