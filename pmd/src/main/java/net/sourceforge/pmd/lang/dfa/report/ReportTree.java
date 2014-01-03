/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.report;

import java.util.Iterator;

import net.sourceforge.pmd.RuleViolation;

public class ReportTree implements Iterable<RuleViolation> {

	private PackageNode rootNode = new PackageNode("");
	private AbstractReportNode level;

	private class TreeIterator implements Iterator<RuleViolation> {

		private AbstractReportNode iterNode = rootNode;
		private boolean hasNextFlag;

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			hasNextFlag = true;
			return getNext() != null;
		}

		public RuleViolation next() {
			if (!hasNextFlag) {
				getNext();
			} else {
				hasNextFlag = false;
			}

			if (iterNode instanceof ViolationNode) {
				return ((ViolationNode) iterNode).getRuleViolation();
			}
			return null;
		}

		/**
		 * It's some kind of left-right-middle search (postorder). It always
		 * returns only leafs. The first node he returns is the most left handed
		 * leaf he can found. Now he's looking for siblings and if there are
		 * any, he starts searching for the next most left handed leaf. If there
		 * are no siblings he goes up to his parent and starts looking for
		 * siblings. If there are any he starts searching for the next most left
		 * handed leaf again. And so on ... until he wants to get the parent of
		 * the root node. Because there is no one, the search stops.
		 */

		private AbstractReportNode getNext() {
			AbstractReportNode node;

			while (true) {
				if (iterNode.isLeaf()) {

					while ((node = iterNode.getNextSibling()) == null) {

						node = iterNode.getParent();
						if (node == null) {
							return null;
						} else {
							iterNode = node;
						}
					}

					iterNode = node;
					if (iterNode.isLeaf()) {
						return iterNode;
					} else {
						continue;
					}
				} else {
					iterNode = iterNode.getFirstChild();
					if (iterNode.isLeaf()) {
						return iterNode;
					} else {
						continue;
					}
				}
			}
		}
	}

	@Override
	public Iterator<RuleViolation> iterator() {
		return new TreeIterator();
	}

	public int size() {
		int count = 0;
		for (Iterator<RuleViolation> i = iterator(); i.hasNext();) {
			i.next();
			count++;
		}
		return count;
	}

	public AbstractReportNode getRootNode() {
		return rootNode;
	}

	/**
	 * Adds the RuleViolation to the tree. Splits the package name. Each
	 * package, class and violation gets there own tree node.
	 */
	public void addRuleViolation(RuleViolation violation) {
		String packageName = violation.getPackageName();
		if (packageName == null) {
			packageName = "";
		}

		level = rootNode;

		int endIndex = packageName.indexOf('.');
		while (true) {
			String parentPackage;
			if (endIndex < 0) {
				parentPackage = packageName;
			} else {
				parentPackage = packageName.substring(0, endIndex);
			}

			if (!isStringInLevel(parentPackage)) {
				PackageNode node = new PackageNode(parentPackage);
				level.addFirst(node);
				// gotoLevel
				level = node;
			}

			if (endIndex < 0) {
				break;
			}
			endIndex = packageName.indexOf('.', endIndex + 1);
		}

		String cl = violation.getClassName();

		if (!isStringInLevel(cl)) {
			ClassNode node = new ClassNode(cl);
			level.addFirst(node);
			// gotoLevel
			level = node;
		}

		/*
		 * Filters duplicated rule violations. Like the comparator in
		 * RuleViolation if he already exists.
		 */
		ViolationNode tmp = new ViolationNode(violation);
		if (!equalsNodeInLevel(level, tmp)) {
			level.add(tmp);
		}
	}

	/**
	 * Checks if node is a child of the level node.
	 */
	private boolean equalsNodeInLevel(AbstractReportNode level,
			AbstractReportNode node) {
		for (int i = 0; i < level.getChildCount(); i++) {
			if (level.getChildAt(i).equalsNode(node)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the packageName or the className is a child of the current
	 * (this.level) node. If it's true, the current node changes to the child
	 * node.
	 */
	private boolean isStringInLevel(String str) {

		for (int i = 0; i < level.getChildCount(); i++) {
			final AbstractReportNode child = level.getChildAt(i);
			final String tmp;
			if (child instanceof PackageNode) {
				tmp = ((PackageNode) child).getPackageName();
			} else if (child instanceof ClassNode) {
				tmp = ((ClassNode) child).getClassName();
			} else {
				return false;
			}

			if (tmp != null && tmp.equals(str)) {
				// goto level
				level = child;
				return true;
			}
		}
		return false;
	}

}
