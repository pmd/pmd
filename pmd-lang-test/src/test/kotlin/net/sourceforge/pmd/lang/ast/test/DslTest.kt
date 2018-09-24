package net.sourceforge.pmd.lang.ast.test

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.java.ast.*


class DslTest : FunSpec({

    failureTest("Empty matcher spec should check the number of children",
            messageContains = setOf("Wrong", "number", "children", "expected 0", "actual 2")) {

        parseStatement("int i = 0;") should matchNode<ASTLocalVariableDeclaration> {}
    }

    test("Matcher with ignoreChildren should not check the number of children") {

        parseStatement("int i = 0;") should matchNode<ASTLocalVariableDeclaration>(ignoreChildren = true) {}
    }

    failureTest("Incorrect node type should cause failure",
            messageContains = setOf("Expression", "actual LocalVariableDeclaration")) {
        parseStatement("int i = 0;") should matchNode<ASTExpression>(ignoreChildren = true) {}
    }

    failureTest("Specifying any child in a pattern should cause the number of children to be checked",
            messageContains = setOf("number", "children", "expected 1", "actual 2")) {

        parseStatement("int i = 0;") should matchNode<ASTLocalVariableDeclaration> {
            child<ASTType>(ignoreChildren = true) {}
            // There's a VarDeclarator
        }
    }


    test("Unspecified children should shift the next child matchers") {
        parseStatement("int i = 0;") should matchNode<ASTLocalVariableDeclaration> {
            unspecifiedChild()
            child<ASTVariableDeclarator>(ignoreChildren = true) {}
        }
    }

    test("Unspecified children should count in total number of children") {
        parseStatement("int i = 0;") should matchNode<ASTLocalVariableDeclaration> {
            unspecifiedChildren(2)
        }
    }

    failureTest("Unspecified children should be counted in the number of expected children",
            messageContains = setOf("#2 doesn't exist")) {

        parseStatement("int i = 0;") should matchNode<ASTLocalVariableDeclaration> {
            unspecifiedChildren(3)
        }
    }

    failureTest("Assertions are always executed in order",
            messageContains = setOf("PrimitiveType")) {

        parseStatement("int[] i = 0;") should matchNode<ASTLocalVariableDeclaration> {

            child<ASTType> {

                // Here we check that the child type check fails before the assertion
                child<ASTPrimitiveType> {}

                it.typeImage shouldBe "bratwurst"

            }

            unspecifiedChild()
        }
    }

    failureTest("Assertions are always executed in order #2",
            messageContains = setOf("bratwurst")) {

        parseStatement("int[] i = 0;") should matchNode<ASTLocalVariableDeclaration> {

            child<ASTType> {

                it.typeImage shouldBe "bratwurst"

                child<ASTPrimitiveType> {}

            }

            unspecifiedChild()
        }
    }

    failureTest("All assertions should have a node path",
            messageContains = setOf("At /LocalVariableDeclaration/Type:", "expected: \"bratwurst\"")) {

        parseStatement("int[] i = 0;") should matchNode<ASTLocalVariableDeclaration> {

            child<ASTType> {

                // this fails
                it.typeImage shouldBe "bratwurst"

            }

            unspecifiedChild()
        }
    }

    failureTest("Child assertions should have a node path",
            messageContains = setOf("At /LocalVariableDeclaration/Type:", "expected", "type", "LambdaExpression")) {

        parseStatement("int[] i = 0;") should matchNode<ASTLocalVariableDeclaration> {

            child<ASTType> {

                // this fails
                child<ASTLambdaExpression> { }
            }

            unspecifiedChild()
        }
    }

    failureTest("Leaf nodes should assert that they have no children",
            messageContains = setOf("number", "children", "expected 0")) {

        parseStatement("int[] i = 0;") should matchNode<ASTLocalVariableDeclaration> {

            child<ASTType> {} // This should fail
            unspecifiedChild()
        }
    }


})



