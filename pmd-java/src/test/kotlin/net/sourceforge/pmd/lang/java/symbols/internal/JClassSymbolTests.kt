package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.inspectors.forAll
import io.kotlintest.matchers.beTheSameInstanceAs
import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.matchers.haveSize
import io.kotlintest.properties.forAll
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import net.sourceforge.pmd.lang.java.symbols.TestClassesGen
import net.sourceforge.pmd.lang.java.symbols.getTypeDeclaration
import net.sourceforge.pmd.lang.java.symbols.internal.impl.JClassSymbolImpl
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.GenericClass
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.IdenticalToSomeFields
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.SomeMethodsNoOverloads
import kotlin.reflect.KClass
import net.sourceforge.pmd.lang.java.symbols.internal.impl.JClassSymbolImpl.create as classSymbol

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class JClassSymbolTests : WordSpec({

    "A class symbol" should {

        "be equal to any class symbol with the same FQCN" {
            forAll(TestClassesGen) { it: Class<*> ->
                val fromAst = classSymbol(it.getTypeDeclaration())
                val fromReflect = classSymbol(it)
                fromAst == fromReflect
            }
        }

        "not be equal to class symbols with a different FQCN" {
            classSymbol(IdenticalToSomeFields.Other::class.java) shouldNotBe classSymbol(SomeMethodsNoOverloads.Other::class.java)
            classSymbol(IdenticalToSomeFields::class.java) shouldNotBe classSymbol(SomeMethodsNoOverloads::class.java)
        }

        fun String.onClass(clazz: KClass<*>, assertions: (JClassSymbolImpl) -> Unit) {

            val fromReflect = classSymbol(clazz.java)
            val fromAst = classSymbol(clazz.java.getTypeDeclaration())

            "$this (on reflected ${clazz.simpleName})" {
                assertions(fromReflect)
            }

            "$this (on parsed ${clazz.simpleName})" {
                assertions(fromAst)
            }
        }


        "be bound to its type parameters".onClass(GenericClass::class) { classSym ->
            classSym.typeParameters should haveSize(2)
            classSym.typeParameters.forAll {
                it.declaringSymbol shouldBe classSym
            }

        }

        "be bound to its nested type symbols".onClass(SomeMethodsNoOverloads::class) { classSym ->
            classSym.declaredClasses should containExactly(classSymbol(SomeMethodsNoOverloads.Other::class.java))

            classSym.declaredClasses[0].enclosingClass should beTheSameInstanceAs(classSym)
        }
    }

})