/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.util;

import junit.framework.TestCase;
import net.sourceforge.pmd.util.StringUtil;

public class StringUtilTest extends TestCase {

    public void testReplaceWithOneChar() {
        assertEquals("faa", StringUtil.replaceString("foo", 'o', "a"));
    }

    public void testReplaceWithMultipleChars() {
        assertEquals("faaaa", StringUtil.replaceString("foo", 'o', "aa"));
    }

    public void testReplaceStringWithString() {
        assertEquals("foo]]&gt;bar", StringUtil.replaceString("foo]]>bar", "]]>", "]]&gt;"));
    }

    public void testReplaceStringWithString2() {
        assertEquals("foobar", StringUtil.replaceString("foobar", "]]>", "]]&gt;"));
    }

}
