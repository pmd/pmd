/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractFunSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.typeresolution.testdata.GenericFbound
import net.sourceforge.pmd.typeresolution.testdata.MutualTypeRecursion

/**
 * @author Clément Fournier
 */
class TypeParamSubstTest : AbstractFunSpec({

    val ts = testTypeSystem
    with(TypeDslOf(ts)) {

        test("Test type param building on F-bound") {

            GenericFbound::class.decl.shouldBeA<JClassType> {

                it::isRaw shouldBe false
                it::isGenericTypeDeclaration shouldBe true
                it::getTypeArgs shouldBe it.formalTypeParams

                it.formalTypeParams.apply {
                    this[0].shouldBeA<JTypeVar> { tp ->
                        tp::isCaptured shouldBe false
                        tp.upperBound.shouldBeA<JClassType> {
                            it::isRaw shouldBe false
                            it::isInterface shouldBe false
                            it::getSuperClass shouldBe ts.OBJECT

                            it.formalTypeParams[0] shouldBe tp
                            it.typeArgs[0] shouldBe tp
                        }
                    }
                }
            }
        }

        test("Test type param building on mutually recursive type") {

            MutualTypeRecursion::class.decl.shouldBeA<JClassType> {

                it.isRaw shouldBe false
                it.isGenericTypeDeclaration shouldBe true
                it.typeArgs shouldBe it.formalTypeParams

                it.formalTypeParams.also { formals ->
                    formals[0].shouldBeA<JTypeVar> { tp ->
                        tp.name shouldBe "T"
                        tp.isCaptured shouldBe false
                        tp.upperBound.shouldBeA<JClassType> {
                            it.isRaw shouldBe false
                            it.isInterface shouldBe false
                            it.superClass shouldBe ts.OBJECT

                            it.formalTypeParams shouldBe formals
                            it.typeArgs shouldBe listOf(formals[0], formals[1])
                        }
                    }
                    formals[1].shouldBeA<JTypeVar> { tp ->
                        tp.name shouldBe "S"
                        tp.isCaptured shouldBe false
                        tp.upperBound.shouldBeA<JClassType> {
                            it.isRaw shouldBe false
                            it.isInterface shouldBe false
                            it.superClass shouldBe ts.OBJECT

                            it.formalTypeParams shouldBe formals
                            it.typeArgs shouldBe listOf(formals[1], formals[0])
                        }
                    }
                }
            }
        }

        test("Test generic superclass") {

            GenericFbound.Inst::class.decl.shouldBeA<JClassType> { instclass ->

                instclass.superClass!!.also {
                    it.isRaw shouldBe false
                    it.typeArgs[0].shouldBe(instclass)
                    it.isGenericTypeDeclaration shouldBe false
                }
            }

            GenericFbound.InstRaw::class.decl.shouldBeA<JClassType> { instclass ->

                instclass.superClass!!.also {
                    it.isRaw shouldBe true
                    it.typeArgs shouldBe emptyList()
                    it.isGenericTypeDeclaration shouldBe false
                }
            }
        }

        test("Test type var scoping") {

            GenericFbound.InstRec::class.decl.shouldBeA<JClassType> { instclass ->

                instclass.superClass!!.also {
                    it.isRaw shouldBe false
                    it.typeArgs[0] shouldBe GenericFbound::class.decl.formalTypeParams[0]
                    it.isGenericTypeDeclaration shouldBe false
                }
            }
        }
    }

})
