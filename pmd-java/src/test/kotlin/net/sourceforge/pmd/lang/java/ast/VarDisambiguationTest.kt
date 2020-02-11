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
               Foo f1;
               static class Inner {
                  static final Foo inField;
               }
               
               {
                    Foo.Inner.inField.call(); 
                    Inner.inField.call(); 
               }
            }
        """)


        doTest("Without disambig") {
            val acu = parser.withProcessing(false).parse(code)
            val (m1, m2) = acu.descendants(ASTMethodCall::class.java).toList()
            m1.qualifier!!.shouldMatchN { ambiguousName("Foo.Inner.inField") }
            m2.qualifier!!.shouldMatchN { ambiguousName("Inner.inField") }
        }

        doTest("With disambig") {
            val acu = parser.withProcessing(true).parse(code)
            val (m1, m2) = acu.descendants(ASTMethodCall::class.java).toList()
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
        }
    }
})
