package net.sourceforge.pmd.lang.ast.xpath.saxon;

/**
 * This class is used to generate unique IDs for nodes.
 */
public class IdGenerator {
    private int id;

    public int getNextId() {
	return id++;
    }
}
