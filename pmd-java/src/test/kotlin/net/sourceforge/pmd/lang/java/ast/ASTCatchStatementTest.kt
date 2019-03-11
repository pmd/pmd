package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import java.io.IOException


class ASTCatchStatementTest : ParserTestSpec({

    parserTest("Test crash on multicatch", javaVersions = Earliest..J1_6) {

        expectParseException("Cannot catch multiple exceptions when running in JDK inferior to 1.7 mode") {
            parseAstStatement("try { } catch (IOException | AssertionError e) { }")
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
