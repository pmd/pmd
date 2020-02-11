package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldMatchN

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class VarDisambiguationTest : ParserTestSpec({


    parserTest("Inner class names") {
        val code = ("""
            package com.foo.bar;
            class Foo {
               static Foo f1;
               static class Inner {
                  static final Foo inField;
               }
               
               {
                    // All LHS here are ambiguous
                    Foo.Inner.inField      .call(); // m1
                    Inner.inField          .call(); // m2
                    com.foo.bar.Foo.f1.f1  .call(); // m3
                    f1                     .call(); // m4
                    Foo                    .call(); // m5
               }
            }
        """)


        doTest("Without disambig") {
            val acu = parser.withProcessing(false).parse(code)
            val (m1, m2, m3, m4, m5) = acu.descendants(ASTMethodCall::class.java).toList()
            m1.qualifier!!.shouldMatchN { ambiguousName("Foo.Inner.inField") }
            m2.qualifier!!.shouldMatchN { ambiguousName("Inner.inField") }
            m3.qualifier!!.shouldMatchN { ambiguousName("com.foo.bar.Foo.f1.f1") }
            m4.qualifier!!.shouldMatchN { ambiguousName("f1") }
            m5.qualifier!!.shouldMatchN { ambiguousName("Foo") }
        }

        doTest("With disambig") {
            val acu = parser.withProcessing(true).parse(code)
            val (m1, m2, m3, m4, m5) = acu.descendants(ASTMethodCall::class.java).toList()
            m1.qualifier!!.shouldMatchN {
                fieldAccess("inField") {
                    typeExpr {
                        classType("Inner") {
                            classType("Foo")
                        }
                    }
                }
            }
            m2.qualifier!!.shouldMatchN {
                fieldAccess("inField") {
                    typeExpr {
                        classType("Inner") {}
                    }
                }
            }
            m3.qualifier!!.shouldMatchN {
                fieldAccess("f1") {
                    fieldAccess("f1") {
                        typeExpr {
                            qualClassType("com.foo.bar.Foo")
                        }
                    }
                }
            }
            m4.qualifier!!.shouldMatchN { variableAccess("f1") }
            m5.qualifier!!.shouldMatchN {
                typeExpr {
                    classType("Foo") {}
                }
            }
        }
    }
})
