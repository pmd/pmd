package net.sourceforge.pmd.dfa.report;

import net.sourceforge.pmd.IRuleViolation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class ReportTree {

    private PackageNode rootNode = new PackageNode("");
    private AbstractReportNode level;

    private class TreeIterator implements Iterator {

        private AbstractReportNode iterNode = rootNode;
        private boolean hasNextFlag;

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            this.hasNextFlag = true;
            return this.getNext() != null;
        }

        public Object next() {

            if (!this.hasNextFlag) {
                this.getNext();
            } else {
                this.hasNextFlag = false;
            }

            if (this.iterNode instanceof ViolationNode) {
                return ((ViolationNode) this.iterNode).getRuleViolation();
            }
            return null;
        }

        /**
         * It's some kind of left-right-middle search (postorder).
         * It always returns only
         * leafs. The first node he returns is the most left handed leaf he can
         * found. Now he's looking for siblings and if there are any, he starts
         * searching for the next most left handed leaf. If there are no
         * siblings he goes up to his parent and starts looking for siblings.
         * If there are any he starts searching for the next most left handed
         * leaf again. And so on ... until he wants to get the parent of the
         * root node. Because there is no one, the search stops.
         */

        private Object getNext() {
            AbstractReportNode node;

            while (true) {
                if (this.iterNode.isLeaf()) {

                    while ((node = (this.iterNode).getNextSibling()) == null) {

                        node = this.iterNode.getParent();
                        if (node == null) {
                            return null;
                        } else {
                            this.iterNode = node;
                        }
                    }

                    this.iterNode = node;
                    if (this.iterNode.isLeaf()) {
                        return this.iterNode;
                    } else {
                        continue;
                    }
                } else {
                    this.iterNode = this.iterNode.getFirstChild();
                    if (this.iterNode.isLeaf()) {
                        return this.iterNode;
                    } else {
                        continue;
                    }
                }
            }
        }
    }


    public Iterator iterator() {
        return new TreeIterator();
    }

    public int size() {
        int count = 0;
        for (Iterator i = iterator(); i.hasNext();) {
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
    public void addRuleViolation(IRuleViolation violation) {
        String pack = violation.getPackageName();
        String[] a = {};
        if (pack == null) {
            a = new String[]{""};
        } else if (pack.indexOf('.') != -1) {
            // TODO Remove when minimal runtime support is >= JDK 1.4
            try {
                Method split = String.class.getMethod("split", new Class[]{String.class});
                if (split != null) {
                    //        // Compatible with >= JDK 1.4
                    Object[] tmp = (Object[]) split.invoke(pack, new Object[]{"\\."});
                    a = new String[tmp.length];
                    for (int i = 0; i < tmp.length; i++) {
                        a[i] = (String) tmp[i];
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new InternalError("Runtime reports to be >= JDK 1.4 yet String.split(java.lang.String) is broken.");
            } catch (NoSuchMethodException nsme) {
                // Compatible with < JDK 1.4
                StringTokenizer toker = new StringTokenizer(pack, ".");
                List parts = new ArrayList();
                while (toker.hasMoreTokens()) {
                    parts.add(toker.nextToken());
                }
                a = (String[]) parts.toArray(new String[parts.size()]);
            }
        } else {
            a = new String[]{pack};
        }

        this.level = this.rootNode;
        String plugedPackageName = "";

        for (int i = 0; i < a.length; i++) {
            String packageName = a[i];
            plugedPackageName += packageName + '.';

            if (!this.isStringInLevel(plugedPackageName)) {
                PackageNode node = new PackageNode(plugedPackageName);
                this.level.addFirst(node);
                // gotoLevel
                this.level = node;
            }
        }

        String cl = violation.getClassName();

        if (!this.isStringInLevel(cl)) {
            ClassNode node = new ClassNode(cl);
            this.level.addFirst(node);
            // gotoLevel
            this.level = node;
        }

        /*
         * Filters dublicated rule violations. Like the comparator in
         * RuleViolation if he already exists.
         */
        ViolationNode tmp = new ViolationNode(violation);
        if (!this.equalsNodeInLevel(this.level, tmp)) {
            this.level.add(tmp);
        }
    }

    /**
     * Checks if node is a child of the level node.
     */
    private boolean equalsNodeInLevel(AbstractReportNode level, AbstractReportNode node) {
        for (int i = 0; i < level.getChildCount(); i++) {
            if ((level.getChildAt(i)).equalsNode(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the packageName or the className is a child of the current
     * (this.level) node. If it's true, the current node changes to the
     * child node.
     */
    private boolean isStringInLevel(String str) {

        for (int i = 0; i < this.level.getChildCount(); i++) {
            AbstractReportNode child = this.level.getChildAt(i);
            String tmp = null;

            if (child instanceof PackageNode) {
                tmp = ((PackageNode) child).getPackageName();
            }
            if (child instanceof ClassNode) {
                tmp = ((ClassNode) child).getClassName();
            }

            if (tmp == null) {
                return false;
            }

            if (tmp.equals(str)) {
                // goto level
                this.level = child;
                return true;
            }
        }
        return false;
    }

}
