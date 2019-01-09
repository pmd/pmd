package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.properties.forAll
import io.kotlintest.specs.WordSpec
import net.sourceforge.pmd.lang.java.symbols.TestClassesGen
import net.sourceforge.pmd.lang.java.symbols.getTypeDeclaration
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.GenericClass
import net.sourceforge.pmd.lang.java.symbols.internal.testdata.GenericClassCopy
import net.sourceforge.pmd.lang.java.symbols.internal.impl.JClassSymbolImpl.create as classSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.impl.JClassSymbolImpl.create as classSymbol

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class JTypeParameterSymbolTest : WordSpec({

    "A type parameter symbol" should {

        "be equal to a type parameter symbol declared on the same owner with the same name" {
            forAll(TestClassesGen) { it: Class<*> ->
                val fromAst = classSymbol(it.getTypeDeclaration()).typeParameters
                val fromReflect = classSymbol(it).typeParameters
                fromAst == fromReflect
            }
        }

        "not be equal to type parameter symbols declared on any other entity" {
            classSymbol(GenericClass::class.java).typeParameters shouldNotBe classSymbol(GenericClassCopy::class.java).typeParameters
        }

    }

})