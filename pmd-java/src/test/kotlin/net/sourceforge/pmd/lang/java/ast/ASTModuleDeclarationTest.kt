/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.since
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTModuleDeclarationTest : ParserTestSpec({

    parserTest("Test annotations on module", javaVersions = since(J9)) {
        val root: ASTCompilationUnit = parser.withProcessing(true).parse("@A @a.B module foo { } ")
        root.moduleDeclaration.shouldMatchNode<ASTModuleDeclaration> {
            it.getAnnotation("A") shouldBe annotation("A")
            it.getAnnotation("a.B") shouldBe annotation("B")
            modName("foo")
        }
    }

    parserTest("opens decl") {

        inContext(ModuleDirectiveParsingContext) {
            "opens a.package_ to a.module_;" should parseAs {
                child<ASTModuleOpensDirective> {
                    it::getPackageName shouldBe "a.package_"
                    it.targetModules.toList() shouldBe listOf(modName("a.module_"))
                }
            }
            "opens a to a.module_, another;" should parseAs {
                child<ASTModuleOpensDirective> {
                    it::getPackageName shouldBe "a"
                    it.targetModules.toList() shouldBe listOf(
                        modName("a.module_"),
                        modName("another"),
                    )
                }
            }
            "opens a;" should parseAs {
                child<ASTModuleOpensDirective> {
                    it::getPackageName shouldBe "a"
                    it.targetModules.toList().shouldBeEmpty()
                }
            }
        }
    }

    parserTest("exports decl") {

        inContext(ModuleDirectiveParsingContext) {
            "exports a.package_ to a.module_;" should parseAs {
                child<ASTModuleExportsDirective> {
                    it::getPackageName shouldBe "a.package_"
                    it.targetModules.toList() shouldBe listOf(modName("a.module_"))
                }
            }
            "exports a to a.module_, another;" should parseAs {
                child<ASTModuleExportsDirective> {
                    it::getPackageName shouldBe "a"
                    it.targetModules.toList() shouldBe listOf(
                        modName("a.module_"),
                        modName("another"),
                    )
                }
            }
            "exports a;" should parseAs {
                child<ASTModuleExportsDirective> {
                    it::getPackageName shouldBe "a"
                    it.targetModules.toList().shouldBeEmpty()
                }
            }
        }
    }

    parserTest("Test uses") {

        inContext(ModuleDirectiveParsingContext) {
            "uses a.clazz.Name;" should parseAs {
                child<ASTModuleUsesDirective> {
                    it::getService shouldBe classType("Name")
                }
            }
        }
    }
    parserTest("Test provides") {

        inContext(ModuleDirectiveParsingContext) {
            enableProcessing()
            "provides an.Itf with imp1.Impl;" should parseAs {
                child<ASTModuleProvidesDirective> {
                    it::getService shouldBe classType("Itf")
                    it.serviceProviders.toList() shouldBe listOf(
                        classType("Impl") {
                            val sym = it.referencedSym as JClassSymbol
                            sym.canonicalName shouldBe "imp1.Impl"
                        }
                    )
                }
            }
        }
    }

})

fun TreeNodeWrapper<Node, *>.modName(name: String, assertions: NodeSpec<ASTModuleName> = EmptyAssertions) =
    child<ASTModuleName> {
        it::getName shouldBe name
        assertions(this)
    }
