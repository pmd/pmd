/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("PropertyName", "unused")

package net.sourceforge.pmd.lang.java.types

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.*
import io.kotest.property.exhaustive.exhaustive
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx
import net.sourceforge.pmd.lang.java.symbols.internal.TestClassesGen
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol
import javax.lang.model.type.TypeMirror
import kotlin.streams.toList


val TypeSystem.primitiveGen: Exhaustive<JPrimitiveType> get() = exhaustive(this.allPrimitives.toList())
val TypeSystem.refTypeGen: RefTypeGenArb get() = RefTypeGenArb(this)
val TypeSystem.allTypesGen: Arb<JTypeMirror>
    get() {
        return arbitrary(edgecases = refTypeGen.allEdgecases + primitiveGen.values) { rs ->
            refTypeGen.sample(rs).value
        }
    }

/**
 * Only well-behaved subtypes
 */
fun TypeSystem.subtypesArb() =
        Arb.pair(refTypeGen, refTypeGen)
                .filter { (t, s) -> t.isConvertibleTo(s).bySubtyping() }
                .filter { (t, s) -> !TypeOps.hasUnresolvedSymbol(t) && !TypeOps.hasUnresolvedSymbol(s) }

infix fun Boolean.implies(v: () -> Boolean): Boolean = !this || v()

class RefTypeGenArb(val ts: TypeSystem) : Arb<JTypeMirror>() {

    val allEdgecases = listOf(
        ts.OBJECT,
        // we exclude the null type because it's not ok as an array component
        ts.SERIALIZABLE,
        ts.CLONEABLE
        );

    override fun edgecase(rs: RandomSource): JTypeMirror {
        return allEdgecases.random(rs.random)
    }

    private fun generateTypes(rs : RandomSource) : List<JTypeMirror> {
        with(TypeDslOf(ts).gen) {
            val unresolved1 = ts.createUnresolvedAsmSymbol("some.fake.Symbol")
            val unresolved2 = ts.createUnresolvedAsmSymbol("another.fake.Symbol")

            val pool: List<JTypeMirror> = listOf(
                    `t_List{String}`,
                    t_Enum,
                    t_JPrimitiveType,
                    `t_Enum{JPrimitiveType}`,
                    `t_Collection{Integer}`,
                    `t_List{? extends Number}`,
                    `t_List{?}`,
                    t_ArrayList,
                    `t_ArrayList{Integer}`,
                    t_String,
                    t_CharSequence,
                    t_StringBuilder,
                    t_MapEntry,
                    `t_Array{Object}`,
                    ts.declaration(unresolved1)[t_String],
                    ts.declaration(unresolved2)[t_Integer, `?` extends `t_List{? extends Number}`],
                    ts.declaration(unresolved2)
            ).flatMap {
                it.superTypeSet.toList() + it + it.erasure + it.toArray()
            }

            val testClasses: List<JTypeMirror> = TestClassesGen.getClassesInPackage().map { ts.typeOf(ts.getClassSymbol(it), true) }
            val fullPool = pool.plus(testClasses).shuffled(rs.random)
            val withParameterized = fullPool.flatMap {
                if (it is JClassType && it.isRaw) {
                    val numparams = it.formalTypeParams.size
                    List(numparams) { fullPool.random(rs.random) } + it
                }
                listOf(it)
            }
            return withParameterized
        }
    }

    var cachedTypes : List<JTypeMirror>? = null
    override fun sample(rs: RandomSource): Sample<JTypeMirror> {
        if (cachedTypes == null) {
            cachedTypes = generateTypes(rs)
        }
        return Sample(cachedTypes!!.random(rs.random))
    }
}



@Suppress("ObjectPropertyName", "MemberVisibilityCanBePrivate")
class RefTypeConstants(override val ts: TypeSystem) : TypeDslMixin {

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
    val t_Map: JClassType                           get() = java.util.Map::class.raw
    val t_MapEntry: JClassType                      get() = java.util.Map.Entry::class.raw

    init {
        assert(`t_Enum{E}`.isGenericTypeDeclaration)
    }

    // Object[]
    val `t_Array{Object}`: JTypeMirror              get() = testTypeSystem.OBJECT.toArray(1)


    // Raw Enum
    val t_Enum: JTypeMirror                         get() = java.lang.Enum::class.raw
    val t_JPrimitiveType: JTypeMirror               get() = JPrimitiveType::class.decl
    val `t_Enum{JPrimitiveType}`: JTypeMirror       get() = java.lang.Enum::class[JPrimitiveType::class.decl]
    val `t_Collection{T}`: JTypeMirror              get() = java.util.Collection::class.decl
    val `t_Collection{String}`: JTypeMirror         get() = java.util.Collection::class[t_String]
    val `t_Collection{Integer}`: JTypeMirror        get() = java.util.Collection::class[t_Integer]
    val `t_Iterable{Integer}`: JTypeMirror          get() = java.lang.Iterable::class[t_Integer]
    val t_Iterable: JTypeMirror                     get() = java.lang.Iterable::class.raw

    val t_Iterator: JTypeMirror                     get() = java.util.Iterator::class.raw

    /** raw Comparable */
    val t_Comparable: JClassType                    get() = java.lang.Comparable::class.raw
    val t_Comparator: JClassType                    get() = java.util.Comparator::class.raw

    val t_EnumSet: JClassType                       get() = java.util.EnumSet::class.raw
}



fun ParserTestCtx.makeDummyTVars(vararg names: String): List<JTypeVar> =
        parser.makeDummyTVars(*names)

fun JavaParsingHelper.makeDummyTVars(vararg names: String): List<JTypeVar> {

    val txt = names.joinToString(separator = ", ", prefix = "class Foo< ", postfix = " > {}")

    return this.withProcessing()
            .parse(txt)
            .descendants(ASTTypeParameters::class.java)
            .take(1)
            .children(ASTTypeParameter::class.java)
            .toStream()
            .map {
                it.typeMirror
            }.toList()

}

fun JavaParsingHelper.parseSomeClass(code: String): JClassType {
    return this.withProcessing().parse(code).firstTypeSignature()
}
