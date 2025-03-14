/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance.stringtostring;

public class StringToStringFP {
    private String s;

    public void print(A a) {
        this.s = a.getB().getC().toString(); // line 5 - false positive
        this.s = String.valueOf(a.getB().getC()); // workaround
        System.out.println(s);
    }

    public String getC() {
        return "";
    }

    interface A {
        B getB();
    }

    interface B {
        Character getC();
    }
}
