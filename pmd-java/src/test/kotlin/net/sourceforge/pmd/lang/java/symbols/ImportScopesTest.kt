package net.sourceforge.pmd.lang.java.symbols

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import javasymbols.testdata.Statics
import net.sourceforge.pmd.lang.java.ast.parserTest
import net.sourceforge.pmd.lang.java.symbols.refs.JFieldReference
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.ImportOnDemandScope
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.JavaLangScope
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SamePackageScope
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SingleImportScope


/**
 * Tests the higher levels of the scope tree.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ImportScopesTest : FunSpec({

    val typesInTheSamePackage = "types in the same package"
    val javalangTypes = "types from java.lang"
    val singleTypeImports = "single-type imports"
    val onDemandTypeImports = "on-demand type imports"
    val onDemandStaticImports = "on-demand static imports"


    // expects a symbolic type
    fun JScope.resolveSymbolic(s: String): Class<*> =
            resolveTypeName(s)
                    .shouldBePresent()
                    .shouldBeA<JSymbolicClassReference>()
                    .loadClass()
                    .shouldBePresent()
                    .classObject


    fun JScope.resolveField(s: String): JFieldReference =
            resolveValueName(s)
                    .shouldBePresent()
                    .shouldBeA()



    parserTest("Test same-package scope") {

        val acu = javasymbols.testdata.TestCase1::class.java.parse()

        acu.symbolTable.shouldBeA<SamePackageScope> {
            it.resolveSymbolic("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java
        }
    }


    parserTest("$javalangTypes should be shadowed by $typesInTheSamePackage") {

        val acu = javasymbols.testdata.TestCase1::class.java.parse()

        acu.symbolTable.shouldBeA<SamePackageScope> {

            it.resolveSymbolic("Thread") shouldBe javasymbols.testdata.Thread::class.java

            it.parent.shouldBeA<JavaLangScope> {
                it.resolveSymbolic("Thread") shouldBe java.lang.Thread::class.java
            }
        }
    }



    parserTest("$typesInTheSamePackage should be shadowed by $singleTypeImports") {

        val acu = javasymbols.testdata.deep.SomewhereElse::class.java.parse()


        acu.symbolTable.shouldBeA<SingleImportScope> {

            it.resolveSymbolic("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java

            it.parent.shouldBeA<SamePackageScope> {

                it.resolveSymbolic("SomeClassA") shouldBe javasymbols.testdata.deep.SomeClassA::class.java
            }
        }
    }


    parserTest("$javalangTypes should be shadowed by $singleTypeImports") {

        val acu = javasymbols.testdata.deep.SomewhereElse::class.java.parse()


        acu.symbolTable.shouldBeA<SingleImportScope> {

            it.resolveSymbolic("Thread") shouldBe javasymbols.testdata.Thread::class.java

            it.parent.shouldBeA<SamePackageScope> {

                it.parent.shouldBeA<JavaLangScope> {

                    it.resolveSymbolic("Thread") shouldBe java.lang.Thread::class.java

                }
            }
        }
    }


    parserTest("$onDemandTypeImports should be shadowed by everything") {

        val acu = javasymbols.testdata.deep.TypeImportsOnDemand::class.java.parse()
        // import javasymbols.testdata.*;

        acu.symbolTable.shouldBeA<SingleImportScope> {
            // from java.lang
            it.resolveSymbolic("Thread") shouldBe java.lang.Thread::class.java
            // from same package
            it.resolveSymbolic("SomeClassA") shouldBe javasymbols.testdata.deep.SomeClassA::class.java
            // from the import-on-demand
            it.resolveSymbolic("Statics") shouldBe Statics::class.java
            // from the single type import
            it.resolveSymbolic("TestCase1") shouldBe javasymbols.testdata.TestCase1::class.java



            it.parent.shouldBeA<SamePackageScope> {

                // shadowing javasymbols.testdata.SomeClassA
                it.resolveSymbolic("SomeClassA") shouldBe javasymbols.testdata.deep.SomeClassA::class.java

                it.parent.shouldBeA<ImportOnDemandScope> {
                    // from java.lang instead of importing javasymbols.testdata.Thread
                    it.resolveSymbolic("Thread") shouldBe java.lang.Thread::class.java
                    // from the import-on-demand
                    it.resolveSymbolic("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java
                    // from the import-on-demand
                    it.resolveSymbolic("Statics") shouldBe Statics::class.java
                    // from the import-on-demand
                    it.resolveSymbolic("TestCase1") shouldBe javasymbols.testdata.TestCase1::class.java

                    it.parent.shouldBeA<JavaLangScope> {
                        it.resolveSymbolic("Thread") shouldBe java.lang.Thread::class.java
                    }
                }
            }
        }
    }



    parserTest("$onDemandStaticImports should import only accessible members") {

        val acu = javasymbols.testdata.deep.StaticImportOnDemand::class.java.parse()
        // import javasymbols.testdata.Statics.*;


        acu.symbolTable.shouldBeA<SamePackageScope> {

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


        acu.symbolTable.shouldBeA<SamePackageScope> {

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


        acu.symbolTable.shouldBeA<SamePackageScope> {

            it.resolveSymbolic("PublicShadowed") shouldBe javasymbols.testdata.deep.PublicShadowed::class.java

            it.parent.shouldBeA<ImportOnDemandScope> {

                // static type member
                it.resolveSymbolic("PublicShadowed") shouldBe javasymbols.testdata.Statics.PublicShadowed::class.java
            }
        }
    }

    parserTest("Types imported through $onDemandStaticImports should be shadowed by $singleTypeImports") {

        val acu = javasymbols.testdata.deep.StaticIOD2::class.java.parse()
        // import javasymbols.testdata.Statics.*;


        acu.symbolTable.shouldBeA<SingleImportScope> {

            it.resolveSymbolic("SomeClassA") shouldBe javasymbols.testdata.SomeClassA::class.java

            it.parent.shouldBeA<SamePackageScope> {

                it.parent.shouldBeA<ImportOnDemandScope> {

                    // static type member
                    it.resolveSymbolic("SomeClassA") shouldBe javasymbols.testdata.Statics.SomeClassA::class.java
                }
            }
        }
    }

})




