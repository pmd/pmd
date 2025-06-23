/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

/**
 * Tests to help analyze  [java] Deadlock when executing PMD in multiple threads #5293.
 *
 * @see <a href="https://github.com/pmd/pmd/issues/5293">[java] Deadlock when executing PMD in multiple threads #5293</a>
 */
class DeadlockTest {

    abstract static class Outer<T> implements GenericInterface<Outer<T>, GenericClass<T>> {
        // must be a nested class, that is reusing the type param T of the outer class
        abstract static class Inner<T> {
            Inner(Outer<T> grid) { }
        }
    }

    static class GenericBaseClass<T> { }

    interface GenericInterface<T, S> { }

    abstract static class GenericClass<T> extends GenericBaseClass<Outer.Inner<T>> { }

    @Timeout(2)
    @RepeatedTest(50)
    void parseWithoutDeadlock() throws InterruptedException {
        /*
         Deadlock:
         t1 -> locks parse for Outer.Inner and waits for parse lock for Outer
          ├─ t1 waiting on        : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer$Inner[<T:Ljava/lang/Object;>Ljava/lang/Object;],status=NOT_PARSED}
          └─ t1 locked            : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer$Inner[<T:Ljava/lang/Object;>Ljava/lang/Object;],status=NOT_PARSED}
             └─ t1 waiting on     : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer[<T:Ljava/lang/Object;>Ljava/lang/Object;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericInterface<Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer<TT;>;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericClass<TT;>;>;],status=BEING_PARSED}
         t2 -> locks parse for Outer, locks parse for GenericInterface and then waits for parse lock for Outer.Inner
          ├─ t2 waiting on        : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer[<T:Ljava/lang/Object;>Ljava/lang/Object;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericInterface<Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer<TT;>;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericClass<TT;>;>;],status=NOT_PARSED}
          └─ t2 locked            : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer[<T:Ljava/lang/Object;>Ljava/lang/Object;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericInterface<Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer<TT;>;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericClass<TT;>;>;],status=NOT_PARSED}
             ├─ t2 waiting on     : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest[null],status=NOT_PARSED}
             ├─ t2 locked         : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest[null],status=NOT_PARSED}
             ├─ t2 released       : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest[null],status=FULL}
             ├─ t2 waiting on     : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer[<T:Ljava/lang/Object;>Ljava/lang/Object;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericInterface<Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer<TT;>;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericClass<TT;>;>;],status=BEING_PARSED}
             ├─ t2 locked         : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer[<T:Ljava/lang/Object;>Ljava/lang/Object;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericInterface<Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer<TT;>;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericClass<TT;>;>;],status=BEING_PARSED}
             ├─ t2 released       : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer[<T:Ljava/lang/Object;>Ljava/lang/Object;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericInterface<Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer<TT;>;Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericClass<TT;>;>;],status=BEING_PARSED}
             └─ t2 waiting on     : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericClass[<T:Ljava/lang/Object;>Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericBaseClass<Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer$Inner<TT;>;>;],status=NOT_PARSED}
               ├─ t2 locked       : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericClass[<T:Ljava/lang/Object;>Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$GenericBaseClass<Lnet/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer$Inner<TT;>;>;],status=NOT_PARSED}
               └─ t2 waiting on   : ParseLock{name=LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer$Inner[<T:Ljava/lang/Object;>Ljava/lang/Object;],status=NOT_PARSED}


        In order to reproduce the deadlock reliably, add the following piece into ParseLock, just at the beginning
        of the synchronized block (line 42):

        // t1 needs to wait after having the lock, so that t2 can go on and wait on the same lock
        if (Thread.currentThread().getName().equals("t1") && this.toString().contains("LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer$Inner[<T:L")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        And then, introduce a bug again. One way to make the test fail is:
        Comment out the method "public int getTypeParameterCount()", so that it is inherited again.
        Add the following method:
            @Override
            public boolean isGeneric() {
                parseLock.ensureParsed();
                return signature.isGeneric();
            }
         */

        List<Throwable> exceptions = new ArrayList<>();
        Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> {
            exceptions.add(e);
            e.printStackTrace();
        };

        Thread t1 = new Thread(() -> {
            ASTCompilationUnit class1 = JavaParsingHelper.DEFAULT.parse(
                    "package net.sourceforge.pmd.lang.java.symbols;\n"
                            + "import net.sourceforge.pmd.lang.java.symbols.DeadlockTest.Outer;\n"
                            + "  class Class1 {\n"
                            + "    public static <X> Outer.Inner<X> newInner(Outer<X> grid) {\n"
                            + "      return null;\n"
                            + "    }\n"
                            + "  }\n"
            );
            assertNotNull(class1);

            // Outer.Inner<X> = return type of method "newInner"
            List<ASTClassType> classTypes = class1.descendants(ASTClassType.class).toList();
            ASTClassType outerInner = classTypes.get(0);
            assertGenericClassType(outerInner, "Inner", "X", "T");

            // Outer = qualifier of Outer.Inner<X>
            ASTClassType outer = classTypes.get(1);
            assertEquals("Outer", outer.getSimpleName());
            assertNull(outer.getTypeArguments());

            // Outer<X> = formal parameter type of method newInner
            ASTClassType outerFormalParam = classTypes.get(3);
            assertGenericClassType(outerFormalParam, "Outer", "X", "T");
        }, "t1");
        t1.setUncaughtExceptionHandler(exceptionHandler);

        Thread t2 = new Thread(() -> {
            ASTCompilationUnit class2 = JavaParsingHelper.DEFAULT.parse(
                    "package net.sourceforge.pmd.lang.java.symbols;\n"
                            + "import net.sourceforge.pmd.lang.java.symbols.DeadlockTest.Outer;\n"
                            + "  class Class2<M> {\n"
                            + "    protected Outer<M> theOuter;\n"
                            + "  }\n"
            );
            assertNotNull(class2);

            // Outer<M> = type of field "theOuter"
            ASTClassType firstClassType = class2.descendants(ASTClassType.class).first();
            assertNotNull(firstClassType);
            assertGenericClassType(firstClassType, "Outer", "M", "T");
        }, "t2");
        t2.setUncaughtExceptionHandler(exceptionHandler);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertAll(exceptions.stream()
                        .map(e -> () -> {
                            throw e;
                        }));
    }

    private static void assertGenericClassType(ASTClassType classType, String simpleName, String actualTypeParamName, String originalTypeParamName) {
        assertEquals(simpleName, classType.getSimpleName());
        assertEquals(1, classType.getTypeArguments().size());
        assertEquals(actualTypeParamName, ((ASTClassType) classType.getTypeArguments().get(0)).getSimpleName());
        JTypeParameterOwnerSymbol symbol = (JTypeParameterOwnerSymbol) classType.getTypeMirror().getSymbol();
        assertEquals(1, symbol.getTypeParameterCount());
        assertEquals(originalTypeParamName, symbol.getTypeParameters().get(0).getName());
    }
}
