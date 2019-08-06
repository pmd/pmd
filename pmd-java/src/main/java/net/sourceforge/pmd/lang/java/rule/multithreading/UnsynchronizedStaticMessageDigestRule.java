/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import java.security.MessageDigest;

/**
 * Using a MessageDigest which is static can cause
 * unexpected results when used in a multi-threaded environment. This rule will
 * find static MessageDigest which are used in an unsynchronized manner.
 */
public class UnsynchronizedStaticMessageDigestRule extends UnsynchronizedStaticFormatterRule {

    public UnsynchronizedStaticMessageDigestRule() {
        super(MessageDigest.class);
    }
}