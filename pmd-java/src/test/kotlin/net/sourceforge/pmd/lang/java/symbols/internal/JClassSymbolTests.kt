package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.inspectors.forAll
import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration
import net.sourceforge.pmd.lang.java.symbols.getAst
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.GenericClass
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.IdenticalToSomeFields
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.Overloads
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.SomeMethodsNoOverloads
import kotlin.reflect.KClass

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class JClassSymbolTests : WordSpec({

    "A class symbol" should {
        "be equal to any class symbol with the same FQCN" {
            val fromAst =
                    Overloads::class.java.getAst()
                            .getFirstDescendantOfType(ASTAnyTypeDeclaration::class.java)
                            .let { JClassSymbol(it) }

            val fromReflect = JClassSymbol(Overloads::class.java)

            fromAst shouldBe fromReflect
        }

        "not be equal to class symbols with a different FQCN" {
            JClassSymbol(IdenticalToSomeFields.Other::class.java) shouldNotBe JClassSymbol(SomeMethodsNoOverloads.Other::class.java)
            JClassSymbol(IdenticalToSomeFields::class.java) shouldNotBe JClassSymbol(SomeMethodsNoOverloads::class.java)
        }

        fun String.onClass(clazz: KClass<*>, assertions: (JClassSymbol) -> Unit) {

            val fromReflect = JClassSymbol(clazz.java)
            val fromAst =
                    clazz.java.getAst()
                            .getFirstDescendantOfType(ASTAnyTypeDeclaration::class.java)
                            .let { JClassSymbol(it) }


            "$this (on reflected ${clazz.simpleName})" {
                assertions(fromReflect)
            }

            "$this (on parsed ${clazz.simpleName})" {
                assertions(fromAst)
            }
        }


        "be bound to its type parameters".onClass(GenericClass::class) { classSym ->

            classSym.typeParameters.forAll {
                it.ownerSymbol shouldBe classSym
            }

        }

        "be bound to its nested type symbols".onClass(SomeMethodsNoOverloads::class) { classSym ->
            classSym.declaredClasses should containExactly(JClassSymbol(SomeMethodsNoOverloads.Other::class.java))

            classSym.declaredClasses[0].enclosingClass shouldBe JResolvableClassSymbol(classSym)
        }
    }

})