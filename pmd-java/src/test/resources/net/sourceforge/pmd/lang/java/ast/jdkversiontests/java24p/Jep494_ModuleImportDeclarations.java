/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import module java.base;
import module java.desktop;

import java.util.List;

/**
 * @see <a href="https://openjdk.org/jeps/494">JEP 494: Module Import Declarations (Second Preview)</a> (Java 24)
 */
public class Jep494_ModuleImportDeclarations {
    public static void main(String[] args) {
        File f = new File(".");
        List<File> myList = new ArrayList<>();
        myList.add(f);
        System.out.println("myList = " + myList);
    }
}
