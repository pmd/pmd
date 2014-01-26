/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.awt.Button;
import java.io.File;
import java.io.FileFilter;
import java.security.PrivilegedAction;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Java 8 language syntax
 * 
 * @see <a href="http://cr.openjdk.java.net/~briangoetz/lambda/lambda-state-final.html">State of the Lambda</a>
 * @see <a href="http://download.java.net/jdk8/docs/api/java/util/function/package-summary.html">java.util.function</a>
 */
public class ParserCornerCases18 {
    
    public void lambdas() {
        FileFilter java = (File f) -> f.getName().endsWith(".java");
        FileFilter java2 = f -> f.getName().endsWith(".java");
        FileFilter java3 = (f) -> f.getName().endsWith(".java");

        FileFilter[] filters = new FileFilter[] { 
                f -> f.exists(), f -> f.canRead(), f -> f.getName().startsWith("q") 
        };
        filterFiles(new FileFilter[] { 
                f -> f.exists(), f -> f.canRead(), f -> f.getName().startsWith("q") 
        });

        String user = doPrivileged(() -> System.getProperty("user.name"));

        Callable<String> c = () -> "done";
        Runnable r = () -> { System.out.println("done"); };
        Supplier<Runnable> sup = () -> () -> { System.out.println("hi"); };
        boolean flag = 1 > 2;
        Callable<Integer> c2 = flag ? (() -> 23) : (() -> 42);
        Object o = (Runnable) () -> { System.out.println("hi"); };
        new ParserCornerCases18().r1.run();

        Comparator<String> comparer = (s1, s2) -> s1.compareToIgnoreCase(s2);
        comparer = (s1, s2) -> s1.compareToIgnoreCase(s2);

        Button button = new Button();
        button.addActionListener(e -> System.out.println(e.getModifiers()));

        // grammar/parser: don't get confused with this...
        int initialSizeGlobal = (int) (profilingContext.m_profileItems.size() * (150.0 * 0.30));
    }
    Runnable r1 = () -> { System.out.println(this); };
    
    public Runnable toDoLater() {
        return () -> {
          System.out.println("later");
        };
    }
    
    private String doPrivileged(PrivilegedAction<String> action) {
        return action.run();
    }
    
    private void filterFiles(FileFilter[] filters) {
    }
    
    public void methodReferences() {
        Runnable r = new ParserCornerCases18()::toDoLater;
        Runnable r1 = this::toDoLater;
        ParserCornerCases18 pc = new ParserCornerCases18();
        Runnable r11 = pc::toDoLater;
        Supplier<String> s = super::toString;
        Runnable r2 = ParserCornerCases18::staticMethod;

        IntFunction<int[]> arrayMaker = int[]::new;
        int[] array = arrayMaker.apply(10);  // creates an int[10]
    }
    public static Runnable staticMethod() {
        return () -> System.out.println("run");
    }
    
    
}

interface DefaultIterator<E> {
    boolean hasNext();
    E next();
    void remove();

    default void skip(int i) {
        for (; i > 0 && hasNext(); i--) next();
    }
}
