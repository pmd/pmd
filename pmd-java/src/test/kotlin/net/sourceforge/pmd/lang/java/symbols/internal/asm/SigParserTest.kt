/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import javasymbols.testdata.impls.SomeInnerClasses
import net.sourceforge.pmd.lang.test.ast.IntelliMarker
import net.sourceforge.pmd.lang.test.ast.shouldBe
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.asm.GenericSigBase.LazyMethodType
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

fun mockLexicalScope(vararg names: String): LexicalScope =
    with(testTypeSystem) {
        LexicalScope.EMPTY.andThen(names.map { mockTypeVar(name = it) })
    }

operator fun LexicalScope.get(name: String): SubstVar = apply(name)!!

fun LexicalScope.shouldParseType(sig: String, test: TypeDslOf.() -> JTypeMirror) {
    val ts = testTypeSystem
    val parsed = ts.asmLoader.sigParser.parseFieldType(this, sig)

    parsed shouldBe TypeDslOf(ts).test()
}
private fun LexicalScope.shouldThrowWhenParsingType(sig: String, matcher: (InvalidTypeSignatureException)->Unit={}) {
    val ex = shouldThrow<InvalidTypeSignatureException> {
        val ts = testTypeSystem
        ts.asmLoader.sigParser.parseFieldType(this, sig)
    }
    matcher(ex)
}

private fun LexicalScope.shouldParseMethod(
    descriptor: String,
    genericSig: String? = descriptor,
    exceptions: Array<String>? = null,
    skipFirstParam: Boolean = false,
    test: TypeDslOf.(LazyMethodType) -> Unit
) {
    val ts = testTypeSystem
    val mockStub = mock(ExecutableStub::class.java)
    `when`(mockStub.sigParser()).thenReturn(ts.asmLoader.sigParser)
    `when`(mockStub.enclosingTypeParameterOwner).thenReturn(null)
    `when`(mockStub.typeSystem).thenReturn(ts)

    val mt = LazyMethodType(mockStub, descriptor, genericSig, exceptions, skipFirstParam)
    withClue("Parsing descriptor") {
        mt.ensureParsed()
    }

    TypeDslOf(ts).test(mt)
}

class SigParserTest : IntelliMarker, FunSpec({

    test("Test type sig parsing with type vars") {

        val scope = mockLexicalScope("T", "V")
        with(scope) {

            shouldParseType("Ljava/util/Map<TT;Ljava/util/List<TV;>;>;") {
                java.util.Map::class[scope["T"], java.util.List::class[scope["V"]]]
            }
        }
    }

    test("Test wildcards") {

        val scope = mockLexicalScope("T", "V")
        scope.shouldParseType("Ljava/util/Map<TT;Ljava/util/List<TV;>;>;") {
            java.util.Map::class[scope["T"], java.util.List::class[scope["V"]]]
        }
        scope.shouldParseType("Ljava/util/Map<-Ljava/lang/Number;+[Ljava/lang/Object;>;") {
            java.util.Map::class[
                    `?` `super` java.lang.Number::class,
                    `?` extends ts.OBJECT.toArray()]
        }
    }

    test("Test method sig") {

        with(LexicalScope.EMPTY) {

            shouldParseMethod(descriptor = "(I)Z") {
                it::getTypeParams shouldBe emptyList()
                it::getExceptionTypes shouldBe emptyList()
                it::getReturnType shouldBe boolean
                it::getParameterTypes shouldBe listOf(int)
            }

            shouldParseMethod(descriptor = "()V") {
                it::getTypeParams shouldBe emptyList()
                it::getExceptionTypes shouldBe emptyList()
                it::getReturnType shouldBe void
                it::getParameterTypes shouldBe emptyList()
            }

        }
    }

    test("Test generic method") {

        with(LexicalScope.EMPTY) {
            shouldParseMethod(descriptor = "<T:>([TT;)TT;") {
                it.typeParams.shouldHaveSize(1)
                val (t) = it.typeParams
                t::getUpperBound shouldBe ts.OBJECT
                it::getExceptionTypes shouldBe emptyList()
                it::getReturnType shouldBe t
                it::getParameterTypes shouldBe listOf(t.toArray())
            }
        }
    }

    test("Test multiple type bounds") {

        with(LexicalScope.EMPTY) {
            shouldParseMethod(descriptor = "<T:Ljava/lang/CharSequence;:Ljava/lang/Number;>()V") {
                it.typeParams.shouldHaveSize(1)
                val (t) = it.typeParams
                t::getUpperBound shouldBe ts.glb(java.lang.Number::class.raw, java.lang.CharSequence::class.raw)
            }
        }
    }

    test("Test inner type") {

        with(LexicalScope.EMPTY) {
            shouldParseType("Ljavasymbols/testdata/impls/SomeInnerClasses<Ljava/lang/String;>.Inner<Ljava/lang/String;>;") {
                SomeInnerClasses::class[ts.STRING]
                    .selectInner(SomeInnerClasses.Inner::class.raw.symbol, listOf(ts.STRING))
            }
        }
    }

    test("Test throws clause") {

        with(LexicalScope.EMPTY) {
            shouldParseMethod(descriptor = "<T:Ljava/lang/Exception;>()V^TT;^Ljava/lang/Error;") {
                it.typeParams.shouldHaveSize(1)
                val (t) = it.typeParams
                it::getExceptionTypes shouldBe listOf(
                    t, java.lang.Error::class.raw
                )
                t::getUpperBound shouldBe java.lang.Exception::class.raw
                it::getReturnType shouldBe void
                it::getParameterTypes shouldBe emptyList()
            }
        }
    }

    test("Test primitives") {

        with(LexicalScope.EMPTY) {
            shouldParseType("Z") { boolean }
            shouldParseType("C") { char }
            shouldParseType("F") { float }
            shouldParseType("B") { byte }
            shouldParseType("S") { short }
            shouldParseType("I") { int }
            shouldParseType("J") { long }
            shouldParseType("D") { double }
        }
    }

    test("Test array types") {

        with(LexicalScope.EMPTY) {

            shouldParseType("[Z") { boolean.toArray() }
            shouldParseType("[I") { int.toArray() }
            shouldParseType("[[[I") { int.toArray(3) }
            shouldParseType("[[Ljava/lang/Object;") { ts.OBJECT.toArray(2) }
            shouldParseType("[[Ljava/util/List<*>;") { java.util.List::class[`?`].toArray(2) }
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


    test("Test invalid sigs") {

        with(LexicalScope.EMPTY) {
            shouldThrowWhenParsingType("Ljava/lang/Object") {
                it.message!!.shouldContain("Expected semicolon")
            }
        }
    }


})

internal class MockTypeParamsScanner(sig: String) : BaseTypeParamsBuilder(sig) {

    val tparams = mutableListOf<Pair<String, String>>()

    override fun addTypeParam(id: String, bound: String) {
        tparams += (id to bound)
    }
}

