/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.withClue
import io.kotlintest.should
import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.component7
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory
import net.sourceforge.pmd.lang.java.symbols.table.internal.SuperTypesEnumerator.*
import net.sourceforge.pmd.lang.java.types.JClassType
import net.sourceforge.pmd.lang.java.types.typeDsl

class SuperTypesEnumeratorTest : ParserTestSpec({


    fun SuperTypesEnumerator.list(t: JClassType) = iterable(t).toList()

    parserTest("All supertypes test") {

        val acu = parser.withProcessing().parse("""
            package test;

            interface I1 { } // yields I1, Object

            interface I2 extends I1 { }  // yields I2, I1, Object

            class Sup implements I2 { }  // yields Sup, I2, I1, Object

            class Sub extends Sup implements I1 { } // yields Sub, I1, Sup, I2, Object

            // enum E implements I1, I2 { } // a bad idea, different supertypes on different JDKs
     
        """)

        val (i1, i2, sup, sub) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        doTest("ALL_SUPERTYPES_INCLUDING_SELF") {
            with(acu.typeDsl) {
                ALL_SUPERTYPES_INCLUDING_SELF.list(i1) should containExactly(i1, ts.OBJECT)
                ALL_SUPERTYPES_INCLUDING_SELF.list(i2) should containExactly(i2, i1, ts.OBJECT)
                ALL_SUPERTYPES_INCLUDING_SELF.list(sup) should containExactly(sup, i2, i1, ts.OBJECT)
                ALL_SUPERTYPES_INCLUDING_SELF.list(sub) should containExactly(sub, i1, sup, i2, ts.OBJECT)
            }
        }

        doTest("ALL_STRICT_SUPERTYPES") {
            with(acu.typeDsl) {
                ALL_STRICT_SUPERTYPES.list(i1) should containExactly(ts.OBJECT)
                ALL_STRICT_SUPERTYPES.list(i2) should containExactly(i1, ts.OBJECT)
                ALL_STRICT_SUPERTYPES.list(sup) should containExactly(i2, i1, ts.OBJECT)
                ALL_STRICT_SUPERTYPES.list(sub) should containExactly(i1, sup, i2, ts.OBJECT)
            }
        }

        doTest("DIRECT_STRICT_SUPERTYPES") {
            DIRECT_STRICT_SUPERTYPES.list(i1) should beEmpty()
            DIRECT_STRICT_SUPERTYPES.list(i2) should containExactly(i1)
            DIRECT_STRICT_SUPERTYPES.list(sup) should containExactly(sup.typeSystem.OBJECT, i2)
            DIRECT_STRICT_SUPERTYPES.list(sub) should containExactly(sup, i1)
        }

        doTest("JUST_SELF") {
            JUST_SELF.list(i1) should containExactly(i1)
            JUST_SELF.list(i2) should containExactly(i2)
            JUST_SELF.list(sup) should containExactly(sup)
            JUST_SELF.list(sub) should containExactly(sub)
        }

        doTest("SUPERCLASSES_AND_SELF") {
            SUPERCLASSES_AND_SELF.list(i1) should containExactly(i1)
            SUPERCLASSES_AND_SELF.list(i2) should containExactly(i2)
            SUPERCLASSES_AND_SELF.list(sup) should containExactly(sup, sup.typeSystem.OBJECT)
            SUPERCLASSES_AND_SELF.list(sub) should containExactly(sub, sup, sup.typeSystem.OBJECT)
        }

    }

})