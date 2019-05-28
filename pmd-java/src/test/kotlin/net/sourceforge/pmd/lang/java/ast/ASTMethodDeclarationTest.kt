package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9

class ASTMethodDeclarationTest : ParserTestSpec({

    parserTest("Non-private interfaces members should be public", javaVersions = Earliest..Latest) {

        genClassHeader = "interface Bar"

        "int foo();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe true
            it::isPrivate shouldBe false
            it::isSyntacticallyPublic shouldBe false
        }

        "public int kk();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe true
            it::isPrivate shouldBe false
            it::isSyntacticallyPublic shouldBe true
        }

        "int FOO = 0;" should matchDeclaration<ASTFieldDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe true
            it::isPrivate shouldBe false
            it::isSyntacticallyPublic shouldBe false
        }

        "public int FOO = 0;" should matchDeclaration<ASTFieldDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe true
            it::isPrivate shouldBe false
            it::isSyntacticallyPublic shouldBe true
        }
    }

    parserTest("Private methods in interface should be private", J9..Latest) {

        "private int de() { return 1; }" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isPublic shouldBe false
            it::isPrivate shouldBe true
            it::isSyntacticallyPublic shouldBe false
        }

    }

    parserTest("Non-default methods in interfaces should be abstract", javaVersions = J1_8..Latest) {

        genClassHeader = "interface Bar"

        "int bar();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isDefault shouldBe false
            it::isAbstract shouldBe true
        }

        "abstract int bar();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isDefault shouldBe false
            it::isAbstract shouldBe true
        }

    }

    parserTest("Default methods in interfaces should not be abstract", javaVersions = J1_8..Latest) {

        genClassHeader = "interface Bar"

        "default int kar() { return 1; } " should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it::isDefault shouldBe true
            it::isAbstract shouldBe false
        }

        // default abstract is an invalid combination of modifiers so we won't encounter it in real analysis
    }

    parserTest("Throws list") {

        "void bar() throws IOException, java.io.Bar { }" should matchDeclaration<ASTMethodDeclaration> {
            it::isAbstract shouldBe false
            it::getMethodName shouldBe "bar"
            it::getTypeParameters shouldBe null
            it::isVoid shouldBe true

            it::getResultType shouldBe child {
                it::getTypeNode shouldBe null
                it::isVoid shouldBe true
            }

            it::getMethodDeclarator shouldBe child {
                it::getParameterCount shouldBe 0

                it::getFormalParameters shouldBe child {
                    it::getParameterCount shouldBe 0
                }
            }

            it::getThrows shouldBe child(ignoreChildren = true){} //TODO

            it::getBlock shouldBe child {}

        }
// TODO
//        "void bar() throws SomeGenericException<?> { }" should matchDeclaration<ASTMethodDeclaration> {
//            it::getResultType shouldBe child {
//                it::getTypeNode shouldBe null
//                it::isVoid shouldBe true
//            }
//
//            it::getMethodDeclarator shouldBe child {
//                it::getParameterCount shouldBe 0
//
//                it::getFormalParameters shouldBe child {
//                    it::getParameterCount shouldBe 0
//                }
//            }
//
//            it::getThrows shouldBe child {
//                child<ASTClassOrInterfaceType> {
//                    it::getTypeImage shouldBe "SomeGenericException"
//
//                    it::getTypeArguments shouldBe child {
//                        it::isDiamond shouldBe false
//
//                        child<ASTWildcardType> {
//                            it::hasLowerBound shouldBe false
//                            it::hasUpperBound shouldBe false
//                            it::getTypeBoundNode shouldBe null
//                            it::getTypeImage shouldBe "?"
//                        }
//                    }
//                }
//            }
//
//            it::getBlock shouldBe child {}
//        }
    }

    parserTest("Annotation placement") {

        "@OnDecl <T extends K> @OnType Ret bar() { return; }" should matchDeclaration<ASTMethodDeclaration> {

            annotationList {
                annotation("OnDecl")
            }

            typeParamList {
                typeParam("T") {
                    classType("K")
                }
            }

            child<ASTResultType> {
                it::isVoid shouldBe false

                classType("Ret") {
                    annotationList {
                        annotation("OnType")
                    }
                }
            }

            child<ASTMethodDeclarator>(ignoreChildren = true) {

            }

            block()
        }
    }

})
