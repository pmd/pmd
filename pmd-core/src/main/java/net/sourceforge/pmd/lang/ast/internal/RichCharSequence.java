/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.regex.Pattern;

import org.apache.commons.lang3.CharSequenceUtils;
import org.apache.commons.lang3.StringUtils;

interface RichCharSequence extends CharSequence {

    /** @see StringUtils#indexOf(CharSequence, int) */
    default int indexOf(int searchChar) {
        return StringUtils.indexOf(this, searchChar);
    }


    /** @see StringUtils#indexOf(CharSequence, int, int) */
    default int indexOf(int searchChar, int startPos) {
        return StringUtils.indexOf(this, searchChar, startPos);
    }


    /** @see StringUtils#lastIndexOf(CharSequence, int) */
    default int lastIndexOf(int searchChar) {
        return StringUtils.lastIndexOf(this, searchChar);
    }


    /** @see StringUtils#lastIndexOf(CharSequence, int, int) */
    default int lastIndexOf(int searchChar, int startPos) {
        return StringUtils.lastIndexOf(this, searchChar, startPos);
    }


    default boolean isEmpty() {
        return length() == 0;
    }


    /** @see StringUtils#isWhitespace(CharSequence) */
    default boolean isWhitespace() {
        return StringUtils.isWhitespace(toString());
    }


    /** @see StringUtils#contains(CharSequence, CharSequence) */
    default boolean contains(CharSequence sequence) {
        return StringUtils.contains(this, sequence);
    }


    /** @see StringUtils#contains(CharSequence, int) */
    default boolean contains(int searchChar) {
        return StringUtils.contains(this, searchChar);
    }


    /** @see StringUtils#containsIgnoreCase(CharSequence, CharSequence) */
    default boolean containsIgnoreCase(CharSequence sequence) {
        return StringUtils.containsIgnoreCase(this, sequence);
    }


    /** @see StringUtils#endsWith(CharSequence, CharSequence) */
    default boolean endsWith(CharSequence suffix) {
        return StringUtils.endsWith(this, suffix);
    }


    /** @see StringUtils#startsWith(CharSequence, CharSequence) */
    default boolean startsWith(CharSequence prefix) {
        return StringUtils.startsWith(this, prefix);
    }


    /** @see CharSequenceUtils#subSequence(CharSequence, int) */
    default CharSequence subSequence(int start) {
        return subSequence(start, length());
    }


    /** @see Pattern#matches(String, CharSequence) */
    default boolean matches(String regex) {
        return Pattern.matches(regex, this);
    }

}
