/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.arbitrary.filterNot
import javasymbols.testdata.impls.IdenticalToSomeFields
import javasymbols.testdata.impls.SomeFields
import net.sourceforge.pmd.lang.ast.test.IntelliMarker

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ReflectedFieldSymbolTest : IntelliMarker, WordSpec({

    "A field symbol" should {

        "reflect its name properly" {
            classSym(SomeFields::class.java)!!.getDeclaredField("a")!!.simpleName shouldBe "a"
            classSym(SomeFields::class.java)!!.getDeclaredField("bb")!!.simpleName shouldBe "bb"
        }

        "be equal to itself" {
            TestClassesGen.forAllEqual {
                Pair(
                        // note: different class symbol instances
                        classSym(it)!!.declaredFields,
                        classSym(it)!!.declaredFields
                )
            }
        }

        "reflect its modifiers properly" {
            TestClassesGen.filterNot { it.isArray }.forAllEqual {
                Pair(
                        classSym(it)!!.declaredFields.map { it.simpleName to it.modifiers },
                        it.declaredFields.map { it.name to it.modifiers }
                )
            }
        }


        "not be equal to fields declared in other classes" {
            classSym(SomeFields::class.java)!!.getDeclaredField("a")!! shouldNotBe classSym(IdenticalToSomeFields::class.java)!!.getDeclaredField("a")!!
            classSym(SomeFields::class.java)!!.getDeclaredField("bb")!! shouldNotBe classSym(IdenticalToSomeFields::class.java)!!.getDeclaredField("bb")!!
        }

        "reflect its annotations" {
            classSym(SomeFields::class.java)!!.getDeclaredField("foo")!!.isAnnotationPresent(java.lang.Deprecated::class.java) shouldBe true
        }

        "be unmodifiable" {
            shouldThrow<UnsupportedOperationException> {
                classSym(SomeFields::class.java)!!.getDeclaredField("foo")!!.declaredAnnotations.add(null)
            }
        }
    }

})
