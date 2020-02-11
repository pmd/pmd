/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.ast.classType
import net.sourceforge.pmd.lang.java.ast.typeArgList

/**
 * @author Clément Fournier
 */
class TypesFromAstTest : ProcessorTestSpec({


    parserTest("Test primitive types are reused") {

        val (tf1, tf2) =
                parser.parse("""
                    package java.util;

                    class Foo<K> {
                              
                      class Inner<T> {
                      
                        Foo<K>.Inner<T> f1;
                        /*Foo<K>.*/Inner<T> f2;
                      }

                    }
                """)
                        .descendants(ASTFieldDeclaration::class.java)
                        .crossFindBoundaries()
                        .map { it.typeNode }
                        .toList()

        tf1.shouldMatchN {

            classType("Inner") {

                it.typeMirror.toString() shouldBe "java.util.Foo<K>#Inner<T>"

                classType("Foo") {

                    it.typeMirror.toString() shouldBe "java.util.Foo<K>"

                    typeArgList {
                        classType("K")
                    }
                }

                typeArgList {
                    classType("T")
                }
            }
        }

        tf2.shouldMatchN {

            classType("Inner") {

                it.typeMirror.toString() shouldBe "java.util.Foo<K>#Inner<T>"

                typeArgList {
                    classType("T")
                }
            }
        }
    }

})
