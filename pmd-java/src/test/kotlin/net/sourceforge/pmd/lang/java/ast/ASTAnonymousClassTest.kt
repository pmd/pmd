/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_ANONYMOUS

class ASTAnonymousClassTest : ParserTestSpec({

    parserTest("Anon class modifiers") {

        inContext(StatementParsingCtx) {

            """
               new java.lang.Runnable() {

                    @Override public void run() {

                    }
               };
            """ should parseAs {

                exprStatement {

                    constructorCall {
                        it::getTypeNode shouldBe classType("Runnable")

                        it::getArguments shouldBe child {}

                        it::getAnonymousClassDeclaration shouldBe child {

                            it::getModifiers shouldBe modifiers {
                                it.effectiveModifiers shouldBe emptySet()
                                it.explicitModifiers shouldBe emptySet()
                            }

                            it should haveVisibility(V_ANONYMOUS)

                            it::isAnonymous shouldBe true
                            it::isInterface shouldBe false

                            val anon = it

                            child<ASTClassOrInterfaceBody> {
                                child<ASTMethodDeclaration>(ignoreChildren = true) {
                                    it::getEnclosingType shouldBe anon
                                }
                            }
                        }
                    }
                }
            }
        }
    }


})
