/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.io.Serializable;

/**
 * This class is used for a test case for the rule MissingSerialVersionUID.
 */
public class MissingSerialVersionUIDBase implements Serializable {
    private static final long serialVersionUID = 1234567L;
}
