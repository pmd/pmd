/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class Promotion {

    public void unaryNumericPromotion() {
        double t;
        t = +((byte) 1);
        t = +((short) 1);
        t = +((char) 1);
        t = +((int) 1);
        t = +((long) 1);
        t = +((float) 1);
        t = +((double) 1);
    }

    public void binaryNumericPromotion() {
        double t;
        t = ((byte) 1) + ((byte) 2);
        t = ((byte) 1) + ((short) 2);
        t = ((byte) 1) + ((char) 2);
        t = ((byte) 1) + ((int) 2);
        t = ((byte) 1) + ((long) 2);
        t = ((byte) 1) + ((float) 2);
        t = ((byte) 1) + ((double) 2);
        t = ((short) 1) + ((byte) 2);
        t = ((short) 1) + ((short) 2);
        t = ((short) 1) + ((char) 2);
        t = ((short) 1) + ((int) 2);
        t = ((short) 1) + ((long) 2);
        t = ((short) 1) + ((float) 2);
        t = ((short) 1) + ((double) 2);
        t = ((char) 1) + ((byte) 2);
        t = ((char) 1) + ((short) 2);
        t = ((char) 1) + ((char) 2);
        t = ((char) 1) + ((int) 2);
        t = ((char) 1) + ((long) 2);
        t = ((char) 1) + ((float) 2);
        t = ((char) 1) + ((double) 2);
        t = ((int) 1) + ((byte) 2);
        t = ((int) 1) + ((short) 2);
        t = ((int) 1) + ((char) 2);
        t = ((int) 1) + ((int) 2);
        t = ((int) 1) + ((long) 2);
        t = ((int) 1) + ((float) 2);
        t = ((int) 1) + ((double) 2);
        t = ((long) 1) + ((byte) 2);
        t = ((long) 1) + ((short) 2);
        t = ((long) 1) + ((char) 2);
        t = ((long) 1) + ((int) 2);
        t = ((long) 1) + ((long) 2);
        t = ((long) 1) + ((float) 2);
        t = ((long) 1) + ((double) 2);
        t = ((float) 1) + ((byte) 2);
        t = ((float) 1) + ((short) 2);
        t = ((float) 1) + ((char) 2);
        t = ((float) 1) + ((int) 2);
        t = ((float) 1) + ((long) 2);
        t = ((float) 1) + ((float) 2);
        t = ((float) 1) + ((double) 2);
        t = ((double) 1) + ((byte) 2);
        t = ((double) 1) + ((short) 2);
        t = ((double) 1) + ((char) 2);
        t = ((double) 1) + ((int) 2);
        t = ((double) 1) + ((long) 2);
        t = ((double) 1) + ((float) 2);
        t = ((double) 1) + ((double) 2);
    }

    public void binaryStringPromotion() {
        String t;
        t = "" + 0;
        t = 0 + "";
        t = "" + "";
        t = "" + null;
        t = null + "";
    }
}
