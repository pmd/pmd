package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.matchers.string.shouldContain
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import java.io.IOException


class ASTCatchClauseTest : ParserTestSpec({

    parserTest("Test crash on multicatch", javaVersions = Earliest..J1_6) {


        inContext(StatementParsingCtx) {

            "try { } catch (IOException | AssertionError e) { }" should throwParseException {
                it.message.shouldContain("Composite catch clauses are a feature of Java 1.7, you should select your language version accordingly")
            }

        }
    }

    parserTest("Test single type", javaVersions = J1_5..Latest) {

        importedTypes += IOException::class.java

        "try { } catch (IOException ioe) { }" should matchStmt<ASTTryStatement> {
            it::getBody shouldBe block { }
            catchClause("ioe") {
                catchFormal("ioe") {
                    it::isMulticatch shouldBe false
                    it::getTypeNode shouldBe classType("IOException")

                    variableId("ioe")
                }
                it::getBody shouldBe block { }
            }
        }

    }

    parserTest("Test multicatch", javaVersions = J1_7..Latest) {

        importedTypes += IOException::class.java

        "try { } catch (IOException | AssertionError e) { }" should matchStmt<ASTTryStatement> {
            it::getBody shouldBe block { }
            catchClause("e") {
                catchFormal("e") {
                    it::isMulticatch shouldBe true

                    it::getTypeNode shouldBe unionType {
                        classType("IOException")
                        classType("AssertionError")
                    }

                    variableId("e")
                }


                it::getBody shouldBe block { }
            }
        }

    }

    parserTest("Test annotated multicatch", javaVersions = J1_8..Latest) {

        importedTypes += IOException::class.java

        "try { } catch (@B IOException | @A AssertionError e) { }" should matchStmt<ASTTryStatement> {
            it::getBody shouldBe block { }
            catchClause("e") {
                catchFormal("e") {
                    it::isMulticatch shouldBe true

                    annotation("B") // not a type annotation

                    unionType {
                        classType("IOException")
                        classType("AssertionError") {
                            annotation("A")
                        }
                    }

                    variableId("e")
                }

                block {}
            }
        }

    }


})
