package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.specs.FunSpec
import javasymbols.testdata.Statics
import net.sourceforge.pmd.lang.java.symbols.refs.*
import net.sourceforge.pmd.lang.java.symbols.shouldBePresent
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
import java.util.*
import java.util.logging.Logger
import java.util.stream.Stream
import kotlin.test.assertEquals

/**
 * Asserts that queries don't travel up the table stack more than necessary.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class LazinessTest : FunSpec({

    fun <T> lazinessTest(
            dummyTableConstructor: (JSymbolTable, SymbolTableResolveHelper, () -> Optional<T>) -> JSymbolTable,
            testedMethod: (JSymbolTable, String) -> Optional<out T>,
            resultGetter: () -> T) {

        val myExpectedResult = Optional.of(resultGetter())
        val dummyResolveHelper = SymbolTableResolveHelper("", LazinessTest::class.java.classLoader, 11)

        val top: JSymbolTable = dummyTableConstructor(EmptySymbolTable.getInstance(), dummyResolveHelper) {
            throw AssertionError("The parent shouldn't have been queried")
        }

        val bottom: JSymbolTable = dummyTableConstructor(top, dummyResolveHelper) { Optional.of(resultGetter()) }

        assertEquals(myExpectedResult, testedMethod(bottom, ""))
    }

    test("Test resolveTypeName doesn't query parents if not needed") {
        lazinessTest<JSimpleTypeDeclarationSymbol<*>>(::MyTypeTable, JSymbolTable::resolveTypeName) {
            JResolvableClassDeclarationSymbol(LazinessTest::class.java)
        }
    }


    test("Test resolveValueName doesn't query parents if not needed") {
        lazinessTest(::MyValueTable, JSymbolTable::resolveValueName) {
            JFieldSymbol(Statics::class.java.getDeclaredField("PUBLIC_FIELD"))
        }
    }


    test("Test resolveMethodName doesn't trigger evaluation of parent streams") {
        lazinessTest(::MyMethodTable, { t, n -> t.resolveMethodName(n).findFirst() }) {
            JMethodSymbol(Statics::class.java.getDeclaredMethod("publicMethod"))
        }
    }


    test("Test resolveMethodName evaluates parent streams if needed") {

        val dummyResolveHelper = SymbolTableResolveHelper("", LazinessTest::class.java.classLoader, 11)

        val top: JSymbolTable = MyMethodTable(EmptySymbolTable.getInstance(), dummyResolveHelper) {
            Optional.of(JMethodSymbol(Statics::class.java.getDeclaredMethod("publicMethod")))
        }

        val bottom: JSymbolTable = MyMethodTable(top, dummyResolveHelper) {
            Optional.empty()
        }

        bottom.resolveMethodName("publicMethod").findFirst().shouldBePresent()
    }

})

private class MyTypeTable(parent: JSymbolTable, resolveHelper: SymbolTableResolveHelper, private val typeSymbolGetter: () -> Optional<out JSimpleTypeDeclarationSymbol<*>>) : AbstractSymbolTable(parent, resolveHelper) {
    override fun resolveTypeNameImpl(simpleName: String) = typeSymbolGetter()

    override fun resolveValueNameImpl(simpleName: String): Optional<JValueSymbol> = Optional.empty()

    override fun resolveMethodNameImpl(simpleName: String): Stream<JMethodSymbol> = Stream.empty()

    override fun getLogger(): Logger = Logger.getLogger(LazinessTest::class.java.canonicalName)
}

private class MyValueTable(parent: JSymbolTable, resolveHelper: SymbolTableResolveHelper, private val valueSymbolGetter: () -> Optional<JValueSymbol>) : AbstractSymbolTable(parent, resolveHelper) {
    override fun resolveTypeNameImpl(simpleName: String) = Optional.empty<JSimpleTypeDeclarationSymbol<*>>()

    override fun resolveValueNameImpl(simpleName: String): Optional<JValueSymbol> = valueSymbolGetter()

    override fun resolveMethodNameImpl(simpleName: String): Stream<JMethodSymbol> = Stream.empty()

    override fun getLogger(): Logger = Logger.getLogger(LazinessTest::class.java.canonicalName)
}

private class MyMethodTable(parent: JSymbolTable, resolveHelper: SymbolTableResolveHelper, private val methodSymbolGetter: () -> Optional<JMethodSymbol>) : AbstractSymbolTable(parent, resolveHelper) {
    override fun resolveTypeNameImpl(simpleName: String) = Optional.empty<JSimpleTypeDeclarationSymbol<*>>()

    override fun resolveValueNameImpl(simpleName: String): Optional<JValueSymbol> = Optional.empty()

    override fun resolveMethodNameImpl(simpleName: String): Stream<JMethodSymbol> = methodSymbolGetter().stream()

    override fun getLogger(): Logger = Logger.getLogger(LazinessTest::class.java.canonicalName)
}
