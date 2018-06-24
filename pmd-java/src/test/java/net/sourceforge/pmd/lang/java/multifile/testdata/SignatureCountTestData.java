/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile.testdata;

import org.apache.commons.lang3.StringUtils;

// Fields:
// 2 private static final
// 3 public
// 1 protected

// Constructors:
// 3 public

// Getters/ Setters:
// 4 public
// 4 package

// Static:
// 2 private
// 2 public

// Methods:
// 1 public
// 1 private
// 2 protected
// 2 protected final
public abstract class SignatureCountTestData {

    private static final int MAX_X = 0;
    private static final int MIN_X = 0;
    public int x;
    public int y;
    public int z;
    protected int t;


    public SignatureCountTestData(int x, int y, int z, int t) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.t = t;
    }


    public SignatureCountTestData(int x, int y, int z) {}


    public SignatureCountTestData(int x, int y) {}


    public int getX() {
        return x;
    }


    void setX(int x) {
        this.x = x;
    }


    public int getY() {
        return y;
    }


    void setY(int y) {
        this.y = y;
    }


    public int getZ() {
        return z;
    }


    void setZ(int z) {
        this.z = z;
    }


    public int getT() {
        return t;
    }


    void setT(int t) {
        this.t = t;
    }


    public boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }


    private boolean isEmpty(String value) {
        return StringUtils.isBlank(value);
    }


    protected boolean isMissing(String value) {
        return StringUtils.isEmpty(value);
    }


    protected boolean areSemanticEquals(String a, String b) {
        return a.equals(b);
    }


    protected abstract String replaceString(String original, String oldString, String newString);


    @Deprecated
    protected abstract void appendXmlEscaped(StringBuilder buf, String src);


    public static boolean startsWithAny(String text, String... prefixes) {
        return false;
    }


    public static boolean isAnyOf(String text, String... tests) {
        return false;
    }


    private static String withoutPrefixes(String text, String... prefixes) {
        return text;
    }


    private static void appendXmlEscaped(StringBuilder buf, String src, boolean supportUTF8) {
    }


}
