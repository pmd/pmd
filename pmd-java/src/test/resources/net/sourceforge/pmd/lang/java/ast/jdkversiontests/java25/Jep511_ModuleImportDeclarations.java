/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import module java.base;
import module java.desktop;

import java.util.List;

/**
 * @see <a href="https://openjdk.org/jeps/511">JEP 511: Module Import Declarations</a> (Java 25)
 */
public class Jep511_ModuleImportDeclarations {
    public static void main(String[] args) {
        File f = new File(".");
        List<File> myList = new ArrayList<>();
        myList.add(f);
        System.out.println("myList = " + myList);
    }
}
