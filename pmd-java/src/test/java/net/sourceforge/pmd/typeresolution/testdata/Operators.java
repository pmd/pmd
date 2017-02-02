/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.List;

public class Operators {
    public void unaryLogicalOperators() {
        boolean t;
        t = !true;
        t = !false;
    }

    public void binaryLogicalOperators() {
        boolean t;
        t = true | false;
        t = true & false;
        t = true ^ false;
        t = true && false;
        t = true || false;
        t = 1 > 1;
        t = 1 >= 1;
        t = 1 == 1;
        t = 1 != 1;
        t = 1 <= 1;
        t = 1 < 1;
        t = this instanceof List;
        t = this instanceof Operators;
    }

    public void unaryNumericOperators() {
        double t;
        t = +1;
        t = -1;
        t++;
        t--;
        ++t;
        --t;
    }

    public void binaryNumericOperators() {
        long t;
        t = 1 + 1;
        t = 1 - 1;
        t = 1 / 1;
        t = 1 * 1;
        t = 1 % 1;
        t = 1 << 1;
        t = 1 >> 1;
        t = 1 >>> 1;
    }

    public void assignmentOperators() {
        long t;
        t = 1;
        t *= 1;
        t /= 1;
        t %= 1;
        t += 1;
        t -= 1;
        t <<= 1;
        t >>= 1;
        t >>>= 1;
        t &= 1;
        t ^= 1;
        t |= 1;
    }
}
