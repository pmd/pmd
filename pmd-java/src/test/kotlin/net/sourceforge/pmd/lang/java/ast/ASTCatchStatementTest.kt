package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_5
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_7
import java.io.IOException


class ASTCatchStatementTest : FunSpec({


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

                val catchNode = it

                child<ASTFormalParameter> {
                    val ioe = child<ASTType>(ignoreChildren = true) {
                        it.type shouldBe IOException::class.java
                    }

                    val aerr = child<ASTType>(ignoreChildren = true) {
                        it.type shouldBe java.lang.AssertionError::class.java
                    }

                    catchNode.caughtExceptionTypeNodes.shouldContainExactly(ioe, aerr)
                    catchNode.caughtExceptionTypes.shouldContainExactly(catchNode.caughtExceptionTypeNodes.map { it.type })

                    child<ASTVariableDeclaratorId> { }
                }

                child<ASTBlock> { }
            }
        }

    }


})