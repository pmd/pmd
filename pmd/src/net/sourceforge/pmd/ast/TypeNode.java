package net.sourceforge.pmd.ast;

/**
 * This interface allows a Java Class to be associtated with a node.
 */
public interface TypeNode {

	/**
	 * Get the Java Class associated with this node.
	 * @return The Java Class, may return <code>null</code>.
	 */
	public Class<?> getType();

	/**
	 * Set the Java Class associated with this node.
	 * @param type A Java Class
	 */
	public void setType(Class<?> type);
}
