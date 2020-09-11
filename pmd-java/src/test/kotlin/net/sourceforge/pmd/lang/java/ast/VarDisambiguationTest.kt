/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger

class VarDisambiguationTest : ParserTestSpec({


    parserTest("AmbiguousName reclassification") {
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

    parserTest("Disambiguation bug") {
        val code = ("""
            class Foo {
               protected void setBandIndexes() {
                    for (Object[] need : needPredefIndex) {
                        CPRefBand b     = (CPRefBand) need[0];
                        Byte      which = (Byte)      need[1];
                        b.setIndex(getCPIndex(which.byteValue()));
                    }
                }
            }
        """)


        doTest("Without disambig") {
            val acu = parser.withProcessing(false).parse(code)
            val (setIndex) = acu.descendants(ASTMethodCall::class.java).toList()
            setIndex.qualifier!!.shouldMatchN { ambiguousName("b") }
        }

        doTest("With disambig") {
            val acu = parser.withProcessing(true).parse(code)
            val (setIndex) = acu.descendants(ASTMethodCall::class.java).toList()
            setIndex.qualifier!!.shouldMatchN { variableAccess("b") }
        }
    }

    parserTest("Failure cases") {
        val code = ("""
package com.foo.bar;
class Foo<T> {
   static Foo f1;
   static class Inner {
      static final Foo inField;
   }

   {
        Foo.Inner.noField      .call();
               // ^^^^^^^
        Foo.Inner.noField.next .call();
               // ^^^^^^^^^^^^

        // T is a type var

        T.fofo                 .call();
       // ^^^^
        T.Fofo v;
       // ^^^^

   }
}
        """)

        fun JavaParsingHelper.TestCheckLogger.getWarning(key: String, idx: Int, testCode: (JavaNode, List<String>) -> Unit) {
            val (node, args) = warnings[key]!![idx]
            testCode(node, args.map { it.toString() })
        }

        // Hmm, since shouldMatchN looks into the children of the parent, what it sees here
        // are the new nodes, but the reported node is the original AmbiguousName (pruned from the tree)

        // This won't matter when we replace nodes with a position for reporting

        val logger = enableProcessing(true)
        parser.parse(code)

        doTest("Unresolved field") {

            logger.getWarning(SemanticChecksLogger.CANNOT_RESOLVE_MEMBER, 0) { node, args ->

                args shouldBe listOf("noField", "com.foo.bar.Foo.Inner", "a field access")
                node.shouldMatchN {
                    fieldAccess("noField") {
                        typeExpr {
                            classType("Inner") {
                                classType("Foo") {}
                            }
                        }
                    }
                }
            }
        }

        doTest("Unresolved field chain") {

            logger.getWarning(SemanticChecksLogger.CANNOT_RESOLVE_MEMBER, 1) { node, args ->

                args shouldBe listOf("noField", "com.foo.bar.Foo.Inner", "a field access")
                node.shouldMatchN {
                    fieldAccess("next") { // todo should actually be reported on noField
                        fieldAccess("noField") {
                            typeExpr {
                                classType("Inner") {
                                    classType("Foo") {}
                                }
                            }
                        }
                    }
                }
            }
        }

        doTest("Unresolved type var member") {

            logger.getWarning(SemanticChecksLogger.CANNOT_RESOLVE_MEMBER, 2) { node, args ->

                args shouldBe listOf("fofo", "type variable T", "a field access")
                node.shouldMatchN {
                    fieldAccess("fofo") {
                        typeExpr {
                            classType("T") {
                                it.referencedSym.shouldBeA<JTypeParameterSymbol> { }
                            }
                        }
                    }
                }
            }
        }

        doTest("Unresolved type var member (in type ctx)") {

            logger.getWarning(SemanticChecksLogger.CANNOT_RESOLVE_MEMBER, 3) { node, args ->

                args shouldBe listOf("Fofo", "type variable T", "an unresolved type")
                node.shouldMatchN {
                    classType("Fofo") {
                        it.referencedSym.shouldBeA<JClassSymbol> {
                            it::isUnresolved shouldBe true
                        }
                        classType("T") {
                            it.referencedSym.shouldBeA<JTypeParameterSymbol> { }
                        }
                    }
                }
            }
        }
    }
})
