/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryfullyqualifiedname;



public class TestClass {

    protected class SomeInnerClass {
        public void alsoDoSomething() {
            System.out.println("alsoDoSomething");
        }
    }
}
