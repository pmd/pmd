/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Clément Fournier
 */
class FileIdTest {
    @Test
    void testRootStringPath() {
        FileId fileId = FileId.fromPathLikeString("/");
        assertEquals("/", fileId.toAbsolutePath());
    }
}
