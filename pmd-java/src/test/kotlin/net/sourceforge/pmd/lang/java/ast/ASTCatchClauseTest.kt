package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import java.io.IOException


class ASTCatchClauseTest : ParserTestSpec({

    parserTest("Test crash on multicatch", javaVersions = Earliest..J1_6) {

        expectParseException("Composite catch clauses are a feature of Java 1.7, you should select your language version accordingly") {
            parseStatement<ASTTryStatement>("try { } catch (IOException | AssertionError e) { }")
        }

    }

    parserTest("Test single type", javaVersions = J1_5..Latest) {

        importedTypes += IOException::class.java

        "try { } catch (IOException ioe) { }" should matchStmt<ASTTryStatement> {
            block()
            catchClause("ioe") {
                it::isMulticatchStatement shouldBe false
                it::getExceptionName shouldBe "ioe"

                var types: List<ASTClassOrInterfaceType>? = null

                catchFormal("ioe") {
                    types = listOf(classType("IOException"))

                    variableId("ioe")
                }

                block()

                it::getCaughtExceptionTypeNodes shouldBe types!!
            }
        }

    }

    parserTest("Test multicatch", javaVersions = J1_7..Latest) {

        importedTypes += IOException::class.java

        "try { } catch (IOException | AssertionError e) { }" should matchStmt<ASTTryStatement> {
            block()
            catchClause("e") {
                it::isMulticatchStatement shouldBe true
                it::getExceptionName shouldBe "e"

                var types: List<ASTClassOrInterfaceType>? = null

                catchFormal("e") {
                    unionType {
                        val t1 = classType("IOException")
                        val t2 = classType("AssertionError")

                        types = listOf(t1, t2)
                    }
                    variableId("e")
                }

                block()

                it::getCaughtExceptionTypeNodes shouldBe types!!
            }
        }

    }


})
