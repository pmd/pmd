/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("PropertyName", "unused")

package net.sourceforge.pmd.lang.java.types

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters
import net.sourceforge.pmd.lang.java.ast.JavaNode
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.types.TypeOps.isSameType
import kotlin.reflect.KClass
import kotlin.streams.toList


val JavaNode.typeDsl get() = TypeDslOf(this.typeSystem)

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

    val gen get() = RefTypeConstants(ts)

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
