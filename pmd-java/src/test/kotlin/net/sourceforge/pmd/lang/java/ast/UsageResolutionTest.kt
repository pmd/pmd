/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.WRITE
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol

class UsageResolutionTest : ProcessorTestSpec({

    parserTest("Test usage resolution") {
        val acu = parser.parse("""
            class Bar {
                int f1;
                {
                    this.f1 = 4;
                }
            }
            class Foo extends Bar {
                int f1; // hides Bar#f1
                int f2;
                {
                    int f2 = 0;
                    super.f1 = 0;
                    f1 = 0;
                    this.f1 = 0;
                }
                {
                    int f2 = 0;
                    f2 = this.f2;
                }
            }
        """)
        val (barF1, fooF1, fooF2, localF2, localF22) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()
        barF1.usages.map { it.text.toString() }.shouldContainExactly("this.f1", "super.f1")
        fooF1.usages.map { it.text.toString() }.shouldContainExactly("f1", "this.f1")
        fooF2.usages.map { it.text.toString() }.shouldContainExactly("this.f2")
        localF2.usages.shouldBeEmpty()
        localF22.usages.shouldBeSingleton {
            it.accessType shouldBe WRITE
        }
    }

    parserTest("Test record components") {
        val acu = parser.parse("""
            record Foo(int p) {
                Foo {
                    p = 10;
                }

                void pPlus1() { return p + 1; }
            }
        """)

        val (p) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()

        p::isRecordComponent shouldBe true
        p.usages.shouldHaveSize(2)
        p.usages[0].shouldBeA<ASTVariableAccess> {
            it.referencedSym!!.shouldBeA<JFormalParamSymbol> {
                it.tryGetNode() shouldBe p
            }
        }
        p.usages[1].shouldBeA<ASTVariableAccess> {
            it.referencedSym!!.shouldBeA<JFieldSymbol> {
                it.tryGetNode() shouldBe p
            }
        }
    }

})
