package test.net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.dfa.IDataFlowNode;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class AcceptanceTestRule extends AbstractRule {

    private String methodName;
    private List flow;

    public boolean usesDFA() {
        return true;
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        super.visit(node, data);
        String methodName = node.getImage();
        IDataFlowNode inode = node.getDataFlowNode();
        if (inode == null || inode.getFlow() == null) {
            return data;
        }
        boolean result = check(methodName, inode.getFlow());
        if (!result) {
            System.out.println(methodName + " failed");
        }
        return data;
    }

    private boolean check(String methodName, List flow) {
        if (methodName == null || flow == null) {
            return false;
        }
        this.methodName = methodName;
        this.flow = flow;
        try {
            return ((Boolean)(getClass().getMethod(methodName, null).invoke(this, null))).booleanValue();
        } catch (SecurityException e) {
            //e.printStackTrace();
            System.err.println("SecurityException");
            return false;
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
            System.err.println("NoSuchMethodException");
            return false;
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            System.err.println("IllegalArgumentException");
            return false;
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            System.err.println("IllegalAccessException");
            return false;
        } catch (InvocationTargetException e) {
            //e.printStackTrace();
            e.getCause().printStackTrace();
            System.err.println("InvocationTargetException");
            return false;
        }
    }

    private boolean test(int array[][]) {
        for (int i = 0; i < this.flow.size(); i++) {
            IDataFlowNode inode = (IDataFlowNode) flow.get(i);
            for (int j = 0; j < inode.getChildren().size(); j++) {
                IDataFlowNode child = (IDataFlowNode) inode.getChildren().get(j);
                if (array[i][j] != child.getIndex()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *  These methods represent data flows. They each contain a 2d
     *  array:
     *  - first dimension: the index of a node
     *  - second dimension: the indices of the children
     */
    public boolean test_1() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 6},
            {5},
            {6},
            {}
        };
        return this.test(array);
    }

    public boolean test_2() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 7},
            {3},
            {6},
            {4},
            {}
        };
        return this.test(array);
    }

    public boolean test_3() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 5},
            {3},
            {}
        };
        return this.test(array);
    }

    public boolean test_4() {
        int array[][] = {
            {1},
            {2},
            {3},
            {}
        };
        return this.test(array);
    }

    public boolean test_5() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4},
            {}
        };
        return this.test(array);
    }

    public boolean test_6() {
        return test_5();
    }

    public boolean test_7() {
        return test_5();
    }

    public boolean test_8() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 5},
            {3},
            {}
        };
        return this.test(array);
    }

    public boolean test_9() {
        return test_5();
    }

    public boolean test_10() {
        return test_8();
    }

    public boolean test_11() {
        return test_8();
    }

    public boolean test_12() {
        int array[][] = {
            {1},
            {2},
            {3, 4},
            {2},
            {}
        };
        return this.test(array);
    }

    public boolean test_13() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 9},
            {3},
            {6},
            {7, 8},
            {8},
            {4},
            {}
        };
        return this.test(array);
    }

    public boolean test_14() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 8},
            {3},
            {6},
            {7, 4},
            {4},
            {}
        };
        return this.test(array);
    }

    public boolean test_15() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 7},
            {3},
            {6, 4},
            {4},
            {}
        };
        return this.test(array);
    }

    public boolean test_16() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 8},
            {3},
            {6, 7},
            {4},
            {4},
            {}
        };
        return this.test(array);
    }

    public boolean test_17() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 10},
            {3},
            {6, 7},
            {4},
            {8, 9},
            {4},
            {4},
            {}
        };
        return this.test(array);
    }

    public boolean test_18() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 9},
            {3},
            {6},
            {8, 4},
            {6},
            {7},
            {}
        };
        return this.test(array);
    }

    public boolean test_19() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 5},
            {10},
            {6, 7},
            {10},
            {8, 9},
            {10},
            {10},
            {}
        };
        return this.test(array);
    }

    public boolean test_20() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 6},
            {5, 7},
            {7},
            {7},
            {}
        };
        return this.test(array);
    }

    public boolean test_21() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 9},
            {5},
            {7, 8},
            {5},
            {6},
            {11},
            {10, 11},
            {11},
            {}
        };
        return this.test(array);
    }

    public boolean test_22() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 8},
            {5},
            {7, 9},
            {5},
            {6},
            {9},
            {}
        };
        return this.test(array);
    }

    public boolean test_23() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 8},
            {5},
            {7, 10},
            {5},
            {6},
            {9, 10},
            {10},
            {}
        };
        return this.test(array);
    }

    public boolean test_24() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4, 9},
            {5},
            {7, 11},
            {5},
            {8, 6},
            {6},
            {10, 11},
            {11},
            {}
        };
        return this.test(array);
    }

    public boolean test_25() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4,5},
            {5},
            {}
        };
        return this.test(array);
    }

    public boolean test_26() {
        int array[][] = {
            {1},
            {2},
            {3},
            {4},
            {5, 6},
            {6},
            {7},
            {8, 3},
            {9},
            {}
        };
        return this.test(array);
    }

    public boolean test_27() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 9},
            {3},
            {6},
            {7},
            {8},
            {6, 4},
            {}
        };
        return this.test(array);
    }

    public boolean test_28() {
        int array[][] = {
            {1},
            {2},
            {3},
            {5, 14},
            {3},
            {6},
            {7},
            {8, 12},
            {9},
            {10, 12},
            {11},
            {12},
            {13},
            {6, 4},
            {}
        };
        return this.test(array);
    }


    public static final String TEST =
        "public class TestClass {" + PMD.EOL +
        "    public void test_1() {" + PMD.EOL +
        "        int x = 0;" + PMD.EOL +
        "        if (x == 0) {" + PMD.EOL +
        "            x++;" + PMD.EOL +
        "            x = 0;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_2() {" + PMD.EOL +
        "        for (int i = 0; i < 1; i++) {" + PMD.EOL +
        "            i++;" + PMD.EOL +
        "            i = 8;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_3() {" + PMD.EOL +
        "        for (int i = 0; i < 1; i++) {" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_4() {" + PMD.EOL +
        "        for (; ;) {" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_6() {" + PMD.EOL +
        "        for (int i = 0; ;) {" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_7() {" + PMD.EOL +
        "        for (int i = 0; i < 0;) {" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_8() {" + PMD.EOL +
        "        for (int i = 0; ; i++) {" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_9() {" + PMD.EOL +
        "        int i = 0;" + PMD.EOL +
        "        for (; i < 0;) {" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_10() {" + PMD.EOL +
        "        int i = 0;" + PMD.EOL +
        "        for (; i < 0; i++) {" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_11() {" + PMD.EOL +
        "        int i = 0;" + PMD.EOL +
        "        for (; ; i++) {" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_12() {" + PMD.EOL +
        "        for (; ;) {" + PMD.EOL +
        "            int i = 0;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_13() {" + PMD.EOL +
        "        for (int i = 0; i < 0; i++) {" + PMD.EOL +
        "            i = 9;" + PMD.EOL +
        "            if (i < 8) {" + PMD.EOL +
        "                i = 7;" + PMD.EOL +
        "            }" + PMD.EOL +
        "            i = 6;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_14() {" + PMD.EOL +
        "        for (int i = 0; i < 0; i++) {" + PMD.EOL +
        "            i = 9;" + PMD.EOL +
        "            if (i < 8) {" + PMD.EOL +
        "                i = 7;" + PMD.EOL +
        "            }" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_15() {" + PMD.EOL +
        "        for (int i = 0; i < 0; i++) {" + PMD.EOL +
        "            if (i < 8) {" + PMD.EOL +
        "                i = 7;" + PMD.EOL +
        "            }" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_16() {" + PMD.EOL +
        "        for (int i = 0; i < 0; i++) {" + PMD.EOL +
        "            if (i < 8) {" + PMD.EOL +
        "                i = 7;" + PMD.EOL +
        "            } else {" + PMD.EOL +
        "                i = 6;" + PMD.EOL +
        "            }" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_17() {" + PMD.EOL +
        "        for (int i = 0; i < 0; i++) {" + PMD.EOL +
        "            if (i < 6) {" + PMD.EOL +
        "                i = 7;" + PMD.EOL +
        "            } else if (i > 8) {" + PMD.EOL +
        "                i = 9;" + PMD.EOL +
        "            } else {" + PMD.EOL +
        "                i = 10;" + PMD.EOL +
        "            }" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_18() {" + PMD.EOL +
        "        for (int i = 0; i < 0; i++) {" + PMD.EOL +
        "            for (int j = 0; j < 0; j++) {" + PMD.EOL +
        "                j++;" + PMD.EOL +
        "            }" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_19() {" + PMD.EOL +
        "        int i = 0;" + PMD.EOL +
        "        if (i == 1) {" + PMD.EOL +
        "            i = 2;" + PMD.EOL +
        "        } else if (i == 3) {" + PMD.EOL +
        "            i = 4;" + PMD.EOL +
        "        } else if (i == 5) {" + PMD.EOL +
        "            i = 6;" + PMD.EOL +
        "        } else {" + PMD.EOL +
        "            i = 7;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_20() {" + PMD.EOL +
        "        int i = 0;" + PMD.EOL +
        "        if (i == 1) {" + PMD.EOL +
        "            if (i == 2) {" + PMD.EOL +
        "                i = 3;" + PMD.EOL +
        "            }" + PMD.EOL +
        "        } else {" + PMD.EOL +
        "            i = 7;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_21() {" + PMD.EOL +
        "        int i = 0;" + PMD.EOL +
        "        if (i == 1) {" + PMD.EOL +
        "            for (i = 3; i < 4; i++) {" + PMD.EOL +
        "                i = 5;" + PMD.EOL +
        "            }" + PMD.EOL +
        "            i++;" + PMD.EOL +
        "        } else if (i < 6) {" + PMD.EOL +
        "            i = 7;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_22() {" + PMD.EOL +
        "        int i = 0;" + PMD.EOL +
        "        if (i == 1) {" + PMD.EOL +
        "            for (i = 3; i < 4; i++) {" + PMD.EOL +
        "                i = 5;" + PMD.EOL +
        "            }" + PMD.EOL +
        "        } else {" + PMD.EOL +
        "            i = 7;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_23() {" + PMD.EOL +
        "        int i = 0;" + PMD.EOL +
        "        if (i == 1) {" + PMD.EOL +
        "            for (i = 3; i < 4; i++) {" + PMD.EOL +
        "                i = 5;" + PMD.EOL +
        "            }" + PMD.EOL +
        "        } else if (i < 6) {" + PMD.EOL +
        "            i = 7;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_24() {" + PMD.EOL +
        "        int x = 0;" + PMD.EOL +
        "        if (x > 2) {" + PMD.EOL +
        "            for (int i = 0; i < 1; i++) {" + PMD.EOL +
        "                if (x > 3) {" + PMD.EOL +
        "                    x++;" + PMD.EOL +
        "                }" + PMD.EOL +
        "            }" + PMD.EOL +
        "        } else if (x > 4) {" + PMD.EOL +
        "            x++;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_25() {" + PMD.EOL +
        "        int x = 0;" + PMD.EOL +
        "        switch (x) {" + PMD.EOL +
        "            default:" + PMD.EOL +
        "                x = 9;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_26() {" + PMD.EOL +
        "        int x = 0;" + PMD.EOL +
        "        do {" + PMD.EOL +
        "            if (x > 0) {" + PMD.EOL +
        "                x++;" + PMD.EOL +
        "            }" + PMD.EOL +
        "            x++;" + PMD.EOL +
        "        } while (x < 9);" + PMD.EOL +
        "        x++;" + PMD.EOL +
        "    }" + PMD.EOL +
        "//     ----------------------------------------------------------------------------" + PMD.EOL +
        "    public void test_27() {" + PMD.EOL +
        "        for (int i = 0; i < 36; i++) {" + PMD.EOL +
        "            int x = 0;" + PMD.EOL +
        "            do {" + PMD.EOL +
        "                x++;" + PMD.EOL +
        "            } while (x < 9);" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "    private void test_28() {" + PMD.EOL +
        "        for (int i = 0; i < 36; i++) {" + PMD.EOL +
        "            int x = 0;" + PMD.EOL +
        "            do {" + PMD.EOL +
        "                if (x > 0) {" + PMD.EOL +
        "                    x++;" + PMD.EOL +
        "                    switch (i) {" + PMD.EOL +
        "                        case 0:" + PMD.EOL +
        "                            x = 0;" + PMD.EOL +
        "                            break;" + PMD.EOL +
        "                    }" + PMD.EOL +
        "                }" + PMD.EOL +
        "                x++;" + PMD.EOL +
        "            } while (x < 9);" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
}
