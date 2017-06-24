/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.testdata;

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

    public void putNewValue(int x) {
        value = x;
    }

    public void putNewValueComposed(int x) {
        value += x;
    }

    public void putNewValueIf(int x) {
        if (x > 0) {
            value = x;
        }
    }

    public void putNewValueConditional(int x) {
        value = x > 0 ? x : -x;
    }

    public void updateWithMethod(int x) {
        mutX.setValue(x);
    }

    public void updateWithOtherMethod(int x) {
        mutX.increment();
    }

    public void updateHiddenVal(int value) {
        this.value = value;
    }
}
