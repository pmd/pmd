/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId
import net.sourceforge.pmd.lang.java.ast.ParserTestSpec
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol
import java.lang.reflect.Modifier

class VarScopingTest : ParserTestSpec({

    parserTest("Shadowing of variables") {

        val acu = parser.withProcessing().parse("""
            
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
                result::getSimpleName shouldBe "f"
                result::getModifiers shouldBe Modifier.PRIVATE

                this::getResult shouldBe outerField.symbol
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
                result::getModifiers shouldBe 0

                this::getResult shouldBe innerField.symbol
            }
        }
    }
})

private infix fun ASTMethodCall.shouldResolveToLocal(localInBlock: ASTVariableDeclaratorId): JLocalVariableSymbol =
        symbolTable.shouldResolveVarTo("f") {
            this::getContributor shouldBe localInBlock
            this::getResult shouldBe localInBlock.symbol
        }


