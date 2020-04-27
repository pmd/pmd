/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

/**
 * This class is used to generate unique IDs for nodes.
 */
class IdGenerator {
    private int id = 1; // the 0 is taken by the document node


    int getNextId() {
        return id++;
    }
}
