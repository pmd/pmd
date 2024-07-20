/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import module java.base;
import module java.desktop;

import java.util.List;

/**
 * @see <a href="https://openjdk.org/jeps/476">JEP 476: Module Import Declarations (Preview)</a> (Java 23)
 */
public class Jep476_ModuleImportDeclarations {
    public static void main(String[] args) {
        File f = new File(".");
        List<File> myList = new ArrayList<>();
        myList.add(f);
        System.out.println("myList = " + myList);
    }
}
