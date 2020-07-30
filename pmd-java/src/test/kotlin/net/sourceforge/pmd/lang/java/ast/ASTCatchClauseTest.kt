package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.string.shouldContain
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

        inContext(StatementParsingCtx) {

            "try { } catch (IOException ioe) { }" should parseAs {
                tryStmt {
                    it::getBody shouldBe block { }
                    catchClause("ioe") {
                        catchFormal("ioe") {
                            it::getModifiers shouldBe localVarModifiers { }

                            it::isMulticatch shouldBe false
                            it::getTypeNode shouldBe classType("IOException")

                            variableId("ioe")
                        }
                        it::getBody shouldBe block { }
                    }
                }
            }
        }
    }

    parserTest("Test multicatch", javaVersions = J1_7..Latest) {

        importedTypes += IOException::class.java
        inContext(StatementParsingCtx) {

            "try { } catch (IOException | AssertionError e) { }" should parseAs {
                tryStmt {
                    it::getBody shouldBe block { }
                    catchClause("e") {
                        catchFormal("e") {
                            it::isMulticatch shouldBe true

                            it::getModifiers shouldBe localVarModifiers { }

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

        }
    }

    parserTest("Test annotated multicatch", javaVersions = J1_8..Latest) {

        importedTypes += IOException::class.java

        inContext(StatementParsingCtx) {
            "try { } catch (@B IOException | @A AssertionError e) { }" should parseAs {
                tryStmt {
                    it::getBody shouldBe block { }
                    catchClause("e") {
                        catchFormal("e") {
                            it::isMulticatch shouldBe true

                            it::getModifiers shouldBe localVarModifiers {
                                annotation("B") // not a type annotation
                            }

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
        }
    }


})
