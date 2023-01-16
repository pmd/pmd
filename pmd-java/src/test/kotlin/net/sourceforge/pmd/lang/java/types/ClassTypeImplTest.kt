/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol

/**
 * @author Clément Fournier
 */
class ClassTypeImplTest : IntelliMarker,FunSpec({

    val ts = testTypeSystem
    with(TypeDslOf(ts)) {
        with(gen) {



            test("Test repeated withTypeArguments on unresolved type") {
                val sym = ts.createUnresolvedAsmSymbol("does.not.Exist")
                val t = ts.declaration(sym) as JClassType
                t.withTypeArguments(listOf(t_String)).typeArgs shouldBe listOf(t_String)
                t.withTypeArguments(listOf(t_String))
                    .withTypeArguments(emptyList()).typeArgs shouldBe emptyList()

            }

            test("Test generic type decl") {
                t_List::isRaw shouldBe true
                val `t_List{T}` = t_List.genericTypeDeclaration
                `t_List{T}`::isGenericTypeDeclaration shouldBe true

            }


        }
    }


})
