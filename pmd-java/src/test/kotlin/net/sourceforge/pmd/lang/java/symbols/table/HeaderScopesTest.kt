package net.sourceforge.pmd.lang.java.symbols.table

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import javasymbols.testdata.Statics
import net.sourceforge.pmd.lang.java.ast.parserTest
import net.sourceforge.pmd.lang.java.symbols.*
import net.sourceforge.pmd.lang.java.symbols.refs.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.refs.JResolvableClassDeclarationSymbol
import net.sourceforge.pmd.lang.java.symbols.table.internal.ImportOnDemandSymbolTable
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaLangSymbolTable
import net.sourceforge.pmd.lang.java.symbols.table.internal.SamePackageSymbolTable
import net.sourceforge.pmd.lang.java.symbols.table.internal.SingleImportSymbolTable


/**
 * Tests the scopes that dominate the whole compilation unit.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class HeaderScopesTest : FunSpec({

    val typesInTheSamePackage = "types in the same package"
    val javalangTypes = "types from java.lang"
    val singleTypeImports = "single-type imports"
    val onDemandTypeImports = "on-demand type imports"
    val onDemandStaticImports = "on-demand static imports"

    // The test data is placed in a short package to allow typing out FQCNs here for readability

    // expects a symbolic type
    fun JSymbolTable.resolveClass(s: String): Class<*> =
            resolveTypeName(s)
                    .shouldBePresent()
                    .shouldBeA<JResolvableClassDeclarationSymbol>()
                    .loadClass()
                    .shouldBePresent()
                    .classObject


    fun JSymbolTable.resolveField(s: String): JFieldSymbol =
            resolveValueName(s)
                    .shouldBePresent()
                    .shouldBeA()



    parserTest("Test same-package scope") {

        val acu = javasymbols.testdata.TestCase1::class.java.parse()

        acu.symbolTable.shouldBeA<SamePackageSymbolTable> {
            it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java
        }
    }


    parserTest("$javalangTypes should be shadowed by $typesInTheSamePackage") {

        val acu = javasymbols.testdata.TestCase1::class.java.parse()

        acu.symbolTable.shouldBeA<SamePackageSymbolTable> {

            it.resolveClass("Thread") shouldBe javasymbols.testdata.Thread::class.java

            it.parent.shouldBeA<JavaLangSymbolTable> {
                it.resolveClass("Thread") shouldBe java.lang.Thread::class.java
            }
        }
    }



    parserTest("$typesInTheSamePackage should be shadowed by $singleTypeImports") {

        val acu = javasymbols.testdata.deep.SomewhereElse::class.java.parse()


        acu.symbolTable.shouldBeA<SingleImportSymbolTable> {

            it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java

            it.parent.shouldBeA<SamePackageSymbolTable> {

                it.resolveClass("SomeClassA") shouldBe javasymbols.testdata.deep.SomeClassA::class.java
            }
        }
    }


    parserTest("$javalangTypes should be shadowed by $singleTypeImports") {

        val acu = javasymbols.testdata.deep.SomewhereElse::class.java.parse()


        acu.symbolTable.shouldBeA<SingleImportSymbolTable> {

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

        acu.symbolTable.shouldBeA<SingleImportSymbolTable> {
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


        acu.symbolTable.shouldBeA<SamePackageSymbolTable> {

            it.resolveValueName("PUBLIC_FIELD").shouldBePresent()
            it.resolveValueName("PACKAGE_FIELD").shouldBeEmpty()
            it.resolveValueName("PRIVATE_FIELD").shouldBeEmpty()
            it.resolveValueName("PROTECTED_FIELD").shouldBeEmpty()

            it.resolveMethodName("packageMethod").shouldBeEmpty()
            it.resolveMethodName("privateMethod").shouldBeEmpty()
            it.resolveMethodName("protectedMethod").shouldBeEmpty()
            it.resolveMethodName("publicMethod").shouldHaveSize(1)
            it.resolveMethodName("publicMethod2").shouldHaveSize(1)

            it.resolveTypeName("PublicStatic").shouldBePresent()
            it.resolveTypeName("PackageStatic").shouldBeEmpty()
            it.resolveTypeName("ProtectedStatic").shouldBeEmpty()
            it.resolveTypeName("PrivateStatic").shouldBeEmpty()

        }
    }

    parserTest("$onDemandStaticImports should import only static members") {

        val acu = javasymbols.testdata.deep.StaticImportOnDemand::class.java.parse()
        // import javasymbols.testdata.Statics.*;


        acu.symbolTable.shouldBeA<SamePackageSymbolTable> {

            it.resolveValueName("PUBLIC_FIELD").shouldBePresent()
            it.resolveValueName("publicField").shouldBeEmpty()

            it.resolveMethodName("publicMethod").shouldHaveSize(1)
            it.resolveMethodName("publicInstanceMethod").shouldBeEmpty()

            it.resolveTypeName("PublicStatic").shouldBePresent()
            it.resolveTypeName("PublicInner").shouldBeEmpty()

        }
    }

    parserTest("Types imported through $onDemandStaticImports should be shadowed by $typesInTheSamePackage") {

        val acu = javasymbols.testdata.deep.StaticImportOnDemand::class.java.parse()
        // import javasymbols.testdata.Statics.*;


        acu.symbolTable.shouldBeA<SamePackageSymbolTable> {

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


        acu.symbolTable.shouldBeA<SingleImportSymbolTable> {

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

})




