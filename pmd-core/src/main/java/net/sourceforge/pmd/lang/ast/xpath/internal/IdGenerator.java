/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

/**
 * This class is used to generate unique IDs for nodes.
 */
class IdGenerator {
    private int id;


    int getNextId() {
        return id++;
    }
}
