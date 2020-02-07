package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.should
import io.kotlintest.shouldNot
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_PRIVATE
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_PUBLIC
import net.sourceforge.pmd.lang.java.ast.JModifier.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9

class ASTMethodDeclarationTest : ParserTestSpec({

    parserTest("Non-private interfaces members should be public", javaVersions = Earliest..Latest) {

        genClassHeader = "interface Bar"

        "int foo();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it should haveVisibility(V_PUBLIC)
            it shouldNot haveExplicitModifier(PUBLIC)
            it should haveModifier(PUBLIC)
        }

        "public int kk();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it should haveVisibility(V_PUBLIC)
            it should haveExplicitModifier(PUBLIC)
            it should haveModifier(PUBLIC)
        }

        "int FOO = 0;" should matchDeclaration<ASTFieldDeclaration>(ignoreChildren = true) {
            it should haveVisibility(V_PUBLIC)
            it shouldNot haveExplicitModifier(PUBLIC)
            it should haveModifier(PUBLIC)
        }

        "public int FOO = 0;" should matchDeclaration<ASTFieldDeclaration>(ignoreChildren = true) {
            it should haveVisibility(V_PUBLIC)
            it should haveExplicitModifier(PUBLIC)
            it should haveModifier(PUBLIC)
        }
    }

    parserTest("Private methods in interface should be private", J9..Latest) {

        "private int de() { return 1; }" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it should haveVisibility(V_PRIVATE)
            it shouldNot haveExplicitModifier(PUBLIC)
            it shouldNot haveModifier(PUBLIC)
            it should haveExplicitModifier(PRIVATE)
            it should haveModifier(PRIVATE)
        }

    }

    parserTest("Non-default methods in interfaces should be abstract", javaVersions = J1_8..Latest) {

        genClassHeader = "interface Bar"

        "int bar();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it shouldNot haveModifier(DEFAULT)
            it should haveModifier(ABSTRACT)
        }

        "abstract int bar();" should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it shouldNot haveModifier(DEFAULT)
            it should haveModifier(ABSTRACT)
        }

    }

    parserTest("Default methods in interfaces should not be abstract", javaVersions = J1_8..Latest) {

        genClassHeader = "interface Bar"

        "default int kar() { return 1; } " should matchDeclaration<ASTMethodDeclaration>(ignoreChildren = true) {
            it should haveModifier(DEFAULT)
            it shouldNot haveModifier(ABSTRACT)
        }

        // default abstract is an invalid combination of modifiers so we won't encounter it in real analysis
    }

    parserTest("Throws list") {

        "void bar() throws IOException, java.io.Bar { }" should matchDeclaration<ASTMethodDeclaration> {
            it::isAbstract shouldBe false
            it::getName shouldBe "bar"
            it::getTypeParameters shouldBe null
            it::isVoid shouldBe true
            it::getArity shouldBe 0

            it::getModifiers shouldBe modifiers {  }

            it::getResultType shouldBe voidResult()


            it::getFormalParameters shouldBe formalsList(0)

            it::getThrowsList shouldBe throwsList {
                classType("IOException")
                classType("Bar") {
                    ambiguousName("java.io")
                }
            }

            it::getBody shouldBe block()
        }
    }

    parserTest("Throws list can be annotated") {

        "void bar() throws @Oha IOException, @Aha java.io.@Oha Bar { }" should matchDeclaration<ASTMethodDeclaration> {

            it::getModifiers shouldBe modifiers {  }

            it::getResultType shouldBe voidResult()

            it::getFormalParameters shouldBe formalsList(0)

            it::getThrowsList shouldBe throwsList {
                classType("IOException") {
                    annotation("Oha")
                }

                classType("Bar") {
                    classType("io") {
                        classType("java") {
                            annotation("Aha")
                        }
                    }
                    annotation("Oha")
                }
            }

            it::getBody shouldBe block()
        }
    }

    parserTest("Varargs can be annotated") {

        "void bar(@Oha IOException @Aha ... java) { }" should matchDeclaration<ASTMethodDeclaration> {

            it::getModifiers shouldBe modifiers {  }

            it::getResultType shouldBe voidResult()

            it::getFormalParameters shouldBe formalsList(1) {
                child<ASTFormalParameter> {
                    it::getModifiers shouldBe modifiers {
                        annotation("Oha")
                    }
                    arrayType {
                        classType("IOException")
                        it::getDimensions shouldBe child {
                            varargsArrayDim {
                                annotation("Aha")
                            }
                        }
                    }

                    variableId("java")
                }
            }

            it::getBody shouldBe block()
        }


        "void bar(@Oha IOException []@O[] @Aha ... java) { }" should matchDeclaration<ASTMethodDeclaration> {

            it::getModifiers shouldBe modifiers {  }

            it::getResultType shouldBe voidResult()

            it::getFormalParameters shouldBe formalsList(1) {
                child<ASTFormalParameter> {
                    it::getModifiers shouldBe modifiers {
                        annotation("Oha")
                    }
                    arrayType {
                        classType("IOException")
                        it::getDimensions shouldBe child {
                            arrayDim { }
                            arrayDim {
                                annotation("O")
                            }
                            varargsArrayDim {
                                annotation("Aha")
                            }
                        }
                    }

                    variableId("java")
                }
            }

            it::getBody shouldBe block()
        }
    }


    parserTest("Extra dimensions can be annotated") {

        "void bar() [] @O[] { }" should matchDeclaration<ASTMethodDeclaration> {

            it::getModifiers shouldBe modifiers {  }

            it::getResultType shouldBe voidResult()

            it::getFormalParameters shouldBe formalsList(0)

            it::getExtraDimensions shouldBe child {
                arrayDim {}
                arrayDim {
                    annotation("O")
                }
            }

            it::getBody shouldBe block()
        }
    }

    parserTest("Annotation methods") {

        genClassHeader = "@interface Foo"

        inContext(EnclosedDeclarationParsingCtx) {

            "Bar bar() throws IOException;" shouldNot parse()
            "void bar();" shouldNot parse()
            "default int bar() { return 1; }" shouldNot parse()
            "int bar(Foo f);" shouldNot parse()
            "public int bar();" should parseAs {
                annotationMethod {
                    modifiers {  }

                    resultType {
                        primitiveType(PrimitiveType.INT)
                    }
                    formalsList(0)
                }
            }

            "int bar() default 2;" should parseAs {
                annotationMethod {
                    it::getModifiers shouldBe modifiers {  }

                    it::getResultType shouldBe resultType {
                        primitiveType(PrimitiveType.INT)
                    }
                    it::getFormalParameters shouldBe formalsList(0)

                    it::getDefaultClause shouldBe defaultValue { int(2) }
                }
            }

            "int bar() @NonZero [];" should parseAs {
                annotationMethod {
                    it::getModifiers shouldBe modifiers {  }
                    it::getResultType shouldBe resultType {
                        primitiveType(PrimitiveType.INT)
                    }
                    it::getFormalParameters shouldBe formalsList(0)

                    it::getDefaultClause shouldBe null
                    it::getExtraDimensions shouldBe child {
                        arrayDim {
                            annotation("NonZero")
                        }
                    }
                }
            }

            "Override bar() default @Override;" should parseAs {
                annotationMethod {
                    it::getModifiers shouldBe modifiers {  }
                    it::getResultType shouldBe resultType {
                        classType("Override")
                    }
                    it::getFormalParameters shouldBe formalsList(0)

                    it::getDefaultClause shouldBe defaultValue { annotation("Override") }
                }
            }

            "Override bar()[] default { @Override };" should parseAs {
                annotationMethod {
                    it::getModifiers shouldBe modifiers {  }
                    it::getResultType shouldBe resultType {
                        classType("Override")
                    }
                    it::getFormalParameters shouldBe formalsList(0)

                    it::getExtraDimensions shouldBe child {
                        arrayDim {}
                    }
                    it::getDefaultClause shouldBe defaultValue {
                        memberValueArray {
                            annotation("Override")
                        }
                    }
                }
            }
        }

    }

    parserTest("Receiver parameters") {

        /*
            Notice the parameterCount is 0 - receiver parameters don't affect arity.
         */

        "void bar(@A Foo this);" should matchDeclaration<ASTMethodDeclaration> {
            it::isAbstract shouldBe false
            it::getName shouldBe "bar"
            it::getTypeParameters shouldBe null
            it::isVoid shouldBe true
            // notice that arity is zero
            it::getArity shouldBe 0

            it::getModifiers shouldBe modifiers {  }

            it::getResultType shouldBe voidResult()

            it::getFormalParameters shouldBe child {
                it::getParameterCount shouldBe 0
                it::toList shouldBe emptyList()

                it::getReceiverParameter shouldBe child {
                    classType("Foo") {
                        annotation("A")
                    }
                }

            }

            it::getThrowsList shouldBe null
            it::getBody shouldBe null
        }

        "void bar(@A Foo this, int other);" should matchDeclaration<ASTMethodDeclaration> {
            it::isAbstract shouldBe false
            it::getName shouldBe "bar"
            it::getTypeParameters shouldBe null
            it::isVoid shouldBe true
            it::getArity shouldBe 1

            it::getModifiers shouldBe modifiers {  }

            it::getResultType shouldBe voidResult()

            it::getFormalParameters shouldBe child {
                it::getParameterCount shouldBe 1

                it::getReceiverParameter shouldBe child {
                    classType("Foo") {
                        annotation("A")
                    }
                }

                it::toList shouldBe listOf(
                        child {
                            localVarModifiers {  }
                            primitiveType(PrimitiveType.INT)
                            variableId("other")
                        }
                )


            }

            it::getThrowsList shouldBe null
            it::getBody shouldBe null
        }
    }

    parserTest("Annotation placement") {

        "@OnDecl <T extends K> @OnType Ret bar() { return; }" should matchDeclaration<ASTMethodDeclaration> {

            it::getName shouldBe "bar"

            it::getModifiers shouldBe modifiers {
                annotation("OnDecl")
            }

            it::getTypeParameters shouldBe typeParamList {
                typeParam("T") {
                    classType("K")
                }
            }

            it::getResultType shouldBe resultType {
                classType("Ret") {
                    annotation("OnType")
                }
            }

            formalsList(0)
            block()
        }
    }
})
