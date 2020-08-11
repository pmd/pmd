/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("PropertyName", "unused")

package net.sourceforge.pmd.lang.java.types

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters
import net.sourceforge.pmd.lang.java.ast.JavaNode
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.asm.AsmSymbolResolver
import kotlin.String
import kotlin.reflect.KClass
import kotlin.streams.toList


fun newTypeSystem(): TypeSystem = TypeSystem(Thread.currentThread().contextClassLoader)
val testTypeSystem: TypeSystem = newTypeSystem()

val TypeSystem.asmLoader: AsmSymbolResolver get() = this.resolver

fun TypeSystem.lub(vararg us: JTypeMirror): JTypeMirror = lub(us.toList())
fun TypeSystem.intersect(vararg us: JTypeMirror): JTypeMirror = intersect(us.toList())

val TypeSystem.STRING get() = declaration(getClassSymbol(String::class.java)) as JClassType

typealias TypePair = Pair<JTypeMirror, JTypeMirror>


fun JTypeMirror.getMethodsByName(name: String) = streamMethods { it.simpleName == name }.toList()

object PrimitiveGen : Exhaustive<JPrimitiveType>() {
    override val values: List<JPrimitiveType>
        get() = testTypeSystem.allPrimitives.toList()
}

fun JMethodSig.shouldMatchMethod(
        named: String,
        declaredIn: JTypeMirror,
        withFormals: List<JTypeMirror>,
        returning: JTypeMirror = this.typeSystem.NO_TYPE
): JMethodSig {
    this::getName shouldBe named
    this::getDeclaringType shouldBe declaredIn
    this::getFormalParameters shouldBe withFormals
    this::getReturnType shouldBe returning
    return this
}

fun JTypeVar.withNewBounds(upper: JTypeMirror? = null, lower:JTypeMirror? = null) {
    this.cloneWithBounds(upper ?: this.upperBound, lower ?: this.lowerBound)
}


@Suppress("ObjectPropertyName", "MemberVisibilityCanBePrivate")
class TypeGen(override val ts: TypeSystem) : Arb<JTypeMirror>(), TypeDslMixin {

    override fun edgecases(): List<JTypeMirror> = listOf(testTypeSystem.OBJECT, testTypeSystem.STRING)

    override fun values(rs: RandomSource): Sequence<Sample<JTypeMirror>> = pool.asSequence().map { Sample(it) }

    val t_String: JClassType                        get() = java.lang.String::class.decl
    val t_StringBuilder: JClassType                 get() = java.lang.StringBuilder::class.decl
    val t_CharSequence: JClassType                  get() = java.lang.CharSequence::class.decl
    val t_Integer: JClassType                       get() = java.lang.Integer::class.decl
    val t_Number: JClassType                        get() = java.lang.Number::class.decl

    val t_List: JClassType                          get() = java.util.List::class.raw
    val t_Collection: JClassType                    get() = java.util.Collection::class.raw

    val t_AbstractList: JClassType                  get() = java.util.AbstractList::class.raw
    val t_ArrayList: JClassType                     get() = java.util.ArrayList::class.raw
    val t_AbstractCollection: JClassType            get() = java.util.AbstractCollection::class.raw

    val `t_List{String}`: JClassType                get() = java.util.List::class[t_String]
    val `t_List{? extends String}`: JClassType      get() = java.util.List::class[`?` extends t_String]
    val `t_ArrayList{String}`: JClassType           get() = java.util.ArrayList::class[t_String]
    val `t_LinkedList{String}`: JClassType          get() = java.util.LinkedList::class[t_String]

    val `t_ArrayList{Integer}`: JClassType          get() = java.util.ArrayList::class[t_Integer]
    val `t_LinkedList{Integer}`: JClassType         get() = java.util.LinkedList::class[t_Integer]
    val `t_AbstractList{Integer}`: JClassType       get() = java.util.AbstractList::class[t_Integer]
    val `t_AbstractCollection{Integer}`: JClassType get() = java.util.AbstractCollection::class[t_Integer]

    val `t_List{Number}`: JClassType                get() = java.util.List::class[t_Number]
    val `t_List{Integer}`: JClassType               get() = java.util.List::class[t_Integer]

    val `t_List{? extends Number}`: JClassType      get() = java.util.List::class[`?` extends t_Number]
    val `t_List{? extends Integer}`: JClassType     get() = java.util.List::class[`?` extends t_Integer]
    val `t_List{? super Number}`: JClassType        get() = java.util.List::class[`?` `super` t_Number]
    val `t_List{? super Integer}`: JClassType       get() = java.util.List::class[`?` `super` t_Integer]
    val `t_List{?}`: JClassType                     get() = java.util.List::class[`?`]

    // Enum<E>
    val `t_Enum{E}`: JClassType                     get() = java.lang.Enum::class.decl

    val t_Stream: JClassType                        get() = java.util.stream.Stream::class.raw
    val t_Function: JClassType                      get() = java.util.function.Function::class.raw

    init {
        assert(`t_Enum{E}`.isGenericTypeDeclaration)
    }

    // Object[]
    val `t_Array{Object}`: JTypeMirror get() = testTypeSystem.OBJECT.toArray(1)


    // Raw Enum
    val t_Enum: JTypeMirror get() = java.lang.Enum::class.raw
    val t_JPrimitiveType: JTypeMirror get() = JPrimitiveType::class.decl
    val `t_Enum{JPrimitiveType}`: JTypeMirror get() = java.lang.Enum::class[JPrimitiveType::class.decl]
    val `t_Collection{T}`: JTypeMirror get() = java.util.Collection::class.decl
    val `t_Collection{String}`: JTypeMirror get() = java.util.Collection::class[t_String]
    val `t_Collection{Integer}`: JTypeMirror get() = java.util.Collection::class[t_Integer]
    val `t_Iterable{Integer}`: JTypeMirror get() = java.lang.Iterable::class[t_Integer]
    val t_Iterable: JTypeMirror get() = java.lang.Iterable::class.raw

    /** raw Comparable */
    val t_Comparable: JClassType get() = java.lang.Comparable::class.raw

    val t_EnumSet: JClassType get() = java.util.EnumSet::class.raw


    fun comparableOf(mirror: JTypeMirror): JClassType = Comparable::class[mirror]

    private val pool = listOf(
            `t_List{String}`,
            `t_Enum{E}`,
            t_Enum,
            t_JPrimitiveType,
            `t_Enum{JPrimitiveType}`,
            `t_Collection{Integer}`,
            `t_List{? extends Number}`,
            `t_List{?}`,
            `t_Array{Object}`

    )

}

val RefTypeGen = TypeGen(testTypeSystem)

/**
 * assertSubtypeOrdering(a, b, c) asserts that a >: b >: c
 * In other words, the supertypes are on the left, subtypes on the right
 */
fun assertSubtypeOrdering(vararg ts: JTypeMirror) {
    for ((a, b) in ts.zip(ts.asList().drop(1))) {
        b shouldBeSubtypeOf a
    }
}

fun JClassType.parameterize(m1: JTypeMirror, vararg mirror: JTypeMirror): JClassType = withTypeArguments(listOf(m1, *mirror))

private fun assertSubtype(t: JTypeMirror, s: JTypeMirror, pos: Boolean) {
    assertSubtypeUnchecked(t, s, pos, unchecked = false)
    // println("Proven ${if(pos) "" else " ¬"} $t <: $s")
}

private fun assertSubtypeUnchecked(t: JTypeMirror, s: JTypeMirror, pos: Boolean, unchecked: Boolean = true) {
    val res = t.isSubtypeOf(s, unchecked)
    assert(if (pos) res else !res) {
        "Failure, expected\n\t${if (pos) "" else " not"} $t \n\t\t<: $s"
    }
    // println("Proven ${if(pos) "" else " ¬"} $t <: $s")
}

infix fun JTypeMirror.shouldBeSubtypeOf(other: JTypeMirror) {
    assertSubtype(this, other, true)
    assertSubtype(other, this, this == other)
}

infix fun JTypeMirror.shouldNotBeSubtypeOf(other: JTypeMirror) {
    assertSubtype(this, other, false)
}

infix fun JTypeMirror.shouldBeUncheckedSubtypeOf(other: JTypeMirror) {
    assertSubtypeUnchecked(this, other, true)
}

infix fun JTypeMirror.shouldBeUnrelatedTo(other: JTypeMirror) {
    if (this == other) return
    assertSubtype(this, other, false)
    assertSubtype(other, this, false)
}

/**
 * A DSL over the API of [TypeSystem], to build types concisely.
 * Eg:
 *
 * List<String>:            List::class[String::class]
 * int[][]:                 int.toArray(2)
 * List<? extends Number>:  List::class[`?` extends Number::class]
 *
 * Use [typeDsl] (eg `with(node.typeDsl) { ... }`,
 * or [TypeDslOf] (eg `with(TypeDslOf(ts)) { ... }`)
 *
 * to bring it into scope.
 */
interface TypeDslMixin {

    val ts: TypeSystem

    val gen get() = TypeGen(ts)

    /* extensions to turn a class (literal) into a type mirror */

    val KClass<*>.raw: JClassType get() = ts.rawType(ts.getClassSymbol(this.java)) as JClassType
    val KClass<*>.decl: JClassType get() = java.decl
    val Class<*>.decl: JClassType get() = ts.declaration(ts.getClassSymbol(this)) as JClassType

    /* aliases with regular java keywords */

    val int get() = ts.INT
    val char get() = ts.CHAR
    val double get() = ts.DOUBLE
    val byte get() = ts.BYTE
    val long get() = ts.LONG
    val short get() = ts.SHORT
    val boolean get() = ts.BOOLEAN
    val float get() = ts.FLOAT


    /** intersection */
    operator fun JTypeMirror.times(t: JTypeMirror): JTypeMirror =
            // flatten
            ts.intersect(TypeOps.asList(this) + TypeOps.asList(t))

    /** subtyping */
    operator fun JTypeMirror.compareTo(t: JTypeMirror): Int = when {
        this.isSubtypeOf(t) -> -1
        t.isSubtypeOf(this) -> +1
        else                -> 0
    }

    fun JTypeMirror.toArray(dims: Int = 1): JTypeMirror = ts.arrayType(this, dims)

    // these operators overload the array access syntax
    // to represent parameterization:
    //  t[s] === t<s>
    //  List::class[String::class] === List<String>

    fun typeOf(binaryName: String): JClassType = ts.declaration(ts.getClassSymbol(binaryName)!!) as JClassType

    operator fun JClassSymbol.get(vararg t: JTypeMirror): JClassType = (ts.declaration(this) as JClassType).withTypeArguments(t.toList())
    operator fun JTypeMirror.get(vararg t: JTypeMirror): JClassType = (this as JClassType).withTypeArguments(t.toList())
    operator fun KClass<*>.get(vararg t: JTypeMirror): JClassType = this.decl.withTypeArguments(t.toList())
    operator fun KClass<*>.get(vararg t: KClass<*>): JClassType = this.decl.withTypeArguments(t.toList().map { it.decl })

    /** Unbounded wildcard. The wildcard DSL allows
     * using extends and super as methods.
     *
     * Eg
     *      List::class[`?` extends Number::class]
     *      List::class[`?` super String::class]
     *
     */
    val `?`: WildcardDsl get() = WildcardDsl(ts)

}

val JavaNode.typeDsl get() = TypeDslOf(this.typeSystem)


class TypeDslOf(override val ts: TypeSystem) : TypeDslMixin


class WildcardDsl(override val ts: TypeSystem) : JWildcardType by ts.UNBOUNDED_WILD, TypeDslMixin {

    infix fun extends(t: JTypeMirror) = ts.wildcard(true, t) as JWildcardType
    infix fun extends(t: KClass<*>) = extends(t.raw)
    infix fun `super`(t: JTypeMirror) = ts.wildcard(false, t) as JWildcardType
    infix fun `super`(t: KClass<*>) = `super`(t.raw)

    override fun equals(other: Any?): Boolean =
            other is JWildcardType && TypeOps.isSameType(this, other)

    override fun hashCode(): Int = ts.UNBOUNDED_WILD.hashCode()
}




fun ParserTestCtx.makeDummyTVars(vararg names: String): List<JTypeVar> {

    val txt = names.joinToString(separator = ", ", prefix = "class Foo< ", postfix = " > {}")

    return parser.withProcessing()
            .parse(txt)
            .descendants(ASTTypeParameters::class.java)
            .take(1)
            .children(ASTTypeParameter::class.java)
            .toStream()
            .map {
                it.typeMirror as JTypeVar
            }.toList()

}
