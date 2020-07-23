/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotlintest.inspectors.forNone
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractFunSpec
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.types.TypeConversion.UncheckedConversion.*
import net.sourceforge.pmd.lang.java.types.testdata.SomeEnum
import kotlin.test.assertEquals

/**
 * @author Clément Fournier
 */
class SubtypingTest : AbstractFunSpec({

    val ts = testTypeSystem
    with(TypeDslOf(ts)) {
        with(gen) {

            test("Test primitive subtyping") {

                typeHierarchyTest(double) {
                    float / {
                        long / {
                            int / {
                                short / {
                                    byte / {}
                                }
                                char / {}
                            }
                        }
                    }
                }

                (ts.allPrimitives.toList() - boolean).forEach {
                    it shouldBeUnrelatedTo boolean
                }
            }

            test("Test primitive arrays are unrelated") {

                val unArrays = ts.allPrimitives.map {
                    ts.arrayType(it, 1)
                }

                unArrays.forEach { arr ->
                    arr shouldBeUnrelatedTo `t_Array{Object}`
                    arr shouldBeSubtypeOf ts.OBJECT

                    arr shouldBeSubtypeOf java.lang.Cloneable::class.decl
                    arr shouldBeSubtypeOf java.io.Serializable::class.decl

                    (unArrays - arr).forEach {
                        arr shouldBeUnrelatedTo it
                    }
                }
            }

            test("Test reference arrays subtype Object[]") {
                forAll(gen, RangeGen(min = 1, max = 5)) { t, i ->
                    val arrayType = ts.arrayType(t, i)
                    arrayType shouldBeSubtypeOf `t_Array{Object}`
                    arrayType shouldBeSubtypeOf ts.OBJECT
                    arrayType shouldBeSubtypeOf ts.CLONEABLE
                    arrayType shouldBeSubtypeOf ts.SERIALIZABLE

                    true
                }
            }

            test("Test intersection type subtyping") {

                val comp = comparableOf(`t_List{String}`)

                val intersection = ts.intersect(`t_List{String}`, comp)

                intersection shouldBeSubtypeOf comp
                intersection shouldBeSubtypeOf t_Comparable
                intersection shouldBeSubtypeOf `t_List{String}`
                intersection shouldBeSubtypeOf `t_Collection{String}`

                intersection shouldBeUnrelatedTo `t_Collection{Integer}`

            }

            test("Test enum f-bound") {

                val someEnum = SomeEnum::class.raw

                val sup = ts.parameterise(ts.getClassSymbol(java.lang.Enum::class.java), listOf(someEnum))

                someEnum.isRaw shouldBe false
                someEnum shouldBeSubtypeOf sup
            }

            test("Test raw type supertype erasure") {

                val rawArrayList = ArrayList::class.raw

                `t_ArrayList{String}`.erasure shouldBe rawArrayList

                `t_ArrayList{String}` shouldBeSubtypeOf rawArrayList
                rawArrayList.superClass!!.isRaw shouldBe true

            }

            test("Test class type superclass substitution") {

                `t_ArrayList{Integer}`.superClass shouldBe `t_AbstractList{Integer}`

            }

            test("Test capture variable subtyping") {

                val (k, f, c) = ParserTestCtx().makeDummyTVars("K", "F", "C")

                val wild = `?` `super` k
                val superList = TypeConversion.capture(List::class[wild])

                superList.typeArgs[0].shouldBeA<JTypeVar> {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(wild) shouldBe true

                    k shouldBeSubtypeOf it
                    f shouldNotBeSubtypeOf it
                }
            }

            test("Test raw type is convertible to wildcard parameterized type without unchecked conversion") {
                val `Class{String}` = Class::class[ts.STRING]
                val `Class{?}` = Class::class[`?`]
                val Class = Class::class.raw

                /*
                    Class raw = String.class;
                    Class<?> wild = raw;
                    Class<String> param = String.class;

                    raw = param;  // Class    >: Class<String>
                    raw = wild;   // Class    >: Class<?>

                    wild = param; // Class<?> >: Class<String>
                    wild = raw;   // Class<?>      <~ Class  (convertible without unchecked warning)

                    param = raw;  // Class<String> <~ Class  (convertible *with* unchecked warning)
                    param = wild; // (unconvertible)
                 */


                `Class{String}` shouldBeSubtypeOf Class
                `Class{?}` shouldBeSubtypeOf Class

                `Class{String}` shouldBeSubtypeOf `Class{?}`
                `Class{?}` shouldNotBeSubtypeOf `Class{String}`

                Class shouldNotBeSubtypeOf `Class{?}`
                Class shouldNotBeSubtypeOf `Class{String}`

                assertEquals(NO_WARNING, TypeConversion.uncheckedConversionExists(Class, `Class{?}`))
                assertEquals(WARNING, TypeConversion.uncheckedConversionExists(Class, `Class{String}`))
                assertEquals(NONE, TypeConversion.uncheckedConversionExists(`Class{?}`, `Class{String}`))
                assertEquals(NONE, TypeConversion.uncheckedConversionExists(`Class{String}`, `Class{String}`))
            }

            test("Test wildcard subtyping") {

                // this is the tree on this page:
                // https://docs.oracle.com/javase/tutorial/java/generics/subtyping.html

                typeHierarchyTest(`t_List{?}`) {
                    `t_List{? extends Number}` / {
                        `t_List{? extends Integer}` / {
                            `t_List{Integer}` / {
                                extends(`t_List{? super Integer}`)
                            }
                        }
                    }

                    `t_List{? super Integer}` / {
                        `t_List{? super Number}` / {
                            `t_List{Number}` / {
                                extends(`t_List{? extends Number}`)
                            }
                        }
                    }
                }
            }

            test("Test primitive supertype set") {

                boolean.superTypeSet shouldBe setOf(boolean)
                char.superTypeSet shouldBe setOf(char, int, long, float, double)
                byte.superTypeSet shouldBe setOf(byte, short, int, long, float, double)
                short.superTypeSet shouldBe setOf(short, int, long, float, double)
                int.superTypeSet shouldBe setOf(int, long, float, double)
                long.superTypeSet shouldBe setOf(long, float, double)
                float.superTypeSet shouldBe setOf(float, double)
                double.superTypeSet shouldBe setOf(double)

            }

            test("Test null type subtyping") {
                forAll(gen) {
                    ts.NULL_TYPE.isSubtypeOf(it)
                }

                ts.allPrimitives.forNone {
                    assert(ts.NULL_TYPE.isSubtypeOf(it))
                }
            }
        }
    }


})
