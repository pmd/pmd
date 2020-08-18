/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("PropertyName", "unused")

package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.property.*
import io.kotest.property.forAll as ktForAll
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters
import net.sourceforge.pmd.lang.java.ast.JavaNode
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.asm.AsmSymbolResolver
import net.sourceforge.pmd.lang.java.types.TypeOps.*
import kotlin.String
import kotlin.reflect.KClass
import kotlin.streams.toList

/*
    Note: in parser tests, you can get a log for the inference by calling
    logTypeInference(verbose = true/false)

    Remember to getTypeMirror() / getMethodType() somewhere as the inference
    is done lazily.
 */

fun newTypeSystem(): TypeSystem = TypeSystem(Thread.currentThread().contextClassLoader)
val testTypeSystem: TypeSystem = newTypeSystem()

// bc the method is package private
val TypeSystem.asmLoader: AsmSymbolResolver get() = this.resolver

fun TypeSystem.lub(vararg us: JTypeMirror): JTypeMirror = lub(us.toList())

val TypeSystem.STRING get() = declaration(getClassSymbol(String::class.java)) as JClassType

typealias TypePair = Pair<JTypeMirror, JTypeMirror>


fun JTypeMirror.getMethodsByName(name: String) = streamMethods { it.simpleName == name }.toList()

fun JMethodSig?.shouldMatchMethod(
        named: String,
        declaredIn: JTypeMirror,
        withFormals: List<JTypeMirror>? = null,
        returning: JTypeMirror? = null
): JMethodSig {
    if (this == null)
        fail("Expected non-null result")

    assertSoftly {
        withClue(this) {
            withClue("name") {
                this::getName shouldBe named
            }
            withClue("Declaring type") { this::getDeclaringType shouldBe declaredIn }
            if (withFormals != null)
                withClue("Formals") {
                    this::getFormalParameters shouldBe withFormals
                }
            if (returning != null)
                withClue("Return type") {
                    this::getReturnType shouldBe returning
                }
        }
    }
    return this
}

fun JTypeVar.withNewBounds(upper: JTypeMirror? = null, lower:JTypeMirror? = null) {
    this.cloneWithBounds(upper ?: this.upperBound, lower ?: this.lowerBound)
}

fun JTypeMirror.shouldBeCaptureOf(wild: JWildcardType) =
        this.shouldBeA<JTypeVar> {
            it.isCaptured shouldBe true
            if (wild.isLowerBound)
                it.lowerBound shouldBe wild.asLowerBound()
            else
                it.upperBound shouldBe wild.asUpperBound()
        }

val TypeSystem.primitiveGen get() = PrimitiveTypeGen(this)
val TypeSystem.refTypeGen get() = RefTypeGen(this)
val TypeSystem.allTypesGen get() = AllTypesGen(this)

infix fun Boolean.implies(v: () -> Boolean): Boolean = !this || v()

class PrimitiveTypeGen(val ts: TypeSystem) : Exhaustive<JPrimitiveType>() {
    override val values = ts.allPrimitives.toList()
}

class AllTypesGen(val ts: TypeSystem) : Arb<JTypeMirror>() {
    private val refGen = RefTypeGen(ts)

    override fun edgecases(): List<JTypeMirror> = ts.allPrimitives.toList() + refGen.edgecases()

    override fun values(rs: RandomSource): Sequence<Sample<JTypeMirror>> = refGen.values(rs)
}

@Suppress("ObjectPropertyName", "MemberVisibilityCanBePrivate")
class RefTypeGen(override val ts: TypeSystem) : Arb<JTypeMirror>(), TypeDslMixin {

    val t_String: JClassType get() = java.lang.String::class.decl
    val t_StringBuilder: JClassType get() = java.lang.StringBuilder::class.decl
    val t_CharSequence: JClassType get() = java.lang.CharSequence::class.decl
    val t_Integer: JClassType get() = java.lang.Integer::class.decl
    val t_Number: JClassType get() = java.lang.Number::class.decl

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
    val t_Map: JClassType                           get() = java.util.Map::class.raw
    val t_MapEntry: JClassType                      get() = java.util.Map.Entry::class.raw

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

    val t_Iterator: JTypeMirror get() = java.util.Iterator::class.raw

    /** raw Comparable */
    val t_Comparable: JClassType get() = java.lang.Comparable::class.raw
    val t_Comparator: JClassType get() = java.util.Comparator::class.raw

    val t_EnumSet: JClassType get() = java.util.EnumSet::class.raw

    override fun edgecases(): List<JTypeMirror> = listOf(testTypeSystem.OBJECT, testTypeSystem.STRING)

    override fun values(rs: RandomSource): Sequence<Sample<JTypeMirror>> =
            pool.asSequence().map { Sample(it, shrinks = RTree({ it.toArray() })) }


    private val pool = listOf(
            `t_List{String}`,
            `t_Enum{E}`,
            t_Enum,
            t_JPrimitiveType,
            `t_Enum{JPrimitiveType}`,
            `t_Collection{Integer}`,
            `t_List{? extends Number}`,
            `t_List{?}`,
            t_MapEntry,
            `t_Array{Object}`

    )

}

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

private fun assertSubtype(t: JTypeMirror, s: JTypeMirror, expected: Subtyping) {
    val res = isSubtype(t , s)
    withClue("$t \n\t\t<: $s") {
        res shouldBe expected
    }
}

infix fun JTypeMirror.shouldBeSubtypeOf(other: JTypeMirror) {
    assertSubtype(this, other, Subtyping.YES)
    // assertSubtype(other, this, SubtypeResult.definitely(this == other))
}

infix fun JTypeMirror.shouldNotBeSubtypeOf(other: JTypeMirror) {
    assertSubtype(this, other, Subtyping.NO)
}

infix fun JTypeMirror.shouldBeUncheckedSubtypeOf(other: JTypeMirror) {
    assertSubtype(this, other, Subtyping.UNCHECKED_WARNING)
}

infix fun JTypeMirror.shouldBeUnrelatedTo(other: JTypeMirror) {
    if (this == other) return
    assertSubtype(this, other, Subtyping.NO)
    assertSubtype(other, this, Subtyping.NO)
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

    val gen get() = RefTypeGen(ts)

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
    val void get() = ts.NO_TYPE


    /** intersection */
    operator fun JTypeMirror.times(t: JTypeMirror): JTypeMirror =
            ts.glb(listOf(this, t))

    // for some tests we assert whether the intersection is flattened, which doesn't work if we use `a * b * c`
    fun glb(t1: JTypeMirror, t2: JTypeMirror, vararg tail: JTypeMirror): JTypeMirror =
            // flatten
            ts.glb(listOf(t1, t2, *tail))

    // for some tests we assert whether the intersection is flattened, which doesn't work if we use `a * b * c`
    fun lub(vararg tail: JTypeMirror): JTypeMirror =
            // flatten
            ts.lub(listOf(*tail))

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
            other is JWildcardType && isSameType(this, other)

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
                it.typeMirror
            }.toList()

}

/** A type that binds to a capture variable for the given wildcard. */
fun captureMatcher(wild: JWildcardType): JTypeVar =
        CaptureMatcher(wild)


fun JTypeMirror.shouldBePrimitive(kind: JPrimitiveType.PrimitiveTypeKind) {
    this shouldBe typeSystem.getPrimitive(kind)
}

fun canIntersect(t: JTypeMirror, s: JTypeMirror) = t.isExlusiveIntersectionBound xor s.isExlusiveIntersectionBound
fun canIntersect(t: JTypeMirror, s: JTypeMirror, vararg others:JTypeMirror) : Boolean{
    val comps = listOf(t, s, *others)
    return comps.filter { it.isExlusiveIntersectionBound }.size <= 1
            && comps.none { it.isPrimitive }
}

/** If so, there can only be one in a well formed intersection. */
val JTypeMirror.isExlusiveIntersectionBound
    get() = this is JArrayType
            || this is JClassType && this.symbol.isClass
            || this is JTypeVar
