/*
 * Created on 18.08.2004
 */
package net.sourceforge.pmd.dfa.test;

import net.sourceforge.pmd.dfa.IDataFlowNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author raik
 *         <p/>
 *         Contains methods which represent a data flow. The methods contains an 2d
 *         array. The first dimension represent the index of a node. The second
 *         dimension represent the indices of the children.
 */
public class FlowTest {

    String methodName = "";
    List flow = null;
    int number = 0;

    public boolean run(String methodName, List flow) {

        if (methodName == null || flow == null) return false;

        this.methodName = methodName;
        this.flow = flow;

        Method method;
        Object returnValue = null;

        System.out.print("test class " + methodName + " ");

        try {
            method = this.getClass().getMethod(methodName, null);
            returnValue = method.invoke(this, null);
        } catch (SecurityException e) {
            //e.printStackTrace();
            System.err.println("SecurityException");
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
            System.err.println("NoSuchMethodException");
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            System.err.println("IllegalArgumentException");
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            System.err.println("IllegalAccessException");
        } catch (InvocationTargetException e) {
            //e.printStackTrace();
            System.err.println("InvocationTargetException");
        }

        System.out.println(" -  result: " + returnValue);

        return true;
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
            {4},
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


}
