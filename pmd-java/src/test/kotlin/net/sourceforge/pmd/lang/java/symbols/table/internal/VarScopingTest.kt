/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol
import java.lang.reflect.Modifier

class VarScopingTest : ParserTestSpec({

    parserTest("Shadowing of variables") {

        val acu = parser.parse("""

            // TODO test with static import, currently there are no "unresolved field" symbols

            class Outer extends Sup {
                private T f;

                {
                    f.foo(); // outerField

                    for (T f : someList) {
                       f.foo(); // foreachParam
                    }
                }

                void method(T f) {
                    f.foo(); // methodParam

                    {
                        T f;
                        f.foo(); // localInBlock
                    }
                }

                class Inner {
                    T f;

                    {
                        f.foo(); // innerField
                    }
                }
            }
        """.trimIndent())

        val (outerClass, innerClass) =
                acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList()

        val (outerField, foreachParam, methodParam, localInBlock, innerField) =
                acu.descendants(ASTVariableDeclaratorId::class.java).toList()

        val (inInitializer, inForeach, inMethod, inLocalBlock, inInnerClass) =
                acu.descendants(ASTMethodCall::class.java).toList()


        doTest("Inside outer initializer: f is outerField") {
            inInitializer.symbolTable.shouldResolveVarTo<JFieldSymbol>("f") {
                this::getContributor shouldBe outerClass
                this.result.shouldBeA<JFieldSymbol> {
                    it::getSimpleName shouldBe "f"
                    it::getModifiers shouldBe Modifier.PRIVATE
                    it shouldBe outerField.symbol
                }
            }
        }

        doTest("Inside foreach: f is foreachParam") {
            val sym = inForeach shouldResolveToLocal foreachParam
            sym.shouldBeA<JLocalVariableSymbol>()

        }

        doTest("Inside method body: f is methodParam") {
            val sym = inMethod shouldResolveToLocal methodParam
            sym.shouldBeA<JFormalParamSymbol>()
        }

        doTest("Inside local block: f is local var") {
            val sym = inLocalBlock shouldResolveToLocal localInBlock
            sym.shouldBeA<JLocalVariableSymbol>()
        }

        doTest("Inside inner class: f is inner field") {
            inInnerClass.symbolTable.shouldResolveVarTo<JFieldSymbol>("f") {
                this::getContributor shouldBe innerClass
            }.also {
                it::getModifiers shouldBe 0
                it shouldBe innerField.symbol
            }
        }
    }


    parserTest("Try statement") {

        val acu = parser.withProcessing().parse("""

            // TODO test with static import, currently there are no "unresolved field" symbols

            class Outer extends Sup {
                private Reader r; // outerField

                {
                    try {

                    } catch (Exception e) { // exception1
                      e.printStackTrace(); // inCatch1
                    }

                    try (Reader r = new StringReader("k")) { // reader1
                      r.read();  // inTry: resource
                    } catch (Exception e) { // exception2
                      r.close(); // inCatch2: field
                    } finally {
                      r.close(); // inFinally: field
                    }

                    try (Reader r = new StringReader("k"); // reader2
                         BufferedReader br = r.buffered()) { // bufferedReader, inResource

                    }
                }
            }
        """.trimIndent())

        val (outerField, exception1, reader1, exception2, reader2, bufferedReader) =
                acu.descendants(ASTVariableDeclaratorId::class.java).toList()

        val (inCatch1, inTry, inCatch2, inFinally, inResource) =
                acu.descendants(ASTMethodCall::class.java).toList()

        infix fun JavaNode.shouldResolveToField(fieldId: ASTVariableDeclaratorId): JFieldSymbol =
                symbolTable.shouldResolveVarTo(fieldId.variableName) {
                    this::getResult shouldBe fieldId.symbol
                }


        doTest("Inside catch clause: catch param is in scope") {
            inCatch1 shouldResolveToLocal exception1
        }

        doTest("Inside try body: r is the resource") {
            inTry shouldResolveToLocal reader1
        }

        doTest("Inside catch 2: r is the field (resource not in scope)") {
            inCatch2 shouldResolveToField outerField
        }

        doTest("Inside finally: r is the field (resource not in scope)") {
            inFinally shouldResolveToField outerField
        }

        doTest("Inside resource declaration: r is the resource of the same resource list") {
            inResource shouldResolveToLocal reader2
        }
    }

    parserTest("Switch statement") {

        val acu = parser.withProcessing().parse("""

            // TODO test with static import, currently there are no "unresolved field" symbols

            class Outer extends Sup {
                private int i; // outerField

                {
                    switch (i) { // fAccess
                    case 4:
                        int i = 0; // ivar
                        return i + 1; // iAccess
                    case 3:
                        int k = 0; // kvar
                        return i + 1; // iAccess2
                    }
                }
            }
        """.trimIndent())

        val (outerField, ivar) =
                acu.descendants(ASTVariableDeclaratorId::class.java).toList()

        val (fAccess, iAccess, iAccess2) =
                acu.descendants(ASTVariableAccess::class.java).toList()

        infix fun JavaNode.shouldResolveToField(fieldId: ASTVariableDeclaratorId): JFieldSymbol =
                symbolTable.shouldResolveVarTo(fieldId.variableName) {
                    this::getResult shouldBe fieldId.symbol
                }

        doTest("Inside tested expr: vars are not in scope") {
            fAccess shouldResolveToField outerField
        }

        doTest("Inside label: var is in scope") {
            iAccess shouldResolveToLocal ivar
        }

        doTest("Inside fallthrough: var is in scope") {
            // this is suprising but legal, the var is just not definitely
            // assigned at the point of use
            iAccess2 shouldResolveToLocal ivar
        }
    }
})

private infix fun JavaNode.shouldResolveToLocal(localId: ASTVariableDeclaratorId): JLocalVariableSymbol =
        symbolTable.shouldResolveVarTo(localId.variableName) {
            this::getContributor shouldBe localId
            this::getResult shouldBe localId.symbol
        }
