package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractFunSpec
import javasymbols.testdata.Statics
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.classSym
import net.sourceforge.pmd.lang.java.symbols.internal.getDeclaredMethods
import net.sourceforge.pmd.lang.java.symbols.internal.testSymFactory
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult
import kotlin.test.assertEquals

/**
 * Asserts that queries don't travel up the table stack more than necessary.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class LazinessTest : AbstractFunSpec({

    fun <T> lazinessTest(
            dummyTableConstructor: (JSymbolTable, SymbolTableHelper, () -> T?) -> JSymbolTable,
            testedMethod: (JSymbolTable, String) -> T?,
            resultGetter: () -> T) {

        val myExpectedResult = resultGetter()
        val dummyResolveHelper = testResolveHelper("")

        val top: JSymbolTable = dummyTableConstructor(EmptySymbolTable.getInstance(), dummyResolveHelper) {
            throw AssertionError("The parent shouldn't have been queried")
        }

        val bottom: JSymbolTable = dummyTableConstructor(top, dummyResolveHelper) { resultGetter() }

        assertEquals(myExpectedResult, testedMethod(bottom, ""))
    }

    test("Test resolveTypeName doesn't query parents if not needed") {
        lazinessTest(::MyTypeTable, { t, n -> t.resolveTypeName(n)!!.result }) {
            testSymFactory.getClassSymbol(LazinessTest::class.java)!!
        }
    }

    val staticsClass = classSym(Statics::class.java)!!


    test("Test resolveValueName doesn't query parents if not needed") {
        lazinessTest(::MyValueTable, { t, n -> t.resolveValueName(n)!!.result }) {
            staticsClass.getDeclaredField("PUBLIC_FIELD")
        }
    }

    test("Test resolveMethodName evaluates parent streams if needed") {

        val dummyResolveHelper = testResolveHelper("")

        val top: JSymbolTable = MyMethodTable(EmptySymbolTable.getInstance(), dummyResolveHelper) {
            staticsClass.getDeclaredMethods("publicMethod")[1]
        }

        val mid: JSymbolTable = MyMethodTable(top, dummyResolveHelper) {
            staticsClass.getDeclaredMethods("publicMethod")[0]
        }

        val bottom: JSymbolTable = MyMethodTable(mid, dummyResolveHelper) {
            null
        }

        bottom.resolveMethodName("publicMethod").shouldHaveSize(2)
        bottom.resolveMethodName("publicMethod") shouldBe staticsClass.getDeclaredMethods("publicMethod")
    }

})

private class MyTypeTable(
        parent: JSymbolTable,
        helper: SymbolTableHelper,
        private val typeSymbolGetter: () -> JTypeDeclSymbol?
) : AbstractSymbolTable(parent, helper) {

    override fun resolveTypeNameImpl(simpleName: String): ResolveResult<JTypeDeclSymbol> =
            ResolveResultImpl.ClassResolveResult(typeSymbolGetter(), this, JavaParsingHelper.JUST_PARSE.parse(""))

}

private class MyValueTable(
        parent: JSymbolTable,
        helper: SymbolTableHelper,
        private val variableSymbolGetter: () -> JVariableSymbol?
) : AbstractSymbolTable(parent, helper) {

    override fun resolveValueNameImpl(simpleName: String): ResolveResult<JVariableSymbol>? =
            ResolveResultImpl.VarResolveResult(variableSymbolGetter(), this, JavaParsingHelper.JUST_PARSE.parse(""))


}

private class MyMethodTable(parent: JSymbolTable, helper: SymbolTableHelper,
                            private val methodSymbolGetter: () -> JMethodSymbol?) : AbstractSymbolTable(parent, helper) {

    override fun getCachedMethodResults(simpleName: String): List<JMethodSymbol>? =
            null

    override fun resolveMethodNamesHere(simpleName: String): List<JMethodSymbol> =
            listOfNotNull(methodSymbolGetter()).toMutableList()

}
