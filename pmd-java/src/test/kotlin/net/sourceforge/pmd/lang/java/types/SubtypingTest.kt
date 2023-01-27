/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forNone
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints
import io.kotest.property.forAll
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.UnresolvedClassStore
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol
import net.sourceforge.pmd.lang.java.types.TypeConversion.*
import net.sourceforge.pmd.lang.java.types.TypeOps.Convertibility.*
import net.sourceforge.pmd.lang.java.types.testdata.ComparableList
import net.sourceforge.pmd.lang.java.types.testdata.SomeEnum
import kotlin.test.assertTrue

/**
 * @author Clément Fournier
 */
class SubtypingTest : FunSpec({

    val ts = testTypeSystem
    with(TypeDslOf(ts)) {
        with(gen) {

            test("Test primitive subtyping") {

                assertSubtypeOrdering(double, float, long, int, short, byte)
                assertSubtypeOrdering(double, float, long, int, char)

                short shouldBeUnrelatedTo char
                byte shouldBeUnrelatedTo char

                (ts.allPrimitives.toList() - boolean).forEach {
                    it shouldBeUnrelatedTo boolean
                }
            }

            test("Test primitive arrays are unrelated") {

                val unArrays = ts.allPrimitives.map {
                    ts.arrayType(it)
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
                forAll(ts.refTypeGen, Exhaustive.ints(1..5)) { t, i ->
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

                val sup = ts.parameterise(ts.getClassSymbol(java.lang.Enum::class.java)!!, listOf(someEnum))

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

                val (k, f) = ParserTestCtx(this).makeDummyTVars("K", "F")

                val wild = `?` `super` k
                val superList = capture(List::class[wild])

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

                assertSubtype(Class, `Class{?}`) { this == UNCHECKED_NO_WARNING }
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

            test("Test wildcard subtyping (property-based)") {

                checkAll(ts.subtypesArb()) { (t, s) ->
                    t_List[t] shouldBeSubtypeOf t_List[`?` extends s]
                    t_List[s] shouldBeSubtypeOf t_List[`?` `super` t]
                }
            }

            test("Test containment") {

                checkAll(ts.refTypeGen, ts.refTypeGen) { t, s ->
                    if (t != s) // remember, t and s are types, never wildcards with this generator
                        t_List[t] shouldBeUnrelatedTo t_List[s]
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

            test("Null type is only compatible with reference types") {
                forAll(ts.refTypeGen) {
                    ts.NULL_TYPE.isSubtypeOf(it)
                }

                ts.allPrimitives.forNone {
                    assertTrue(ts.NULL_TYPE.isSubtypeOf(it))
                }
            }

            test("Error type is compatible with anything") {
                forAll(ts.allTypesGen) {
                    ts.ERROR.isSubtypeOf(it)
                }
            }

            test("Unresolved type is compatible with anything") {
                forAll(ts.allTypesGen) {
                    ts.UNKNOWN.isSubtypeOf(it)
                }
            }

            test("Unresolved symbol is compatible with any class/interface") {
                val t = ts.declaration(UnresolvedClassStore(ts).makeUnresolvedReference("obj.foo", 0))
                    .shouldBeUnresolvedClass("obj.foo")

                checkAll(ts.primitiveGen) { s ->
                    t shouldNotBeSubtypeOf s
                }

                checkAll(ts.refTypeGen) { s ->
                    if (s.isArray)
                        t shouldNotBeSubtypeOf s
                    else
                        t shouldBeSubtypeOf s
                }
            }

            test("Captured subtyping tests") {


                checkAll(ts.refTypeGen) { t ->

                    withClue("Upper captures") {
                        capture(t_List[`?` extends t]) shouldSubtypeNoCapture t_List[`?` extends t]
                        t_List[`?` extends t] shouldNotSubtypeNoCapture capture(t_List[`?` extends t])
                    }

                    withClue("Lower captures") {
                        capture(t_List[`?` `super` t]) shouldSubtypeNoCapture t_List[`?` `super` t]
                        t_List[`?` `super` t] shouldNotSubtypeNoCapture capture(t_List[`?` `super` t])
                    }
                }

            }

            test("Captured subtyping wild vs wild") {

                checkAll(ts.subtypesArb()) { (t, s) ->
                    // println("$t <: $s")

                    capture(t_List[`?` extends t]) shouldSubtypeNoCapture t_List[`?` extends s]
                }

            }

            test("Some negs for captures") {


                checkAll(ts.refTypeGen) { t ->
                    val someCapture = capture(t_List[`?` extends t]).typeArgs[0]

                    someCapture shouldBe captureMatcher(`?` extends t)

                    // List<? extends T>   VS   List<? extends capture#809 of ? extends T>
                    // not a subtype
                    t_List[`?` extends t] shouldNotSubtypeNoCapture t_List[`?` extends someCapture]
                }
            }

            test("Test non well-formed types") {
                val sym = ts.createUnresolvedAsmSymbol("does.not.Exist") as JClassSymbol
                sym[t_String, t_String] shouldBeUnrelatedTo sym[t_String]
                sym[t_String] shouldBeSubtypeOf sym[t_String]
                sym[t_String] shouldBeSubtypeOf sym[`?` extends t_String] // containment
            }
        }
    }


})
