/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_7
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9
import net.sourceforge.pmd.lang.test.ast.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTTryStatementTest : ParserTestSpec({
    parserTestContainer("Test try with resources", javaVersions = J1_7..Latest) {
        inContext(StatementParsingCtx) {
            "try (Foo a = 2){}" should parseAs {
                tryStmt {
                    child<ASTResourceList> {
                        child<ASTResource> {
                            it::isConciseResource shouldBe false

                            it::getInitializer shouldBe fromChild<ASTLocalVariableDeclaration, ASTExpression> {
                                it::getModifiers shouldBe localVarModifiers {
                                    it::getExplicitModifiers shouldBe emptySet()
                                    it::getEffectiveModifiers shouldBe setOf(JModifier.FINAL)
                                }

                                classType("Foo")
                                fromChild<ASTVariableDeclarator, ASTExpression> {
                                    variableId("a") {
                                        it::isResourceDeclaration shouldBe true
                                    }
                                    int(2)
                                }
                            }
                        }
                    }

                    block()
                }
            }

            "try (final Foo a = 2){}" should parseAs {
                tryStmt {
                    child<ASTResourceList> {
                        child<ASTResource> {
                            it::isConciseResource shouldBe false

                            it::getInitializer shouldBe fromChild<ASTLocalVariableDeclaration, ASTExpression> {
                                it::getModifiers shouldBe localVarModifiers {
                                    it::getExplicitModifiers shouldBe setOf(JModifier.FINAL)
                                    it::getEffectiveModifiers shouldBe setOf(JModifier.FINAL)
                                }
                                classType("Foo")
                                fromChild<ASTVariableDeclarator, ASTExpression> {
                                    variableId("a") {
                                        it::isResourceDeclaration shouldBe true
                                    }
                                    int(2)
                                }
                            }
                        }
                    }

                    block()
                }
            }
        }
    }

    parserTestContainer("Test concise try with resources", javaVersions = J9..Latest) {
        inContext(StatementParsingCtx) {
            "try (a){}" should parseAs {
                tryStmt {
                    child<ASTResourceList> {
                        child<ASTResource> {
                            it::isConciseResource shouldBe true

                            it::getInitializer shouldBe variableAccess("a")
                        }
                        it::hasTrailingSemiColon shouldBe false
                    }

                    block()
                }
            }

            "try (a;){}" should parseAs {
                tryStmt {
                    child<ASTResourceList> {
                        child<ASTResource> {
                            it::isConciseResource shouldBe true

                            it::getInitializer shouldBe variableAccess("a")
                        }
                        it::hasTrailingSemiColon shouldBe true
                    }

                    block()
                }
            }

            "try (a.b){}" should parseAs {
                tryStmt {
                    child<ASTResourceList> {
                        child<ASTResource> {
                            it::isConciseResource shouldBe true

                            it::getInitializer shouldBe fieldAccess("b") {
                                ambiguousName("a")
                            }
                        }
                    }

                    block()
                }
            }

            // the expr must be a field access or var access
            "try ( a.foo() ){}" shouldNot parse()
            "try (new Foo()){}" shouldNot parse()
            "try(arr[0]) {}" shouldNot parse()

            "try ( a.foo().b ){}" should parse()
            "try (new Foo().x){}" should parse()
            // this is also allowed by javac
            "try(this) {}" should parse()
            "try(Foo.this) {}" should parse()
        }
    }
})
