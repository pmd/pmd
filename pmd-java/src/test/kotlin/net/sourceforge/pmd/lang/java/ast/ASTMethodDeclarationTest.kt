/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_PRIVATE
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.V_PUBLIC
import net.sourceforge.pmd.lang.java.ast.JModifier.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT

class ASTMethodDeclarationTest : ParserTestSpec({

    parserTest("Non-private interfaces members should be public", javaVersions = Earliest..Latest) {

        genClassHeader = "interface Bar"

        inContext(TypeBodyParsingCtx) {

            "int foo();" should parseAs {
                methodDecl {
                    it should haveVisibility(V_PUBLIC)
                    it shouldNot haveExplicitModifier(PUBLIC)
                    it should haveModifier(PUBLIC)

                    modifiers {}
                    unspecifiedChildren(2)
                }
            }

            "public int kk();" should parseAs {
                methodDecl {
                    it should haveVisibility(V_PUBLIC)
                    it should haveExplicitModifier(PUBLIC)
                    it should haveModifier(PUBLIC)

                    modifiers {}
                    unspecifiedChildren(2)
                }
            }

            "int FOO = 0;" should parseAs {
                fieldDecl {
                    it should haveVisibility(V_PUBLIC)
                    it shouldNot haveExplicitModifier(PUBLIC)
                    it should haveModifier(PUBLIC)

                    modifiers { }
                    unspecifiedChildren(2)
                }
            }

            "public int FOO = 0;" should parseAs {
                fieldDecl {
                    it should haveVisibility(V_PUBLIC)
                    it should haveExplicitModifier(PUBLIC)
                    it should haveModifier(PUBLIC)

                    modifiers { }
                    unspecifiedChildren(2)
                }
            }
        }
    }

    parserTest("Private methods in interface should be private", J9..Latest) {

        inContext(TypeBodyParsingCtx) {
            "private int de() { return 1; }" should parseAs {
                methodDecl {
                    it should haveVisibility(V_PRIVATE)
                    it shouldNot haveExplicitModifier(PUBLIC)
                    it shouldNot haveModifier(PUBLIC)
                    it should haveExplicitModifier(PRIVATE)
                    it should haveModifier(PRIVATE)


                    unspecifiedChildren(4)
                }
            }
        }
    }

    parserTest("Non-default methods in interfaces should be abstract", javaVersions = J1_8..Latest) {

        genClassHeader = "interface Bar"

        inContext(TypeBodyParsingCtx) {

            "int bar();" should parseAs {
                methodDecl {
                    it shouldNot haveModifier(DEFAULT)
                    it should haveModifier(ABSTRACT)


                    unspecifiedChildren(3)
                }
            }

            "abstract int bar();" should parseAs {
                methodDecl {
                    it shouldNot haveModifier(DEFAULT)
                    it should haveModifier(ABSTRACT)


                    unspecifiedChildren(3)
                }
            }

        }
    }

    parserTest("Default methods in interfaces should not be abstract", javaVersions = J1_8..Latest) {

        genClassHeader = "interface Bar"

        inContext(TypeBodyParsingCtx) {
            "default int kar() { return 1; } " should parseAs {
                methodDecl {
                    it should haveModifier(DEFAULT)
                    it shouldNot haveModifier(ABSTRACT)


                    unspecifiedChildren(4)
                }
            }
        }

        // default abstract is an invalid combination of modifiers so we won't encounter it in real analysis
    }

    parserTest("Throws list") {

        inContext(TypeBodyParsingCtx) {

            "void bar() throws IOException, java.io.Bar { }" should parseAs {
                methodDecl {
                    it::isAbstract shouldBe false
                    it::getName shouldBe "bar"
                    it::getTypeParameters shouldBe null
                    it::isVoid shouldBe true
                    it::getArity shouldBe 0

                    it::getModifiers shouldBe modifiers { }

                    it::getResultTypeNode shouldBe voidType()


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
        }
    }

    parserTest("Throws list can be annotated") {

        inContext(TypeBodyParsingCtx) {

            "void bar() throws @Oha IOException, @Aha java.io.@Oha Bar { }" should parseAs {
                methodDecl {

                    it::getModifiers shouldBe modifiers { }

                    it::getResultTypeNode shouldBe voidType()

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
        }
    }

    parserTest("Varargs can be annotated") {

        inContext(TypeBodyParsingCtx) {
            "void bar(@Oha IOException @Aha ... java) { }" should parseAs {

                methodDecl {

                    it::getModifiers shouldBe modifiers { }

                    it::getResultTypeNode shouldBe voidType()

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
            }


            "void bar(@Oha IOException []@O[] @Aha ... java) { }" should parseAs {

                methodDecl {

                    it::getModifiers shouldBe modifiers { }

                    it::getResultTypeNode shouldBe voidType()

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

                    it::getThrowsList shouldBe null
                    it::getBody shouldBe block()
                }
            }
        }
    }

    parserTest("Extra dimensions can be annotated") {

        inContext(TypeBodyParsingCtx) {
            "void bar() [] @O[] { }" should parseAs {

                methodDecl {

                    it::getModifiers shouldBe modifiers { }

                    it::getResultTypeNode shouldBe voidType()

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
        }
    }

    parserTest("Annotation methods") {

        genClassHeader = "@interface Foo"

        inContext(TypeBodyParsingCtx) {

            "Bar bar() throws IOException;" shouldNot parse()
            "void bar();" shouldNot parse()
            "default int bar() { return 1; }" shouldNot parse()
            "int bar(Foo f);" shouldNot parse()
            "public int bar();" should parseAs {
                annotationMethod {
                    modifiers { }

                    it::getResultTypeNode shouldBe primitiveType(INT)
                    formalsList(0)
                }
            }
            "int bar() default 2;" should parseAs {
                annotationMethod {
                    it::getModifiers shouldBe modifiers { }

                    it::getResultTypeNode shouldBe primitiveType(INT)
                    it::getFormalParameters shouldBe formalsList(0)

                    it::getDefaultClause shouldBe defaultValue { int(2) }
                }
            }

            "int bar() @NonZero [];" should parseAs {
                annotationMethod {
                    it::getModifiers shouldBe modifiers { }
                    it::getResultTypeNode shouldBe primitiveType(INT)
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
                    it::getModifiers shouldBe modifiers { }
                    it::getResultTypeNode shouldBe classType("Override")

                    it::getFormalParameters shouldBe formalsList(0)

                    it::getDefaultClause shouldBe defaultValue { annotation("Override") }
                }
            }

            "Override bar()[] default { @Override };" should parseAs {
                annotationMethod {
                    it::getModifiers shouldBe modifiers { }
                    it::getResultTypeNode shouldBe classType("Override")
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

        inContext(TypeBodyParsingCtx) {
            "void bar(@A Foo this);" should parseAs {
                methodDecl {
                    it::isAbstract shouldBe false
                    it::getName shouldBe "bar"
                    it::getTypeParameters shouldBe null
                    it::isVoid shouldBe true
                    // notice that arity is zero
                    it::getArity shouldBe 0

                    it::getModifiers shouldBe modifiers { }

                    it::getResultTypeNode shouldBe voidType()

                    it::getFormalParameters shouldBe formalsList(0) {
                        it.toList() shouldBe emptyList()

                        it::getReceiverParameter shouldBe child {
                            classType("Foo") {
                                annotation("A")
                            }
                        }

                    }

                    it::getThrowsList shouldBe null
                    it::getBody shouldBe null
                }
            }

            "void bar(@A Foo this, int other);" should parseAs {
                methodDecl {
                    it::isAbstract shouldBe false
                    it::getName shouldBe "bar"
                    it::getTypeParameters shouldBe null
                    it::isVoid shouldBe true
                    it::getArity shouldBe 1

                    it::getModifiers shouldBe modifiers { }

                    it::getResultTypeNode shouldBe voidType()

                    it::getFormalParameters shouldBe formalsList(1) {

                        it::getReceiverParameter shouldBe child {
                            classType("Foo") {
                                annotation("A")
                            }
                        }

                    it.toList() shouldBe listOf(
                            child {
                                localVarModifiers {  }
                            primitiveType(INT)
                                variableId("other")
                            }
                    )


            }

                    it::getThrowsList shouldBe null
                    it::getBody shouldBe null
                }
            }
        }
    }

    parserTest("Annotation placement") {
        inContext(TypeBodyParsingCtx) {

            "@OnDecl <T extends K> @OnType Ret bar() { return; }" should parseAs {
                methodDecl {

                    it::getName shouldBe "bar"

                    it::getModifiers shouldBe modifiers {
                        annotation("OnDecl")
                    }

                    it::getTypeParameters shouldBe typeParamList {
                        typeParam("T") {
                            classType("K")
                        }
                    }

                    it::getResultTypeNode shouldBe classType("Ret") {
                        annotation("OnType")
                    }


                    formalsList(0)
                    block()
                }
            }
        }
    }
})
