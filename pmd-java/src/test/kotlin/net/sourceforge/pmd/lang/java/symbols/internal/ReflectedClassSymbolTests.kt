/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.testTypeSystem

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ReflectedClassSymbolTests : IntelliMarker, WordSpec({

    "A class symbol" should {

        "reflect its superclass correctly" {
            TestClassesGen.forAllEqual {
                if (it.isInterface)
                    classSym(it)!!.superclass to testTypeSystem.OBJECT.symbol
                else
                    classSym(it)!!.superclass to classSym(it.superclass)
            }
        }

        "reflect its type parameters correctly" {
            TestClassesGen.checkAll { clazz ->
                val classSym = classSym(clazz)!!
                classSym.typeParameters.map { it!!.name } shouldBe clazz.typeParameters.toList().map { it.name }
                classSym.typeParameters.forEach {
                    it!!.symbol!!.declaringSymbol shouldBe classSym
                }
            }
        }

        "reflect its superinterfaces correctly" {
            TestClassesGen.forAllEqual {
                classSym(it)!!.superInterfaces to it.interfaces.toList().map { classSym(it) }
            }
        }

        "reflect its simple name properly" {
            TestClassesGen.forAllEqual {
                classSym(it)!!.simpleName to it.simpleName
            }
        }

        "reflect its canonical name properly" {
            TestClassesGen.forAllEqual {
                classSym(it)!!.canonicalName to it.canonicalName
            }
        }

        "reflect its modifiers properly" {
            TestClassesGen.forAllEqual {
                classSym(it)!!.modifiers to it.modifiers
            }
        }

        "reflect its component type when it is a primitive array" {

            val iarr = classSym(IntArray::class.java)!!
            iarr::isArray shouldBe true
            iarr::isInterface shouldBe false
            iarr::getArrayComponent shouldBe testTypeSystem.getClassSymbol(Integer.TYPE)
        }

        "reflect its component type when it is a reference array" {

            val arr = classSym(Array<String>::class.java)!!
            arr::isArray shouldBe true
            arr::isInterface shouldBe false
            arr::getArrayComponent shouldBe classSym(String::class.java)
        }
    }

})
