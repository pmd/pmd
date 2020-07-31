/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("RemoveRedundantQualifierName")

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import javasymbols.testdata.StaticNameCollision
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.classSym
import net.sourceforge.pmd.lang.java.symbols.internal.getDeclaredMethods
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo.*
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChain
import net.sourceforge.pmd.lang.java.types.JClassType
import net.sourceforge.pmd.lang.java.types.JTypeMirror

/**
 * Tests the scopes that dominate the whole compilation unit.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class HeaderScopesTest : ProcessorTestSpec({

    val typesInTheSamePackage = "types in the same package"
    val javalangTypes = "types from java.lang"
    val singleTypeImports = "single-type imports"
    val staticSingleMemberImports = "static single-member imports"
    val onDemandTypeImports = "on-demand type imports"
    val onDemandStaticImports = "on-demand static imports"

    // The test data is placed in a short package to allow typing out FQCNs here for readability

    fun JSymbolTable.resolveField(s: String): JFieldSymbol = variables().resolveFirst(s)!!.symbol.shouldBeA()
    fun JSymbolTable.resolveMethods(s: String): List<JMethodSymbol> = methods().resolve(s).map { it.symbol as JMethodSymbol }

    fun ShadowChain<JTypeMirror, ScopeInfo>.shouldResolveToClass(simpleName: String, qualName: String) {
        resolveFirst(simpleName).shouldBeA<JClassType> {
            it.symbol::getBinaryName shouldBe qualName
            it.symbol::getSimpleName shouldBe simpleName
        }
    }

    fun ShadowChain<JTypeMirror, ScopeInfo>.typeShadowSequence(simpleName: String): List<Pair<ScopeInfo, String>> {
        return sequence {
            val iter = iterateResults(simpleName)
            while (iter.hasNext()) {
                iter.next()
                val t = iter.results.single().shouldBeA<JClassType>()
                yield(iter.scopeTag to t.symbol.binaryName)
            }
        }.toList()
    }

    parserTest("Test same-package scope") {

        val acu = parser.parseClass(javasymbols.testdata.TestCase1::class.java)

        acu.symbolTable.types().shouldResolveToClass("SomeClassA", "javasymbols.testdata.SomeClassA")
    }


    parserTest("$javalangTypes should be shadowed by $typesInTheSamePackage") {

        val acu = parser.parseClass(javasymbols.testdata.TestCase1::class.java)


        acu.symbolTable.types().typeShadowSequence("Thread") shouldBe
                // from same package
                listOf(SAME_PACKAGE to "javasymbols.testdata.Thread",
                        JAVA_LANG to "java.lang.Thread")
    }



    parserTest("$typesInTheSamePackage should be shadowed by $singleTypeImports") {

        val acu = parser.parseClass(javasymbols.testdata.deep.SomewhereElse::class.java)

        acu.symbolTable.types().typeShadowSequence("SomeClassA") shouldBe
                // from same package
                listOf(SINGLE_IMPORT to "javasymbols.testdata.SomeClassA",
                        SAME_PACKAGE to "javasymbols.testdata.deep.SomeClassA")

    }

    parserTest("$javalangTypes should be shadowed by $singleTypeImports") {

        val acu = parser.parseClass(javasymbols.testdata.deep.SomewhereElse::class.java)

        acu.symbolTable.types().typeShadowSequence("Thread") shouldBe
                listOf(SINGLE_IMPORT to "javasymbols.testdata.Thread",
                        JAVA_LANG to "java.lang.Thread")
    }


    parserTest("$onDemandTypeImports should be shadowed by everything") {

        val acu = parser.parseClass(javasymbols.testdata.deep.TypeImportsOnDemand::class.java)
        // import javasymbols.testdata.*;

        val group = acu.symbolTable.types()

        group.typeShadowSequence("Thread") shouldBe
                // from java.lang
                listOf(JAVA_LANG to "java.lang.Thread",
                        IMPORT_ON_DEMAND to "javasymbols.testdata.Thread")

        group.typeShadowSequence("SomeClassA") shouldBe
                // from same package
                listOf(SAME_PACKAGE to "javasymbols.testdata.deep.SomeClassA",
                        IMPORT_ON_DEMAND to "javasymbols.testdata.SomeClassA")

        group.typeShadowSequence("Statics") shouldBe
                // from the import-on-demand
                listOf(IMPORT_ON_DEMAND to "javasymbols.testdata.Statics")

        group.typeShadowSequence("TestCase1") shouldBe
                // from the single type import
                listOf(SINGLE_IMPORT to "javasymbols.testdata.TestCase1",
                       IMPORT_ON_DEMAND to "javasymbols.testdata.TestCase1")
    }



    parserTest("$onDemandStaticImports should import only accessible members") {

        val acu = parser.parseClass(javasymbols.testdata.deep.StaticImportOnDemand::class.java)
        // import javasymbols.testdata.Statics.*;


        acu.symbolTable.let {

            it.variables().resolveFirst("PUBLIC_FIELD") shouldNotBe null
            it.variables().resolveFirst("PACKAGE_FIELD") shouldBe null
            it.variables().resolveFirst("PRIVATE_FIELD") shouldBe null
            it.variables().resolveFirst("PROTECTED_FIELD") shouldBe null

            it.resolveMethods("packageMethod").shouldHaveSize(0)
            it.resolveMethods("privateMethod").shouldHaveSize(0)
            it.resolveMethods("protectedMethod").shouldHaveSize(0)
            it.resolveMethods("publicMethod").shouldHaveSize(2)
            it.resolveMethods("publicMethod2").shouldHaveSize(1)

            it.types().resolveFirst("PublicStatic") shouldNotBe null
            it.types().resolveFirst("PackageStatic") shouldBe null
            it.types().resolveFirst("ProtectedStatic") shouldBe null
            it.types().resolveFirst("PrivateStatic") shouldBe null

        }
    }

    parserTest("$onDemandStaticImports should import only static members") {

        val acu = parser.parseClass(javasymbols.testdata.deep.StaticImportOnDemand::class.java)
        // import javasymbols.testdata.Statics.*;

        acu.symbolTable.apply {

            variables().resolveFirst("PUBLIC_FIELD") shouldNotBe null
            variables().resolveFirst("publicField") shouldBe null

            resolveMethods("publicMethod").shouldHaveSize(2)
            resolveMethods("publicInstanceMethod").shouldHaveSize(0)

            types().resolveFirst("PublicStatic") shouldNotBe null
            types().resolveFirst("PublicInner") shouldBe null
        }
    }

    parserTest("Types imported through $onDemandStaticImports should be shadowed by $typesInTheSamePackage") {

        val acu = parser.parseClass(javasymbols.testdata.deep.StaticImportOnDemand::class.java)
        // import javasymbols.testdata.Statics.*;

        acu.symbolTable.types().typeShadowSequence("PublicShadowed") shouldBe
                // from same package
                listOf(SAME_PACKAGE to "javasymbols.testdata.deep.PublicShadowed",
                        IMPORT_ON_DEMAND to "javasymbols.testdata.Statics\$PublicShadowed")
    }

    parserTest("Types imported through $onDemandStaticImports should be shadowed by $singleTypeImports") {

        val acu = parser.parseClass(javasymbols.testdata.deep.StaticIOD2::class.java)
        // import javasymbols.testdata.Statics.*;

        acu.symbolTable.types().typeShadowSequence("SomeClassA") shouldBe
                listOf(SINGLE_IMPORT to "javasymbols.testdata.SomeClassA",
                        SAME_PACKAGE to "javasymbols.testdata.deep.SomeClassA",
                        IMPORT_ON_DEMAND to "javasymbols.testdata.Statics\$SomeClassA"
                )
    }

    parserTest("$staticSingleMemberImports should import types, fields and methods with the same name") {

        val acu = parser.parseClass(javasymbols.testdata.deep.StaticCollisionImport::class.java)
        // import javasymbols.testdata.Statics.*;

        acu.symbolTable.let {
            it.resolveField("Ola") shouldBe classSym(StaticNameCollision::class.java)!!.getDeclaredField("Ola")!!
            it.resolveMethods("Ola").shouldContainExactly(classSym(StaticNameCollision::class.java)!!.getDeclaredMethods("Ola").toList())
            it.types().shouldResolveToClass("Ola", "javasymbols.testdata.StaticNameCollision\$Ola")
        }
    }


    parserTest("Method imported through $onDemandStaticImports should be shadowed by $staticSingleMemberImports") {

        val acu = parser.parse(
                """

            import static javasymbols.testdata.StaticNameCollision.publicMethod;

            import static javasymbols.testdata.Statics.*;

            class Foo {}

                """
        )
        doTest("The static import should shadow methods with the same name") {

            acu.symbolTable.methods().iterateResults("publicMethod").let {
                it.next()
                it.apply {
                    scopeTag shouldBe SINGLE_IMPORT
                    results should haveSize(2)
                    results.forEach {
                        it.symbol.enclosingClass.canonicalName shouldBe "javasymbols.testdata.StaticNameCollision"
                    }
                }

                it.next()
                it.apply {
                    scopeTag shouldBe IMPORT_ON_DEMAND
                    results should haveSize(2)
                    results.forEach {
                        it.symbol.enclosingClass.canonicalName shouldBe "javasymbols.testdata.Statics"
                    }
                }
            }
        }
        doTest("Other names are not shadowed but treated separately") {

            acu.symbolTable.methods().iterateResults("publicMethod2").let {
                // other names are still imported by the import on demand

                it.next()
                it.apply {
                    scopeTag shouldBe IMPORT_ON_DEMAND
                    results should haveSize(1)
                    results.forEach {
                        it.symbol.enclosingClass.canonicalName shouldBe "javasymbols.testdata.Statics"
                    }
                }
            }
        }
    }
})




