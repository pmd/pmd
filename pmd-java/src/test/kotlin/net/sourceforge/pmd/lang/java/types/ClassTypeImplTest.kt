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
class ClassTypeImplTest : FunSpec({

    val ts = testTypeSystem
    with(TypeDslOf(ts)) {
        with(gen) {



            test("Test repeated withTypeArguments on unresolved type") {
                val sym = ts.createUnresolvedAsmSymbol("does.not.Exist") as JClassSymbol
                val t = ts.declaration(sym) as JClassType
                t.withTypeArguments(listOf(t_String)).typeArgs shouldBe listOf(t_String)
                t.withTypeArguments(listOf(t_String))
                    .withTypeArguments(emptyList()).typeArgs shouldBe emptyList()

            }


        }
    }


})
