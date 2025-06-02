/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.testdata;

import org.apache.commons.lang3.mutable.MutableInt;

/**
 * @author Clément Fournier
 */
public class GetterDetection {

    private int value;
    private double speed;
    private MutableInt mutX;
    private boolean bool;


    public int getValue() {
        return value;
    }


    public boolean isBool() {
        return bool;
    }


    public int value() {
        return value;
    }


    /*  public double speedModified() {
        return speed * 12 + 1;
    }

    public int mutableInt() {
        return mutX.getValue();
    }

    public MutableInt theMutable() {
        return mutX;
    }

    public int mutableIntIf() {
        if (mutX == null) {
            return 0;
        } else {
            return mutX.getValue();
        }
    }

    public int mutableIntConditional() {
        return mutX == null ? 0 : mutX.getValue();
    }
    */
}
