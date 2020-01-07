/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import javasymbols.testdata.StaticNameCollision
import javasymbols.testdata.Statics
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldHaveSize
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit
import net.sourceforge.pmd.lang.java.ast.ParserTestSpec
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.classSym
import net.sourceforge.pmd.lang.java.symbols.internal.parse
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
import kotlin.streams.toList

/**
 * Tests the scopes that dominate the whole compilation unit.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class HeaderScopesTest : ParserTestSpec({

    val typesInTheSamePackage = "types in the same package"
    val javalangTypes = "types from java.lang"
    val singleTypeImports = "single-type imports"
    val staticSingleMemberImports = "static single-member imports"
    val onDemandTypeImports = "on-demand type imports"
    val onDemandStaticImports = "on-demand static imports"

    // The test data is placed in a short package to allow typing out FQCNs here for readability

    fun JSymbolTable.resolveClass(s: String): Class<*> =
            resolveTypeName(s)!!.jvmRepr!!


    fun JSymbolTable.resolveField(s: String): JFieldSymbol = resolveValueName(s).shouldBeA()
    fun JSymbolTable.resolveMethods(s: String): List<JMethodSymbol> = resolveMethodName(s).toList()

    fun ASTCompilationUnit.firstImportTable() = symbolTable.parent

    parserTest("Test same-package scope") {

        val acu = javasymbols.testdata.TestCase1::class.java.parse()

        acu.firstImportTable().shouldBeA<SamePackageSymbolTable> {
            it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java
        }
    }


    parserTest("$javalangTypes should be shadowed by $typesInTheSamePackage") {

        val acu = javasymbols.testdata.TestCase1::class.java.parse()

        acu.firstImportTable().shouldBeA<SamePackageSymbolTable> {

            it.resolveClass("Thread") shouldBe javasymbols.testdata.Thread::class.java

            it.parent.shouldBeA<JavaLangSymbolTable> {
                it.resolveClass("Thread") shouldBe java.lang.Thread::class.java
            }
        }
    }



    parserTest("$typesInTheSamePackage should be shadowed by $singleTypeImports") {

        val acu = javasymbols.testdata.deep.SomewhereElse::class.java.parse()


        acu.firstImportTable().shouldBeA<SingleImportSymbolTable> {

            it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java

            it.parent.shouldBeA<SamePackageSymbolTable> {

                it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.deep.SomeClassA::class.java
            }
        }
    }


    parserTest("$javalangTypes should be shadowed by $singleTypeImports") {

        val acu = javasymbols.testdata.deep.SomewhereElse::class.java.parse()


        acu.firstImportTable().shouldBeA<SingleImportSymbolTable> {

            it.resolveClass("Thread") shouldBe javasymbols.testdata.Thread::class.java

            it.parent.shouldBeA<SamePackageSymbolTable> {

                it.parent.shouldBeA<JavaLangSymbolTable> {

                    it.resolveClass("Thread") shouldBe java.lang.Thread::class.java

                }
            }
        }
    }


    parserTest("$onDemandTypeImports should be shadowed by everything") {

        val acu = javasymbols.testdata.deep.TypeImportsOnDemand::class.java.parse()
        // import javasymbols.testdata.*;

        acu.firstImportTable().shouldBeA<SingleImportSymbolTable> {
            // from java.lang
            it.resolveClass("Thread") shouldBe java.lang.Thread::class.java
            // from same package
            it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.deep.SomeClassA::class.java
            // from the import-on-demand
            it.resolveClass("Statics") shouldBe Statics::class.java
            // from the single type import
            it.resolveClass("TestCase1") shouldBe javasymbols.testdata.TestCase1::class.java



            it.parent.shouldBeA<SamePackageSymbolTable> {

                // shadowing javasymbols.testdata.SomeClassA
                it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.deep.SomeClassA::class.java

                it.parent.shouldBeA<JavaLangSymbolTable> {
                    // shadows javasymbols.testdata.Thread
                    it.resolveClass("Thread") shouldBe java.lang.Thread::class.java

                    it.parent.shouldBeA<ImportOnDemandSymbolTable> {
                        it.resolveClass("Thread") shouldBe javasymbols.testdata.Thread::class.java
                        it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java
                        it.resolveClass("Statics") shouldBe javasymbols.testdata.Statics::class.java
                        it.resolveClass("TestCase1") shouldBe javasymbols.testdata.TestCase1::class.java
                    }
                }
            }
        }
    }



    parserTest("$onDemandStaticImports should import only accessible members") {

        val acu = javasymbols.testdata.deep.StaticImportOnDemand::class.java.parse()
        // import javasymbols.testdata.Statics.*;


        acu.firstImportTable().shouldBeA<SamePackageSymbolTable> {

            it.resolveValueName("PUBLIC_FIELD") shouldNotBe null
            it.resolveValueName("PACKAGE_FIELD") shouldBe null
            it.resolveValueName("PRIVATE_FIELD") shouldBe null
            it.resolveValueName("PROTECTED_FIELD") shouldBe null

            it.resolveMethodName("packageMethod").shouldHaveSize(0)
            it.resolveMethodName("privateMethod").shouldHaveSize(0)
            it.resolveMethodName("protectedMethod").shouldHaveSize(0)
            it.resolveMethodName("publicMethod").shouldHaveSize(1)
            it.resolveMethodName("publicMethod2").shouldHaveSize(1)

            it.resolveTypeName("PublicStatic") shouldNotBe null
            it.resolveTypeName("PackageStatic") shouldBe null
            it.resolveTypeName("ProtectedStatic") shouldBe null
            it.resolveTypeName("PrivateStatic") shouldBe null

        }
    }

    parserTest("$onDemandStaticImports should import only static members") {

        val acu = javasymbols.testdata.deep.StaticImportOnDemand::class.java.parse()
        // import javasymbols.testdata.Statics.*;


        acu.firstImportTable().shouldBeA<SamePackageSymbolTable> {

            it.resolveValueName("PUBLIC_FIELD") shouldNotBe null
            it.resolveValueName("publicField") shouldBe null

            it.resolveMethodName("publicMethod").shouldHaveSize(1)
            it.resolveMethodName("publicInstanceMethod").shouldHaveSize(0)

            it.resolveTypeName("PublicStatic") shouldNotBe null
            it.resolveTypeName("PublicInner") shouldBe null

        }
    }

    parserTest("Types imported through $onDemandStaticImports should be shadowed by $typesInTheSamePackage") {

        val acu = javasymbols.testdata.deep.StaticImportOnDemand::class.java.parse()
        // import javasymbols.testdata.Statics.*;


        acu.firstImportTable().shouldBeA<SamePackageSymbolTable> {

            it.resolveClass("PublicShadowed") shouldBe javasymbols.testdata.deep.PublicShadowed::class.java

            it.parent.shouldBeA<JavaLangSymbolTable> {
                it.parent.shouldBeA<ImportOnDemandSymbolTable> {

                    // static type member
                    it.resolveClass("PublicShadowed") shouldBe javasymbols.testdata.Statics.PublicShadowed::class.java
                }
            }
        }
    }

    parserTest("Types imported through $onDemandStaticImports should be shadowed by $singleTypeImports") {

        val acu = javasymbols.testdata.deep.StaticIOD2::class.java.parse()
        // import javasymbols.testdata.Statics.*;


        acu.firstImportTable().shouldBeA<SingleImportSymbolTable> {

            it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java

            it.parent.shouldBeA<SamePackageSymbolTable> {
                it.parent.shouldBeA<JavaLangSymbolTable> {
                    it.parent.shouldBeA<ImportOnDemandSymbolTable> {

                        // static type member
                        it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.Statics.SomeClassA::class.java
                    }
                }
            }
        }
    }

    parserTest("$staticSingleMemberImports should import types, fields and methods with the same name") {

        val acu = javasymbols.testdata.deep.StaticCollisionImport::class.java.parse()
        // import javasymbols.testdata.Statics.*;

        acu.firstImportTable().shouldBeA<SingleImportSymbolTable> {

            it.resolveField("Ola") shouldBe classSym(StaticNameCollision::class.java)!!.getDeclaredField("Ola")!!
            it.resolveMethods("Ola") should containExactly(classSym(StaticNameCollision::class.java)!!.getDeclaredMethods("Ola").toList())
            // We can't directly use the FQCN of the Ola inner class because it's obscured by the Ola field
            it.resolveClass("Ola") shouldBe StaticNameCollision::class.nestedClasses.first().java
        }
    }

})




