/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
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

    @Test
    @Timeout(2)
    void parseWithoutDeadlock() throws InterruptedException {
        /*
         Deadlock:
         t1 -> locks parse for Outer.Inner and waits for parse lock for Outer
         t2 -> locks parse for Outer, locks parse for GenericInterface and then waits for parse lock for Outer.Inner


        In order to reproduce the deadlock reliably, add the following piece into ParseLock, just at the beginning
        of the synchronized block (line 42):

        // t1 needs to wait after having the lock, so that t2 can go on and wait on the same lock
        if (Thread.currentThread().getName().equals("t1") && this.toString().contains("LazyClassSignature:net/sourceforge/pmd/lang/java/symbols/DeadlockTest$Outer$Inner[<T:L")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
         */

        Thread t1 = new Thread(() -> {
            ASTCompilationUnit class1 = JavaParsingHelper.DEFAULT.parse(
                    "package net.sourceforge.pmd.lang.java.symbols;\n"
                            + "import net.sourceforge.pmd.lang.java.symbols.DeadlockTest.Outer;\n"
                            + "  class Class1 {\n"
                            + "    public static <T> Outer.Inner<T> newInner(Outer<T> grid) {\n"
                            + "      return null;\n"
                            + "    }\n"
                            + "  }\n"
            );
            assertNotNull(class1);
        }, "t1");

        Thread t2 = new Thread(() -> {
            ASTCompilationUnit class2 = JavaParsingHelper.DEFAULT.parse(
                    "package net.sourceforge.pmd.lang.java.symbols;\n"
                            + "import net.sourceforge.pmd.lang.java.symbols.DeadlockTest.Outer;\n"
                            + "  class Class2<M> {\n"
                            + "    protected Outer<M> theOuter;\n"
                            + "  }\n"
            );
            assertNotNull(class2);
        }, "t2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
