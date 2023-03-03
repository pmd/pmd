/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.FakeSymAnnot
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside

/**
 */
class TypeSystemTest : IntelliMarker, FunSpec({

    val ts = testTypeSystem

    test("Test primitive types are reused") {

        listOf(true, false).forEach { isErased ->
            ts.typeOf(ts.getClassSymbol(Integer.TYPE), isErased).shouldBeSameInstanceAs(ts.INT)
            ts.typeOf(ts.getClassSymbol(Character.TYPE), isErased).shouldBeSameInstanceAs(ts.CHAR)
            ts.typeOf(ts.getClassSymbol(Void.TYPE), isErased).shouldBeSameInstanceAs(ts.NO_TYPE)
            ts.typeOf(ts.getClassSymbol(Void::class.java), isErased).shouldBeSameInstanceAs(ts.BOXED_VOID)
            ts.typeOf(ts.getClassSymbol(Object::class.java), isErased).shouldBeSameInstanceAs(ts.OBJECT)
        }
    }

    test("Test special symbols are reused") {
        ts.getClassSymbol(Integer.TYPE) shouldBeSameInstanceAs ts.INT.symbol
        ts.getClassSymbol(Character.TYPE) shouldBeSameInstanceAs ts.CHAR.symbol
        ts.getClassSymbol(Void.TYPE) shouldBeSameInstanceAs ts.NO_TYPE.symbol
        ts.getClassSymbol(Void::class.java) shouldBeSameInstanceAs ts.BOXED_VOID.symbol
        ts.getClassSymbol(Object::class.java) shouldBeSameInstanceAs ts.OBJECT.symbol
    }

    test("Test getClassSymbol null -> null") {
        ts.getClassSymbol(null as Class<*>?) shouldBe null
        ts.getClassSymbol(null as String?) shouldBe null
        ts.getClassSymbolFromCanonicalName(null) shouldBe null
    }

    test("Test typeOf null -> null") {
        ts.typeOf(null, false) shouldBe null
        ts.typeOf(null, true) shouldBe null
    }


    test("Test typeOf array") {
        val arraySym = ts.getClassSymbol(Cloneable::class.java.arrayType)!!.also {
            it::isArray shouldBe true
            it::getArrayComponent shouldBe ts.getClassSymbol(Cloneable::class.java)
        }
        val arrayT = ts.typeOf(arraySym, false)
        withClue("erased should be the same as not erased") {
            arrayT shouldBe ts.typeOf(arraySym, true)
        }
        arrayT.shouldBeA<JArrayType> {
            it.symbol shouldBeSameInstanceAs arraySym
            it.componentType.shouldBeA<JClassType> { it.symbol shouldBe ts.getClassSymbol(Cloneable::class.java) }
        }
    }

    test("Test typeOf type var") {
        val (tvar) = ParserTestCtx(this).makeDummyTVars("T")
        val type = ts.typeOf(tvar.symbol, false)
        withClue("erased should be the same as not erased") {
            type shouldBe ts.typeOf(tvar.symbol, true)
        }
        type shouldBeSameInstanceAs tvar
    }

    test("Test typeOf array of type var") {
        val (tvar) = ParserTestCtx(this).makeDummyTVars("T")
        val type = ts.arrayType(ts.typeOf(tvar.symbol, false))
        type.shouldBeA<JArrayType> {
            it.componentType shouldBeSameInstanceAs tvar
        }
        type.symbol.shouldBeA<JClassSymbol> {
            it.arrayComponent shouldBeSameInstanceAs tvar.symbol
        }

        // rebuild type from its symbol
        ts.typeOf(type.symbol, false).let {
            it shouldBe type
            it.symbol shouldBe type.symbol
        }

        // rebuild with isErased=true
        ts.typeOf(type.symbol, true).let {
            it shouldBe type
            it.symbol shouldBe type.symbol
        }
    }

    test("Test getClassSymbol from string") {
        ts.getClassSymbol("int") shouldBeSameInstanceAs ts.INT.symbol
        for (primitive in ts.allPrimitives) {
            ts.getClassSymbol(primitive.simpleName) shouldBeSameInstanceAs primitive.symbol
        }
        ts.getClassSymbol("void") shouldBeSameInstanceAs ts.NO_TYPE.symbol
        ts.getClassSymbol("java.lang.Object") shouldBeSameInstanceAs ts.OBJECT.symbol
        shouldThrow<IllegalArgumentException> {
            ts.getClassSymbol("java.lang.Object[]") // does not accept arrays
        }
        shouldThrow<IllegalArgumentException> {
            ts.getClassSymbol("java.lang. Object") // does not accept spaces
        }
    }

    test("Test parameterize special types") {
        ts.parameterise(ts.INT.symbol, emptyList()) shouldBeSameInstanceAs ts.INT
        shouldThrow<java.lang.IllegalArgumentException> {
            ts.parameterise(ts.INT.symbol, listOf(ts.INT.box()))
        }

        ts.parameterise(ts.OBJECT.symbol, emptyList()) shouldBeSameInstanceAs ts.OBJECT
    }

    test("Test isTop") {
        ts.INT::isTop shouldBe false
        ts.OBJECT::isTop shouldBe true
        ts.STRING::isTop shouldBe false

        val otherTypeNamedObject = javaParser.parseSomeClass("class Object { }")
        otherTypeNamedObject::isTop shouldBe false
        val javaLangObjectDeclaration = javaParser.parseSomeClass("package java.lang; class Object { }")
        javaLangObjectDeclaration::isTop shouldBe true
    }

    test("Test isTop on annotated Object") {
        val annotated =
            ts.OBJECT.addAnnotation(FakeSymAnnot(ts.getClassSymbol(ClassWithTypeAnnotationsInside.A::class.java)))
        ts.OBJECT shouldNotBeSameInstanceAs annotated
        annotated.typeAnnotations shouldNot beEmpty()
        annotated::isTop shouldBe true
    }

    test("Test isTop on Object source declaration") {
        val javaLangObjectDeclaration = javaParser.parseSomeClass("package java.lang; class Object { }")
        javaLangObjectDeclaration::isTop shouldBe true
    }

    test("Test parameterize generic class") {
        val listSym = ts.getClassSymbol(List::class.java)!!
        val parameterized = ts.parameterise(listSym, listOf(ts.INT.box()))
        parameterized shouldBe (ts.rawType(listSym) as JClassType).withTypeArguments(listOf(ts.INT.box()))
    }

    test("Test array type recursive") {

        for (t in listOf(ts.INT, ts.OBJECT, ts.CLONEABLE)) {
            shouldThrow<java.lang.IllegalArgumentException> {
                ts.arrayType(t, -1)
            }
            ts.arrayType(t, 0) shouldBeSameInstanceAs t
            ts.arrayType(t) shouldBe ts.arrayType(t, 1)
            ts.arrayType(t, 2) shouldBe ts.arrayType(ts.arrayType(t))
            ts.arrayType(ts.arrayType(t), 2) shouldBe ts.arrayType(t, 3)
        }
    }

    test("Test malformed wildcard bound") {

        ts.wildcard(true, ts.OBJECT) shouldBe ts.UNBOUNDED_WILD
        ts.wildcard(false, ts.CLONEABLE).shouldBeA<JWildcardType> {
            it::getSymbol shouldBe null
            it::isLowerBound shouldBe true
            it::isUpperBound shouldBe false
            it::isUnbounded shouldBe false
            it.asLowerBound() shouldBe ts.CLONEABLE
            it.asUpperBound() shouldBe ts.OBJECT
        }

        shouldThrow<java.lang.IllegalArgumentException> {
            ts.wildcard(false, ts.UNBOUNDED_WILD)
        }
        shouldThrow<java.lang.IllegalArgumentException> {
            ts.wildcard(false, ts.INT)
        }
    }

    test("Test init succeeds") {

        ts.OBJECT shouldNotBe null
        ts.NULL_TYPE shouldNotBe null
        ts.UNBOUNDED_WILD shouldNotBe null

        ts.ERROR shouldNotBe null
        ts.UNKNOWN shouldNotBe null
        ts.NO_TYPE shouldNotBe null

    }

    test("Test specified special symbols") {

        ts.NULL_TYPE.symbol shouldBe null
        ts.NULL_TYPE.isBottom shouldBe true

        ts.OBJECT shouldNotBe null
        ts.NULL_TYPE shouldNotBe null
        ts.UNBOUNDED_WILD shouldNotBe null

        ts.ERROR.symbol.shouldBeA<JClassSymbol>()
        ts.UNKNOWN.symbol.shouldBeA<JClassSymbol>()
        ts.NO_TYPE.symbol!! shouldBeSameInstanceAs ts.getClassSymbol(Void.TYPE)

    }
})


