/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forNone
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.properties.forNone
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.ints
import io.kotest.property.forAll
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.java.types.testdata.ComparableList
import net.sourceforge.pmd.lang.java.types.testdata.SomeEnum
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Clément Fournier
 */
class TypeEqualityTest : FunSpec({

    // TODO test that a non-generic type is equal to itself whether created with declaration or rawType

    val ts = testTypeSystem
    with(TypeDslOf(ts)) {
        with(gen) {

            test("Test primitive equality") {

                ts.allPrimitives.forAll {
                    assertEquals(it, it)
                }

                boolean shouldNotBe int
                double shouldNotBe int
                char shouldNotBe byte
                char shouldNotBe ts.OBJECT

                forAll(gen, ts.primitiveGen) { ref, prim ->
                    ref != prim
                }
            }

            test("Test array equality") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (t == s) == (t.toArray(1) == t.toArray(1))
                }
            }

            test("Test equality symmetry") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (t == s) == (s == t)
                }
            }

            test("Test wildcard equality") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (t == s) == (`?` extends t == `?` extends s)
                }

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (t == s) == (`?` `super` t == `?` `super` s)
                }
            }

            test("Test intersection equality") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    canIntersect(t, s) implies {
                        (t * s) == (t * s)
                    }
                }
            }

            test("Test intersection symmetry") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    canIntersect(t, s) implies {
                        (t * s) == (s * t)
                    }
                }
            }

            test("Test intersection left associativity") {

                forAll(ts.allTypesGen, ts.allTypesGen, ts.allTypesGen) { t, s, r ->
                    canIntersect(t, s, r) implies {
                        (t * s) * r == (t * s * r)
                    }
                }
            }

            test("Test intersection right associativity") {

                forAll(ts.allTypesGen, ts.allTypesGen, ts.allTypesGen) { t, s, r ->
                    canIntersect(t, s, r) implies {
                        (t * s) * r == (t * s * r)
                    }
                }
            }

            test("Test reference arrays subtype Object[]") {
                forAll(gen, Exhaustive.ints(1..5)) { t, i ->
                    val arrayType = ts.arrayType(t, i)
                    arrayType shouldBeSubtypeOf `t_Array{Object}`
                    arrayType shouldBeSubtypeOf ts.OBJECT
                    arrayType shouldBeSubtypeOf ts.CLONEABLE
                    arrayType shouldBeSubtypeOf ts.SERIALIZABLE

                    true
                }
            }

            test("Test intersection type subtyping") {

                val intersection = `t_List{String}` * t_Comparable[`t_List{String}`]

                // implements List<T> and Comparable<T>
                val comparableList = ComparableList::class[t_String]

                withClue("Intersection supertypes") {
                    intersection shouldBeSubtypeOf t_Comparable[`t_List{String}`]
                    intersection shouldBeSubtypeOf t_Comparable
                    intersection shouldBeSubtypeOf `t_List{String}`
                    intersection shouldBeSubtypeOf `t_Collection{String}`
                    intersection shouldNotBeSubtypeOf comparableList

                    intersection shouldBeUnrelatedTo `t_Collection{Integer}`
                }

                withClue("Intersection subtypes") {

                    comparableList shouldBeSubtypeOf intersection

                    t_Comparable shouldNotBeSubtypeOf intersection
                    `t_List{String}` shouldNotBeSubtypeOf intersection
                    `t_Collection{String}` shouldNotBeSubtypeOf intersection
                }

                withClue("Intersection subtypes itself") {
                    // create another so that == will not succeed
                    val intersection2 = `t_List{String}` * t_Comparable[`t_List{String}`]

                    intersection shouldBeSubtypeOf intersection2
                    intersection2 shouldBeSubtypeOf intersection
                }
            }

            test("Test enum f-bound") {

                val someEnum = SomeEnum::class.decl

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

                val `Comparable{?}` = java.lang.Comparable::class[`?`]

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

                Class shouldBeSubtypeOf `Class{?}` // no warning
                Class shouldBeUncheckedSubtypeOf `Class{String}`

                ts.STRING shouldBeSubtypeOf `Comparable{?}`
            }

            test("Test wildcard subtyping") {

                // this is the tree on this page:
                // https://docs.oracle.com/javase/tutorial/java/generics/subtyping.html

                assertSubtypeOrdering(`t_List{?}`, `t_List{? extends Number}`, `t_List{? extends Integer}`, `t_List{Integer}`)
                assertSubtypeOrdering(`t_List{?}`, `t_List{? super Integer}`, `t_List{? super Number}`, `t_List{Number}`)
                assertSubtypeOrdering(`t_List{?}`, `t_List{? extends Number}`, `t_List{Number}`)
                assertSubtypeOrdering(`t_List{?}`, `t_List{? super Integer}`, `t_List{Integer}`)

                `t_List{Number}` shouldBeUnrelatedTo `t_List{? extends Integer}`
                `t_List{Integer}` shouldBeUnrelatedTo `t_List{? super Number}`

                `t_List{? extends Number}` shouldBeUnrelatedTo `t_List{? super Integer}`
                `t_List{? extends Integer}` shouldBeUnrelatedTo `t_List{? super Number}`
                `t_List{Number}` shouldBeUnrelatedTo `t_List{Integer}`

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
                    assertTrue(ts.NULL_TYPE.isSubtypeOf(it))
                }
            }

            test("Test error type subtyping") {
                forAll(gen) {
                    ts.ERROR_TYPE.isSubtypeOf(it)
                }

                ts.allPrimitives.forAll {
                    assertTrue(ts.ERROR_TYPE.isSubtypeOf(it))
                }
            }

            test("Test unresolved type subtyping") {
                forAll(gen) {
                    ts.UNRESOLVED_TYPE.isSubtypeOf(it)
                }

                ts.allPrimitives.forAll {
                    assertTrue(ts.UNRESOLVED_TYPE.isSubtypeOf(it))
                }
            }
        }
    }


})
