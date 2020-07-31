package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import java.io.IOException


class ASTCatchStatementTest : ParserTestSpec({

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
            child<ASTBlock> { }
            child<ASTCatchStatement> {
                it.isMulticatchStatement shouldBe false

                unspecifiedChildren(2)
            }
        }

    }

    parserTest("Test multicatch", javaVersions = J1_7..Latest) {

        importedTypes += IOException::class.java

        "try { } catch (IOException | AssertionError e) { }" should matchStmt<ASTTryStatement> {
            child<ASTBlock> { }
            child<ASTCatchStatement> {
                it.isMulticatchStatement shouldBe true

                val types = fromChild<ASTFormalParameter, List<ASTType>> {
                    val ioe = child<ASTType>(ignoreChildren = true) {
                        it.type shouldBe IOException::class.java
                    }

                    val aerr = child<ASTType>(ignoreChildren = true) {
                        it.type shouldBe java.lang.AssertionError::class.java
                    }

                    child<ASTVariableDeclaratorId> {
                        it.image shouldBe "e"
                    }

                    listOf(ioe, aerr)
                }

                it.caughtExceptionTypeNodes.shouldContainExactly(types)
                it.caughtExceptionTypes.shouldContainExactly(types.map { it.type })

                it.exceptionName shouldBe "e"

                child<ASTBlock> { }
            }
        }

    }


})
