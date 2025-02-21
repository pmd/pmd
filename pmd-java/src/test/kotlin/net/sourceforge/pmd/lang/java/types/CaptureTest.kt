/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import net.sourceforge.pmd.lang.java.ast.ASTVariableId
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol
import net.sourceforge.pmd.lang.java.types.TypeConversion.capture
import net.sourceforge.pmd.lang.test.ast.IntelliMarker
import net.sourceforge.pmd.lang.test.ast.shouldBeA

/**
 * @author Clément Fournier
 */
class CaptureTest : IntelliMarker, FunSpec({

    with(TypeDslOf(testTypeSystem)) {
        with(gen) {

            test("Capture is a noop on raw types") {

                capture(t_List) shouldBe t_List

            }

            test("Capture merges declared bounds and bounds of wildcard") {

                val t_Scratch = javaParser.parseSomeClass("class Scratch<T extends java.util.List<T>> {}")

                val matcher = captureMatcher(`?`)

                capture(t_Scratch[`?`]) shouldBe t_Scratch[matcher]

                matcher.also {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(`?`) shouldBe true
                    it.upperBound shouldBe t_List[matcher]
                    it.lowerBound shouldBe ts.NULL_TYPE
                }
            }

            test("Capture merges declared bounds and bounds of wildcard (self referential bound)") {

                val t_Scratch = javaParser.parseSomeClass("class Scratch<T extends Scratch<T>> {}")

                val matcher = captureMatcher(`?`)

                capture(t_Scratch[`?`]) shouldBe t_Scratch[matcher]

                matcher.also {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(`?`) shouldBe true
                    it.upperBound shouldBe t_Scratch[matcher]
                    it.lowerBound shouldBe ts.NULL_TYPE
                }
            }

            test("Capture of malformed types") {
                val sym = ts.createUnresolvedAsmSymbol("does.not.Exist")

                val matcher = captureMatcher(`?` extends t_String).also {
                    capture(sym[t_String, `?` extends t_String]) shouldBe sym[t_String, it]
                }

                matcher.also {
                    it.isCaptured shouldBe true
                    it.isCaptureOf(`?` extends t_String) shouldBe true
                }
            }

            test("Capture of recursive types #5006") {
                val acu = javaParser.parse("""
                    class Parent<T extends Parent<T>> {} // Recursive type T

                    class Child extends Parent<Child> {}

                    class Box<T extends Parent<T>> {
                      public void foo(Box<? extends Child> box) {} // <-- The bug is triggered by this line
                    }
                """.trimIndent())

                val (_, child, box) = acu.declaredTypeSignatures()

                val type = acu.methodDeclarations().first()!!.formalParameters.get(0).typeMirror

                 capture(type) shouldBe box[captureMatcher(`?` extends child)]
            }

            test("Capture of recursive types #5096") {
                val acu = javaParser.parse(
                    """

                    class Child<C extends Child<? extends C>> {
                      Child(C t) {
                        super(t); // <-- The bug is triggered by capture of the `t` here
                      } 
                    }
                """.trimIndent()
                )

                val (child) = acu.declaredTypeSignatures()

                val parm = acu.descendants(ASTVariableId::class.java).firstOrThrow()

                parm shouldHaveType child.typeArgs[0]
                val captured = capture(parm.typeMirror)
                captured.shouldBeA<JTypeVar> {
                    it.isCaptured shouldBe true // its bounds are captured

                    it.upperBound.shouldBeA<JClassType> {
                        it.symbol shouldBe child.symbol
                        it.typeArgs[0].shouldBeA<JTypeVar> { cvar ->
                            cvar.isCaptured shouldBe true
                            cvar.upperBound.shouldBeSameInstanceAs(captured)
                        }
                    }
                }
            }

            test("Capture of variable with capturable bound #5493") {
                val acu = javaParser.parse(
                    """
                    
                    interface ObservableList<T> {
                    }
                    
                    interface EventStream<T> {
                        Subscription subscribe(Consumer<? super T> observer);
                    }
                    
                    interface BiFunction<A, B, C> {
                        C apply(A a, B b);
                    }
                    
                    interface Subscription {
                    }
                    
                    interface Function<A, B> {
                        B apply(A a);
                    }
                    
                    interface Supplier<A> {
                        A get();
                    }
                    
                    interface Consumer<A> {
                        void accept(A a);
                    }
                    
                    class Ext {
                    
                        static <T> ObservableList<T> emptyList() {
                        }
                    
                        public static <T> Subscription dynamic(
                            ObservableList<? extends T> elems,
                            BiFunction<? super T, Integer, ? extends Subscription> f) {

                        }
                    }
                    
                    public class Something<E> {
                    
                        ObservableList<E> base;
                        Function<? super E, ? extends EventStream<?>> ticks;
                    
                        void notifyObservers(Supplier<? extends ObservableList<E>> f) {
                        }
                    
                        protected Subscription observeInputs() {
                            // ticks: Function<capt#1 of ? super E, capt#2 of ? extends EventStream<?>>
                            // ticks.apply(e): capt#2 of ? extends EventStream<?>
                            // capture(capt#2 of ...) should not return capt#2 unchanged,
                            // but should return a capture var with its upper bound captured: EventStream<capt#3 of ?>
                            return Ext.dynamic(base, (e, i) -> ticks.apply(e).subscribe(k -> this.notifyObservers(Ext::emptyList)));
                        }
                    }
                """.trimIndent()
                )

                val subscribe = acu.firstMethodCall("subscribe")
                subscribe.overloadSelectionInfo.isFailed shouldBe false
                acu.varId("k") shouldHaveType ts.OBJECT // captureMatcher(`?`) // todo this should probably be projected upwards (and be Object)
            }




        }
    }


})
