/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types

import io.kotlintest.matchers.contain
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec

class SubstTest : ProcessorTestSpec({


    parserTest("Test full test case") {


        val typeDecl =
                parser.parse("""
                    package java.util;

                    class Foo<K extends F,
                              F,
                              C extends F> {

                        Map<Map<K, F>, Map<F, C>> field;

                    }
                """).descendants(ASTClassOrInterfaceDeclaration::class.java).firstOrThrow()

        val typeDsl = typeDecl.typeDsl

        val (k, f, c) = typeDecl.typeMirror.formalTypeParams


        val fieldT = typeDecl.descendants(ASTClassOrInterfaceType::class.java).drop(2).firstOrThrow()

        // assert the form of the type
        fieldT.typeMirror shouldBe with (typeDsl) {
            Map::class[Map::class[k, f], Map::class[f, c]]
        }

        fieldT.typeMirror.toString() shouldBe "java.util.Map<java.util.Map<K, F>, java.util.Map<F, C>>"

        val `List{F}` = with (typeDsl) { List::class[f] }

        val subst =
            Substitution.mapping(
                    listOf(k, f),
                    listOf(`List{F}`, k)
            )


        subst.toString() shouldBe "Substitution[K => java.util.List<F>; F => K]"


        subst.apply(f) shouldBe k
        subst.apply(k) shouldBe `List{F}`

        val subbed = TypeOps.subst(fieldT.typeMirror, subst)

        subbed.toString() shouldBe "java.util.Map<java.util.Map<java.util.List<F>, K>, java.util.Map<K, C>>"


    }



    fun subOf(vararg pairs: Pair<SubstVar, JTypeMirror>) =
            pairs.toList()
                    .fold(Substitution.EMPTY) { a, (b, c) ->
                        a.plus(b, c)
                    }

    operator fun Substitution.invoke(t: JTypeMirror) = TypeOps.subst(t, this)


    parserTest("Test simple subst") {


        val (a, b, c) = makeDummyTVars("A", "B", "C")

        with(TypeDslOf(a.typeSystem)) {
            val `t_List{A}` = List::class[a]
            val `t_Iter{B}` = Iterable::class[b]
            val `t_Coll{C}` = Collection::class[c]


            val sub1 = subOf(a to `t_Iter{B}`)
            val sub2 = subOf(b to `t_Coll{C}`)

            val `t_List{Iter{B}}` = sub1(`t_List{A}`)

            `t_List{Iter{B}}` shouldBe List::class[Iterable::class[b]]

            val `t_List{Iter{t_Coll{C}}}` = sub2(`t_List{Iter{B}}`)

            `t_List{Iter{t_Coll{C}}}` shouldBe List::class[Iterable::class[Collection::class[c]]]


            val composed = sub1.andThen(sub2)

            composed.getMap() should contain<SubstVar, JTypeMirror>(a, Iterable::class[Collection::class[c]])

            composed.getMap().shouldContainExactly(mapOf<SubstVar, JTypeMirror>(
                    a to Iterable::class[Collection::class[c]],
                    b to `t_Coll{C}`
            ))

            composed(`t_List{A}`) shouldBe `t_List{Iter{t_Coll{C}}}`
        }


    }


})


