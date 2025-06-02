/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.testdata;

import org.apache.commons.lang3.mutable.MutableInt;

/**
 * @author Clément Fournier
 */
public class SetterDetection {
    private int value;
    private double speed;
    private MutableInt mutX;
    private boolean bool;


    public void setValue(int x) {
        value = x;
    }

    public void value(int x) {
        value = x > 0 ? x : -x;
    }

    public void speed(int x) {
        mutX.setValue(x);
    }

    public void mutX(int x) {
        mutX.increment();
    }

    public void bool(int value) {
        this.value = value;
    }
}
