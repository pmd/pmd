/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpenTagRegisterTest {

    private OpenTagRegister tagList;

    private int elmId = 0;

    @BeforeEach
    void newRegister() {
        tagList = new OpenTagRegister();
    }

    /**
     * &lt;a&gt; &lt;b&gt; &lt;/a&gt;
     */
    @Test
    void testSimpleNesting() {
        ASTElement elm = element("a");
        ASTElement elm2 = element("b");

        tagList.openTag(elm);
        tagList.openTag(elm2);
        tagList.closeTag(elm);

        assertFalse(elm.isUnclosed());
        assertTrue(elm2.isUnclosed());
    }

    /**
     * &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/a&gt;
     */
    @Test
    void doubleNesting() {
        ASTElement elm = element("a");
        ASTElement elm2 = element("b");
        ASTElement elm3 = element("b");

        tagList.openTag(elm);
        tagList.openTag(elm2);
        tagList.openTag(elm3);
        tagList.closeTag(elm);

        assertFalse(elm.isUnclosed());
        assertTrue(elm2.isUnclosed());
        assertTrue(elm3.isUnclosed());
    }

    /**
     * &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/x&gt; &lt;/a&gt; &lt;/x&gt;
     */
    @Test
    void unopenedTags() {
        ASTElement elm = element("x");
        ASTElement elm2 = element("a");
        ASTElement elm3 = element("b");
        ASTElement elm4 = element("b");

        tagList.openTag(elm);
        tagList.openTag(elm2);
        tagList.openTag(elm3);
        tagList.openTag(elm4);
        tagList.closeTag(elm);
        tagList.closeTag(elm2);
        tagList.closeTag(elm3);
        tagList.closeTag(elm);

        assertFalse(elm.isUnclosed());
        assertTrue(elm2.isUnclosed());
        assertTrue(elm3.isUnclosed());
        assertTrue(elm4.isUnclosed());
    }

    /**
     * &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/z&gt; &lt;/a&gt; &lt;/x&gt;
     *
     */
    @Test
    void interleavedTags() {
        ASTElement elm = element("x");
        ASTElement elm2 = element("a");
        ASTElement elm3 = element("b");
        ASTElement elm4 = element("b");
        ASTElement elm5 = element("z");

        tagList.openTag(elm);
        tagList.openTag(elm2);
        tagList.openTag(elm3);
        tagList.openTag(elm4); // open b
        tagList.closeTag(elm5); // close z
        tagList.closeTag(elm2); // close a
        tagList.closeTag(elm); // close x

        assertFalse(elm.isUnclosed()); // x is closed
        assertFalse(elm2.isUnclosed()); // a is closed
        assertTrue(elm3.isUnclosed());
        assertTrue(elm4.isUnclosed());
        // elm5 ???
    }

    /**
     * &lt;a&gt; &lt;x&gt; &lt;a&gt; &lt;b&gt; &lt;b&gt; &lt;/z&gt; &lt;/a&gt;
     * &lt;/x&gt;
     */
    @Test
    void openedIsolatedTag() {
        ASTElement a = element("a");
        ASTElement x = element("x");
        ASTElement a2 = element("a");
        ASTElement b = element("b");
        ASTElement b2 = element("b");
        ASTElement z = element("z");

        tagList.openTag(a);
        tagList.openTag(x);
        tagList.openTag(a2);
        tagList.openTag(b);
        tagList.openTag(b2);
        tagList.closeTag(z); // close z
        tagList.closeTag(a2); // close second a
        tagList.closeTag(x); // close x

        assertTrue(a.isUnclosed()); // first a is unclosed
        assertFalse(x.isUnclosed()); // x is closed
        assertFalse(a2.isUnclosed()); // a is closed

        assertTrue(b.isUnclosed());
        assertTrue(b2.isUnclosed());
    }

    private ASTElement element(String name) {
        ASTElement elm = new ASTElement(elmId++);
        elm.setName(name);
        return elm;
    }

}
