/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol
import net.sourceforge.pmd.lang.test.ast.IntelliMarker
import net.sourceforge.pmd.lang.test.ast.shouldBe
import java.util.stream.Stream

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

    context("streamClasses()") {
        val parser: JavaParsingHelper = JavaParsingHelper.DEFAULT.withResourceContext(javaClass);

        test("class with superclass") {
            val source = """
                class Parent {
                    class NestedInsideParent {}
                }
                class Child extends Parent {
                    class NestedInsideChild {}
                }
            """.trimIndent()
            val root: ASTCompilationUnit = parser.parse(source)
            val classDecl: ASTClassDeclaration = root.children(ASTClassDeclaration::class.java).filter{it.canonicalName == "Child"}.first()!!
            val typeMirror: JTypeMirror = classDecl.typeMirror

            typeMirror.streamClasses().count() shouldBe 2
            typeMirror.streamClasses().anyMatch { it.symbol?.simpleName == "NestedInsideParent" }.shouldBeTrue()
            typeMirror.streamClasses().anyMatch { it.symbol?.simpleName == "NestedInsideChild" }.shouldBeTrue()
        }
    }

})
