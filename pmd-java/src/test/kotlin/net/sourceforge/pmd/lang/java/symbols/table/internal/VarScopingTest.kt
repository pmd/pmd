/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.since
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J22
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol
import net.sourceforge.pmd.lang.java.types.methodCalls
import net.sourceforge.pmd.lang.java.types.shouldHaveType
import net.sourceforge.pmd.lang.test.ast.*
import net.sourceforge.pmd.lang.test.ast.shouldBe
import java.lang.reflect.Modifier

@Suppress("UNUSED_VARIABLE")
class VarScopingTest : ProcessorTestSpec({
    parserTestContainer("Shadowing of variables") {
        val acu = parser.parse("""

            // TODO test with static import, currently there are no "unresolved field" symbols

            class Outer<T> extends Sup {
                private T f;

                {
                    f.foo(); // outerField
                    T f;

                    for (T f : f.foo()) { // localInInit
                       f.foo(); // foreachParam
                    }
                }

                void method(T f) {
                    f.foo(); // methodParam

                    {
                        T f;
                        f.foo(); // localInBlock
                    }
                }

                class Inner {
                    T f;

                    {
                        f.foo(); // innerField
                    }
                }
            }
        """.trimIndent())

        val (outerClass, innerClass) =
                acu.descendants(ASTClassDeclaration::class.java).toList()

        val (outerField, localInInit, foreachParam, methodParam, localInBlock, innerField) =
                acu.descendants(ASTVariableId::class.java)
                   .crossFindBoundaries().toList()

        val (inInitializer, inForeachInit, inForeach, inMethod, inLocalBlock, inInnerClass) =
                acu.descendants(ASTMethodCall::class.java)
                   .crossFindBoundaries().toList()


        doTest("Inside outer initializer: f is outerField") {
            inInitializer.symbolTable.shouldResolveVarTo<JFieldSymbol>("f", outerField.symbol)
        }

        doTest("Inside foreach initializer: f is localInInit") {
            val sym = inForeachInit shouldResolveToLocal localInInit
            sym.shouldBeA<JLocalVariableSymbol>()
        }

        doTest("Inside foreach: f is foreachParam") {
            val sym = inForeach shouldResolveToLocal foreachParam
            sym.shouldBeA<JLocalVariableSymbol>()

        }

        doTest("Inside method body: f is methodParam") {
            val sym = inMethod shouldResolveToLocal methodParam
            sym.shouldBeA<JFormalParamSymbol>()
        }

        doTest("Inside local block: f is local var") {
            val sym = inLocalBlock shouldResolveToLocal localInBlock
            sym.shouldBeA<JLocalVariableSymbol>()
        }

        doTest("Inside inner class: f is inner field") {
            inInnerClass.symbolTable.shouldResolveVarTo<JFieldSymbol>("f", innerField.symbol)
        }
    }

    parserTestContainer("Try statement") {
        val acu = parser.withProcessing().parse("""

            // TODO test with static import, currently there are no "unresolved field" symbols

            class Outer extends Sup {
                private Reader r; // outerField

                {
                    try {

                    } catch (Exception e) { // exception1
                      e.printStackTrace(); // inCatch1
                    }

                    try (Reader r = new StringReader("k")) { // reader1
                      r.read();  // inTry: resource
                    } catch (Exception e) { // exception2
                      r.close(); // inCatch2: field
                    } finally {
                      r.close(); // inFinally: field
                    }

                    try (Reader r = new StringReader("k");   // reader2
                         BufferedReader br = r.buffered()) { // bufferedReader, inResource

                    }

                    try (Reader f = r;                      // reader3
                         BufferedReader r = f.buffered()) { // br2
                    }
                    
                    try (Reader f4 = r;                      // reader4
                         f4.buffered().field) {              // inConciseResource
                    }
                }
            }
        """.trimIndent())

        val (outerField, exception1, reader1, exception2, reader2, bufferedReader, reader3, br2, reader4) =
                acu.descendants(ASTVariableId::class.java).toList()

        val (inCatch1, inTry, inCatch2, inFinally, inResource, _, inConciseResource) =
                acu.descendants(ASTMethodCall::class.java).toList()

        doTest("Inside catch clause: catch param is in scope") {
            inCatch1 shouldResolveToLocal exception1
        }

        doTest("Inside try body: r is the resource") {
            inTry shouldResolveToLocal reader1
        }

        doTest("Inside catch 2: r is the field (resource not in scope)") {
            inCatch2 shouldResolveToField outerField
        }

        doTest("Inside finally: r is the field (resource not in scope)") {
            inFinally shouldResolveToField outerField
        }

        doTest("Inside resource declaration: r is the resource of the same resource list") {
            inResource shouldResolveToLocal reader2
        }

        doTest("Inside resource declaration 2: r is the field, before the other resource is declared") {
            reader3.initializer!! shouldResolveToField outerField
        }

        doTest("Inside resource declaration 2: r is the resource, after its declaration") {
            br2.initializer!! shouldResolveToLocal reader3
        }

        doTest("Inside concise resource declaration") {
            inConciseResource.qualifier!! shouldResolveToLocal reader4
        }

        doTest("Resources are in scope, even if the try body is empty") {
            val emptyBlock = br2.ancestors(ASTTryStatement::class.java).firstOrThrow().body
            emptyBlock.toList() should beEmpty()

            emptyBlock shouldResolveToLocal reader3
            emptyBlock shouldResolveToLocal br2
        }
    }

    parserTestContainer("Switch statement") {
        val acu = parser.withProcessing().parse("""

            // TODO test with static import, currently there are no "unresolved field" symbols

            class Outer extends Sup {
                private int j; // outerField

                {
                    switch (j) { // fAccess
                    case 4:
                        int i = j; // ivar, fAccess2
                        int j = i; // jvar, iAccess
                        return j + 1; // jAccess
                    case 3:
                        int k = 0, l = k + l; // kvar, lvar, kAccess, lAccess
                        return i + 1; // iAccess2
                    }
                }
            }
        """.trimIndent())

        val (outerField, ivar, jvar, kvar, lvar) =
                acu.descendants(ASTVariableId::class.java).toList()

        val (fAccess, fAccess2, iAccess, jAccess, kAccess, lAccess, iAccess2) =
                acu.descendants(ASTVariableAccess::class.java).toList()

        doTest("Inside tested expr: vars are not in scope") {
            fAccess shouldResolveToField outerField
        }

        doTest("Inside label: var is NOT in scope BEFORE its declaration") {
            fAccess2 shouldResolveToField outerField
        }

        doTest("Inside label: var IS in scope AFTER its declaration") {
            jAccess shouldResolveToLocal jvar
            iAccess shouldResolveToLocal ivar
        }

        doTest("Var is in scope in its own initializer (error)") {
            lAccess shouldResolveToLocal lvar
        }

        doTest("Var is in scope in other initializers in same statement") {
            kAccess shouldResolveToLocal kvar
        }

        doTest("Inside fallthrough: var is in scope") {
            // this is suprising but legal, the var is just not definitely
            // assigned at the point of use
            iAccess2 shouldResolveToLocal ivar
        }
    }

    parserTestContainer("For init variables") {
        val acu = parser.withProcessing().parse("""
            class Outer extends Sup {
                {
                    for (int i = 0, x = i; i < 20; i++) {
                        i++;
                        for (i++; false;) {}
                    }
                }
            }
        """.trimIndent())

        val (ivar, _) =
                acu.descendants(ASTVariableId::class.java).toList()

        val iUsages = acu.descendants(ASTVariableAccess::class.java).toList()
        val (initAccess, condAccess, updateAccess, bodyAccess, innerLoopAccess) =
                iUsages

        doTest("Usages") {
            ivar.localUsages shouldBe iUsages
        }
        doTest("Inside init") {
            initAccess shouldResolveToLocal ivar
        }
        doTest("Inside condition") {
            condAccess shouldResolveToLocal ivar
        }

        doTest("Inside update") {
            updateAccess shouldResolveToLocal ivar
        }

        doTest("Inside body") {
            bodyAccess shouldResolveToLocal ivar
        }

        doTest("Inside init of nested loop") {
            innerLoopAccess shouldResolveToLocal ivar
        }
    }

    parserTestContainer("If stmt without a block") {
        val acu = parser.withProcessing().parse("""
            class Outer extends Sup {
                {
                    if ("" !=null)
                    for (int i = 0, x = i; i < 20; i++) {
                        i++;
                        for (i++; false;) {}
                    }
                }
            }
        """.trimIndent())

        val (ivar, _) =
                acu.descendants(ASTVariableId::class.java).toList()

        val iUsages = acu.descendants(ASTVariableAccess::class.java).toList()
        val (initAccess, condAccess, updateAccess, bodyAccess, innerLoopAccess) =
                iUsages

        doTest("Usages") {
            ivar.localUsages shouldBe iUsages
        }

        doTest("Inside init") {
            initAccess shouldResolveToLocal ivar
        }

        doTest("Inside condition") {
            condAccess shouldResolveToLocal ivar
        }

        doTest("Inside update") {
            updateAccess shouldResolveToLocal ivar
        }

        doTest("Inside body") {
            bodyAccess shouldResolveToLocal ivar
        }

        doTest("Inside init of nested loop") {
            innerLoopAccess shouldResolveToLocal ivar
        }
    }

    parserTestContainer("Record constructors") {
        val acu = parser.withProcessing().parse("""

            record Cons(int x, int... rest) {
                Cons {
                    assert true;
                }

                Cons(int x2, int y2) {
                    assert false;
                    this.x = x2;
                    x2 = x;
                    this.rest = new int[] { y2 };
                }
            }
        """.trimIndent())

        val (xComp, restComp, x2Formal, y2Formal) =
                acu.descendants(ASTVariableId::class.java).toList()

        val (insideCompact, insideRegular) =
                acu.descendants(ASTAssertStatement::class.java).toList()

        val (_, compactCtor, normalCtor) =
                acu.descendants(ASTBodyDeclaration::class.java).filterIs(SymbolDeclaratorNode::class.java).toList()

        doTest("Inside compact ctor: components are in scope as formals") {
            insideCompact.symbolTable.shouldResolveVarTo<JFormalParamSymbol>("x").let {
                it::getDeclaringSymbol shouldBe compactCtor.symbol
            }
            insideCompact.symbolTable.shouldResolveVarTo<JFormalParamSymbol>("rest").let {
                it::getDeclaringSymbol shouldBe compactCtor.symbol
            }
        }

        doTest("Inside normal ctor: components are in scope as fields") {
            insideRegular.symbolTable.shouldResolveVarTo<JFieldSymbol>("x").let {
                it::getModifiers shouldBe (Modifier.PRIVATE or Modifier.FINAL)
            }

            insideRegular.symbolTable.shouldResolveVarTo<JFieldSymbol>("rest").let {
                it::getModifiers shouldBe (Modifier.PRIVATE or Modifier.FINAL)
            }
        }
    }

    parserTest("Foreach with no braces") {
        val acu = parser.parse(
                """
            import java.util.*;
            import java.io.*;
            class Outer {

                public File[] listFiles(FilenameFilter filter) {
                    String ss[] = list();
                    if (ss == null) return null;
                    ArrayList<File> files = new ArrayList<>();
                    for (String s : ss)
                        if ((filter == null) || filter.accept(this, s)) // s should be resolved
                            files.add(new File(s, this));
                    return files.toArray(new File[files.size()]);
                }
            }
        """.trimIndent()
        )

        val foreachVar =
            acu.descendants(ASTVariableId::class.java).first { it.name == "s" }!!

        val foreachBody =
            acu.descendants(ASTForeachStatement::class.java).firstOrThrow().body

        foreachBody shouldResolveToLocal foreachVar
    }

    parserTest("Switch on enum has field names in scope") {
        val acu = parser.parse(
            """
        class Scratch {

            enum SomeEnum { A, B }

            public static void main(String[] args) {
                SomeEnum e = SomeEnum.A;

                switch (e) {
                // neither A nor B are in scope
                case A:
                    return;
                case B:
                    return;
                }

                A.foo(); // this is an ambiguous name

            }
    }

    """.trimIndent()
        )

        val (_, typeSomeEnum) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.typeMirror }

        val (enumA, enumB) =
            acu.descendants(ASTEnumDeclaration::class.java)
                .descendants(ASTVariableId::class.java).toList()

        val (e, caseA, caseB) =
            acu.descendants(ASTVariableAccess::class.java).toList()

        val (qualifiedA) =
            acu.descendants(ASTFieldAccess::class.java).toList()

        val (fooCall) =
            acu.descendants(ASTMethodCall::class.java).toList()

        qualifiedA.referencedSym shouldBe enumA.symbol
        qualifiedA.referencedSym!!.tryGetNode() shouldBe enumA
        qualifiedA shouldHaveType typeSomeEnum

        caseA.referencedSym shouldBe enumA.symbol
        caseA.referencedSym!!.tryGetNode() shouldBe enumA
        caseA shouldHaveType typeSomeEnum

        caseB.referencedSym shouldBe enumB.symbol
        caseB.referencedSym!!.tryGetNode() shouldBe enumB
        caseB shouldHaveType typeSomeEnum

        e shouldHaveType typeSomeEnum

        // symbol tables don't carry that info, this is documented on JSymbolTable#variables()
        caseB.symbolTable.variables().resolve("A").shouldBeEmpty()
        caseB.symbolTable.variables().resolve("B").shouldBeEmpty()

        fooCall.qualifier!!.shouldMatchN {
            ambiguousName("A")
        }
    }

    parserTest("Unnamed variables", javaVersions = since(J22)) {
        val acu = parser.parse("""

        class Scratch {
            interface Consumer<T> { void accept(T t); }

            public static void main(String[] _) { // formal param
                foo();

                try (var _ = new StringReader("")) { // resource
                    foo();
                } catch (Exception _) { // catchParam
                    foo();
                }

                int _ = 2; // local
                foo();

                if (this instanceof Runnable _) { // in pattern matching
                    foo();
                }

                Consumer<Integer> consumer = _ -> { foo(); };
            }
        }

        """.trimIndent())

        for (call in acu.methodCalls()) {
            withClue(call) {
                call.symbolTable.variables().resolve("_").shouldBeEmpty()
            }
        }
    }
})
