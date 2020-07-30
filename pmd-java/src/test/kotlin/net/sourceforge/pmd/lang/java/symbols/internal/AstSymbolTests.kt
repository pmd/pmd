package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.*
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol
import java.lang.reflect.Modifier

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class AstSymbolTests : ParserTestSpec({

    parserTest("Parsed class symbols") {

        val acu = parser.withProcessing().parse("""

            package com.foo;

            import java.util.ArrayList;

            public final class Foo extends ArrayList<String> {

                void bar() {

                }

                private void ohio() {

                }

                private interface Inner {}


                enum EE {
                    A,
                    B;

                    public class InnerC {}
                }
            }
        """)

        val (fooClass, innerItf, innerEnum, innerClass) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

        val (barM, ohioM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.symbol }



        doTest("should reflect their modifiers") {
            fooClass::getModifiers shouldBe (Modifier.PUBLIC or Modifier.FINAL)
            innerItf::getModifiers shouldBe (Modifier.PRIVATE or Modifier.ABSTRACT or Modifier.STATIC)
            innerClass::getModifiers shouldBe (Modifier.PUBLIC)
            innerEnum::getModifiers shouldBe (Modifier.FINAL or Modifier.STATIC)
        }

        doTest("should reflect their simple names properly") {
            fooClass::getSimpleName shouldBe "Foo"
            innerItf::getSimpleName shouldBe "Inner"
            innerEnum::getSimpleName shouldBe "EE"
            innerClass::getSimpleName shouldBe "InnerC"
        }

        doTest("should reflect their canonical names properly") {
            fooClass::getCanonicalName shouldBe "com.foo.Foo"
            innerItf::getCanonicalName shouldBe "com.foo.Foo.Inner"
            innerEnum::getCanonicalName shouldBe "com.foo.Foo.EE"
            innerClass::getCanonicalName shouldBe "com.foo.Foo.EE.InnerC"
        }

        doTest("should reflect their methods") {
            fooClass.declaredMethods shouldBe listOf(barM, ohioM)
        }

        doTest("should fetch their methods properly") {
            fooClass.getDeclaredMethods("ohio") shouldBe listOf(ohioM)
        }

        doTest("should reflect their super class") {
            with(acu.typeSystem) {
                fooClass::getSuperclass shouldBe getClassSymbol(java.util.ArrayList::class.java)
                innerItf::getSuperclass shouldBe OBJECT.symbol
                innerClass::getSuperclass shouldBe OBJECT.symbol
                innerEnum::getSuperclass shouldBe getClassSymbol(java.lang.Enum::class.java)
            }
        }

        doTest("should reflect their member types") {
            fooClass.declaredClasses shouldBe listOf(innerItf, innerEnum)
            innerEnum.declaredClasses shouldBe listOf(innerClass)
            innerItf.declaredClasses shouldBe emptyList()
            innerClass.declaredClasses shouldBe emptyList()
        }

        doTest("should reflect their declaring type") {
            fooClass::getEnclosingClass shouldBe null
            innerEnum::getEnclosingClass shouldBe fooClass
            innerItf::getEnclosingClass shouldBe fooClass
            innerClass::getEnclosingClass shouldBe innerEnum
        }

        doTest("(enums) should reflect enum constants as fields") {
            val constants =
                    acu.descendants(ASTEnumDeclaration::class.java)
                            .take(1)
                            .flatMap { it.enumConstants }
                            .toList { it.varId.symbol as JFieldSymbol }

            constants.shouldHaveSize(2)

            innerEnum.declaredFields.shouldBe(constants)

            constants.forEach {
                it::isEnumConstant shouldBe true
                it::getModifiers shouldBe (Modifier.PUBLIC or Modifier.STATIC or Modifier.FINAL)
            }
        }
    }


    parserTest("Default/implicit constructors") {

        val acu = parser.withProcessing().parse("""

            package com.foo;

            public final class Foo extends java.util.List {

                private interface Inner {}
                private @interface Annot {}


                enum EE { A, B }
                enum E2 {
                    A, B;
                    E2(String s) {}
                }

                class BClass {
                    protected BClass() { }
                }
                class CClass {
                    // default, package private
                }
            }
        """)

        val (fooClass, innerItf, innerAnnot, e1, e2, bClass, cClass) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

        doTest("Annotations/itfs should have no constructors") {
            innerItf.constructors.shouldBeEmpty()
            innerAnnot.constructors.shouldBeEmpty()
        }

        doTest("Classes should have a default constructor if there are none") {
            fooClass.constructors[0].shouldBeA<JConstructorSymbol> {
                it.formalParameters.shouldBeEmpty()
                it.modifiers shouldBe Modifier.PUBLIC
            }

            cClass.constructors[0].shouldBeA<JConstructorSymbol> {
                it.formalParameters.shouldBeEmpty()
                it.modifiers shouldBe 0
            }
        }

        doTest("Classes should have no default constructor if there are explicit ones") {

            bClass.constructors[0].shouldBeA<JConstructorSymbol> {
                it.formalParameters.shouldBeEmpty()
                it.modifiers shouldBe Modifier.PROTECTED
            }
        }

        doTest("Enums should have an implicit private constructor") {
            e1.constructors.shouldHaveSize(1)
            e1.constructors[0].shouldBeA<JConstructorSymbol> {
                it::getModifiers shouldBe Modifier.PRIVATE
                it::getFormalParameters shouldBe emptyList()
            }
        }

        doTest("Enums should have no implicit constructor if there are explicit ones") {
            e2.constructors.shouldHaveSize(1)
            e2.constructors[0].shouldBeA<JConstructorSymbol> { ctor ->
                ctor::getModifiers shouldBe Modifier.PRIVATE
                ctor.formalParameters.shouldHaveSize(1)
                ctor.formalParameters[0].shouldBeA<JFormalParamSymbol> {
                    it::getDeclaringSymbol shouldBe ctor
                    it::getSimpleName shouldBe "s"
                }
            }
        }
    }

    parserTest("Enum details") {

        val acu = parser.withProcessing().parse("""
            enum EE { A, B }
            enum E2 { A { } /* anon */ }
        """.trimIndent())

        val (enum, enum2, enumAnon) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

        doTest("Enums should have a values() method") {
            val values = enum.getDeclaredMethods("values")
            values.shouldHaveSize(1)
            values[0].shouldBeA<JMethodSymbol> {
                it::getEnclosingClass shouldBe enum
                it::getModifiers shouldBe (Modifier.PUBLIC or Modifier.STATIC)
                it::getSimpleName shouldBe "values"
            }
        }

        doTest("Enums should have a valueOf() method") {
            val values = enum.getDeclaredMethods("valueOf")
            values.shouldHaveSize(1)
            values[0].shouldBeA<JMethodSymbol> {
                it::getEnclosingClass shouldBe enum
                it::getModifiers shouldBe (Modifier.PUBLIC or Modifier.STATIC)
                it::getSimpleName shouldBe "valueOf"
            }
        }

        doTest("Enum constants with bodies should be static") {
            enumAnon::getModifiers shouldBe Modifier.STATIC
        }

        doTest("Enum constants with bodies should reflect their superclass properly") {
            enumAnon::getSuperclass shouldBe enum2
        }
    }


    parserTest("Local class symbols") {

        val acu = parser.withProcessing().parse("""

            package com.foo;

            public final class Foo { // fooClass

                Foo() {
                    abstract class Loc1 {} // ctorLoc
                }

                void bar() {
                    final class Locc {} // barLoc
                }

                private void ohio() {
                    class Locc {} // ohioLoc
                    new Runnable() { // anon
                        {
                            class Locc {} // anonLoc
                        }
                    };
                }

                {
                    class Locc {} // initLoc
                }

                static {
                    class Loc4 { // staticInitLoc
                        class LocMember {}
                    }
                }

            }
        """)

        val allTypes = acu.descendants(ASTAnyTypeDeclaration::class.java).crossFindBoundaries().toList { it.symbol }
        val locals = allTypes.filter { it.isLocalClass }
        val (fooClass, ctorLoc, barLoc, ohioLoc, anon, anonLoc, initLoc, staticInitLoc, locMember) = allTypes
        val (ctor) = acu.descendants(ASTConstructorDeclaration::class.java).toList { it.symbol }
        val (barM, ohioM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.symbol }


        locals shouldBe listOf(ctorLoc, barLoc, ohioLoc, anonLoc, initLoc, staticInitLoc)

        doTest("should reflect their modifiers") {
            ctorLoc::getModifiers shouldBe (Modifier.ABSTRACT)
            barLoc::getModifiers shouldBe (Modifier.FINAL)
            (locals - barLoc - ctorLoc).forEach {
                it::getModifiers shouldBe 0
            }
        }

        doTest("should reflect their simple names properly") {
            ctorLoc::getSimpleName shouldBe "Loc1"
            barLoc::getSimpleName shouldBe "Locc"
            ohioLoc::getSimpleName shouldBe "Locc"
            anonLoc::getSimpleName shouldBe "Locc"
            initLoc::getSimpleName shouldBe "Locc"
            staticInitLoc::getSimpleName shouldBe "Loc4"
        }

        doTest("should have no canonical name") {
            locals.forEach {
                it::getCanonicalName shouldBe null
            }
            locMember::getCanonicalName shouldBe null
        }

        doTest("should reflect their enclosing type") {
            ctorLoc::getEnclosingClass shouldBe fooClass
            barLoc::getEnclosingClass shouldBe fooClass
            ohioLoc::getEnclosingClass shouldBe fooClass
            anonLoc::getEnclosingClass shouldBe anon
            initLoc::getEnclosingClass shouldBe fooClass
            staticInitLoc::getEnclosingClass shouldBe fooClass
        }

        doTest("should reflect their enclosing method") {
            ctorLoc::getEnclosingMethod shouldBe ctor
            barLoc::getEnclosingMethod shouldBe barM
            ohioLoc::getEnclosingMethod shouldBe ohioM
            anonLoc::getEnclosingMethod shouldBe null
            initLoc::getEnclosingMethod shouldBe null
            staticInitLoc::getEnclosingMethod shouldBe null
        }

    }



    parserTest("Record symbols") {

        // TODO explicit declaration of canonical ctor (need type res)

        val acu = parser.withProcessing().parse("""
            package com.foo;

            public record Point(int x, int y) {

                public int x() {} // explicitly declared accessor, the other is synthesized

            }

            record Point2(int x2, int... y2) {

                public Point2 { // must be declared public

                }
                
                public Point2(int x2) { // aux constructor
                    this(x2);
                }
            }

        """)

        val (pointRecord, point2Record) = acu.descendants(ASTRecordDeclaration::class.java).toList { it.symbol }
        val (canonCtor1, canonCtor2) = acu.descendants(ASTRecordComponentList::class.java).toList { it.symbol }
        val (auxCtor) = acu.descendants(ASTConstructorDeclaration::class.java).toList { it.symbol }
        val (xAccessor) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.symbol }
        val (xComp, yComp, x2Comp, y2Comp, x2Formal) = acu.descendants(ASTVariableDeclaratorId::class.java).toList { it.symbol }


        doTest("should reflect their modifiers") {
            pointRecord::getModifiers shouldBe (Modifier.PUBLIC or Modifier.FINAL)
            point2Record::getModifiers shouldBe (Modifier.FINAL)
        }

        doTest("should reflect their simple names properly") {
            pointRecord::getSimpleName shouldBe "Point"
            point2Record::getSimpleName shouldBe "Point2"
        }

        doTest("Should have a canonical ctor") {
            pointRecord::getConstructors shouldBe listOf(canonCtor1)

            canonCtor1::getSimpleName shouldBe JConstructorSymbol.CTOR_NAME
            canonCtor1::getModifiers shouldBe Modifier.PUBLIC

            canonCtor1.formalParameters should haveSize(2)
            canonCtor1.formalParameters[0].shouldBeA<JFormalParamSymbol> {
                it::getSimpleName shouldBe "x"
                it::getDeclaringSymbol shouldBe canonCtor1
            }
            canonCtor1.formalParameters[1].shouldBeA<JFormalParamSymbol> {
                it::getSimpleName shouldBe "y"
                it::getDeclaringSymbol shouldBe canonCtor1
            }

            point2Record::getConstructors shouldBe listOf(canonCtor2, auxCtor)
            canonCtor2::isVarargs shouldBe true
        }

        doTest("should have field symbols for each component") {
            xComp.shouldBeA<JFieldSymbol> {
                it::getSimpleName shouldBe "x"
                it::getModifiers shouldBe (Modifier.PRIVATE or Modifier.FINAL)
                it::getEnclosingClass shouldBe pointRecord
            }

            yComp.shouldBeA<JFieldSymbol> {
                it::getSimpleName shouldBe "y"
                it::getModifiers shouldBe (Modifier.PRIVATE or Modifier.FINAL)
                it::getEnclosingClass shouldBe pointRecord
            }

            y2Comp.shouldBeA<JFieldSymbol> {
                it::getSimpleName shouldBe "y2"
                it::getModifiers shouldBe (Modifier.PRIVATE or Modifier.FINAL)
                it::getEnclosingClass shouldBe point2Record
            }
        }


        doTest("should declare field accessors") {
            pointRecord.declaredMethods should haveSize(2)
            pointRecord.getDeclaredMethods("x") shouldBe listOf(xAccessor)
            pointRecord.getDeclaredMethods("y").single().shouldBeA<JMethodSymbol> {
                it::getModifiers shouldBe Modifier.PUBLIC
                it::getFormalParameters shouldBe emptyList()
                it::getSimpleName shouldBe "y"
                it::getTypeParameters shouldBe emptyList()
            }
        }
    }



    parserTest("Anonymous class symbols") {

        val acu = parser.withProcessing().parse("""

            package com.foo;

            public class Foo { // fooClass

                final Foo v = new Foo() {}; // fieldAnon

                static final Void v2 = new Foo() {}; // staticFieldAnon

                Foo() {
                    new Runnable() {}; // ctorAnon
                }

                void bar() {
                    new Runnable() {}; // barAnon
                }

                static void bar2() {
                    new Runnable() {}; // staticBarAnon
                }

                {
                    new Runnable() {}; // initAnon
                }

                static {
                   new Runnable() { // staticInitAnon
                      {
                        new Runnable() {}; // anonAnon
                      }

                      class AnonMember {} // anonMember
                   };
                }

            }
        """)

        val allTypes = acu.descendants(ASTAnyTypeDeclaration::class.java).crossFindBoundaries().toList { it.symbol }
        val allAnons = allTypes.filter { it.isAnonymousClass }
        val (fooClass, fieldAnon, staticFieldAnon, ctorAnon, barAnon, staticBarAnon, initAnon, staticInitAnon, anonAnon, anonMember) = allTypes
        val (ctor) = acu.descendants(ASTConstructorDeclaration::class.java).toList { it.symbol }
        val (barM, bar2M) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.symbol }

        allAnons shouldBe (allTypes - fooClass - anonMember)

        doTest("should be static in static contexts") {
            listOf(staticBarAnon, staticInitAnon, staticFieldAnon).forEach {
                it::getModifiers shouldBe Modifier.STATIC
            }

            listOf(ctorAnon, barAnon, initAnon, anonAnon, fieldAnon).forEach {
                it::getModifiers shouldBe 0 // nonstatic
            }
        }

        doTest("should use the empty string as their simple name") {
            allAnons.forEach {
                it::getSimpleName shouldBe ""
            }
        }

        doTest("should have no canonical name") {
            allAnons.forEach {
                it::getCanonicalName shouldBe null
            }
            anonMember::getCanonicalName shouldBe null
        }

        doTest("should reflect their enclosing type") {
            (allAnons - anonAnon).forEach {
                it::getEnclosingClass shouldBe fooClass
            }
            anonAnon::getEnclosingClass shouldBe staticInitAnon
        }

        doTest("should reflect their superclass") {
            val anonsWithSuperClass = listOf(fieldAnon, staticFieldAnon)

            anonsWithSuperClass.forEach {
                it::getSuperclass shouldBe fooClass
                it::getSuperInterfaces shouldBe emptyList()
            }

            // all others are Runnable
            (allAnons - anonsWithSuperClass).forEach {
                it::getSuperclass shouldBe it.typeSystem.OBJECT.symbol
                it::getSuperInterfaces shouldBe listOf(it.typeSystem.getClassSymbol(Runnable::class.java))
            }
        }

        doTest("should reflect their enclosing method") {
            ctorAnon::getEnclosingMethod shouldBe ctor
            barAnon::getEnclosingMethod shouldBe barM
            staticBarAnon::getEnclosingMethod shouldBe bar2M
            anonAnon::getEnclosingMethod shouldBe null
            initAnon::getEnclosingMethod shouldBe null
            staticInitAnon::getEnclosingMethod shouldBe null
        }
    }
})
