/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

/*
 * Created on 18.08.2004
 */
public class AcceptanceTest extends BaseNonParserTest {

    @Test
    public void testbook() {
        getOrderedNodes(ASTMethodDeclarator.class, FOO);
    }

    private static final String FOO = "class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  int x = 2;" + PMD.EOL
            + " }" + PMD.EOL + "}";

    @Test
    public void testLabelledBreakLockup() {
        getOrderedNodes(ASTMethodDeclarator.class, LABELLED_BREAK_LOCKUP);
    }

    private static final String LABELLED_BREAK_LOCKUP = "class Foo {" + PMD.EOL + " void bar(int x) {" + PMD.EOL
            + "  here: if (x>2) {" + PMD.EOL + "   break here;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + "}";

    private boolean check(int[][] array, List<ASTMethodDeclarator> methodNodes) {
        for (int i = 0; i < methodNodes.size(); i++) {
            ASTMethodDeclarator decl = methodNodes.get(i);
            DataFlowNode inode = decl.getDataFlowNode();
            for (int j = 0; j < inode.getChildren().size(); j++) {
                DataFlowNode child = inode.getChildren().get(j);
                if (array[i][j] != child.getIndex() - 1) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    public void test1() {
        assertTrue(check(TEST1_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST1)));
    }

    @Test
    public void test2() {
        assertTrue(check(TEST2_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST2)));
    }

    @Test
    public void test3() {
        assertTrue(check(TEST3_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST3)));
    }

    @Test
    public void test4() {
        assertTrue(check(TEST4_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST4)));
    }

    @Test
    public void test6() {
        assertTrue(check(TEST5_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST6)));
    }

    @Test
    public void test7() {
        assertTrue(check(TEST5_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST7)));
    }

    @Test
    public void test8() {
        assertTrue(check(TEST8_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST8)));
    }

    @Test
    public void test9() {
        assertTrue(check(TEST5_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST9)));
    }

    @Test
    public void test10() {
        assertTrue(check(TEST8_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST10)));
    }

    @Test
    public void test11() {
        assertTrue(check(TEST8_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST11)));
    }

    @Test
    public void test12() {
        assertTrue(check(TEST12_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST12)));
    }

    @Test
    public void test13() {
        assertTrue(check(TEST13_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST13)));
    }

    @Test
    public void test14() {
        assertTrue(check(TEST14_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST14)));
    }

    @Test
    public void test15() {
        assertTrue(check(TEST15_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST15)));
    }

    @Test
    public void test16() {
        assertTrue(check(TEST16_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST16)));
    }

    @Test
    public void test17() {
        assertTrue(check(TEST17_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST17)));
    }

    @Test
    public void test18() {
        assertTrue(check(TEST18_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST18)));
    }

    @Test
    public void test19() {
        assertTrue(check(TEST19_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST19)));
    }

    @Test
    public void test20() {
        assertTrue(check(TEST20_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST20)));
    }

    @Test
    public void test21() {
        assertTrue(check(TEST21_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST21)));
    }

    @Test
    public void test22() {
        assertTrue(check(TEST22_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST22)));
    }

    @Test
    public void test23() {
        assertTrue(check(TEST23_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST23)));
    }

    @Test
    public void test24() {
        assertTrue(check(TEST24_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST24)));
    }

    @Test
    public void test25() {
        assertTrue(check(TEST25_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST25)));
    }

    @Test
    public void test26() {
        assertTrue(check(TEST26_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST26)));
    }

    @Test
    public void test27() {
        assertTrue(check(TEST27_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST27)));
    }

    @Test
    public void test28() {
        assertTrue(check(TEST28_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST28)));
    }

    @Test
    public void test29() {
        assertTrue(check(TEST29_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST29)));
    }

    @Test
    public void test30() {
        assertTrue(check(TEST30_NODES, getOrderedNodes(ASTMethodDeclarator.class, TEST30)));
    }

    // first dimension: the index of a node
    // second dimension: the indices of the children
    private static final int[][] TEST1_NODES = { { 1, }, { 2, }, { 3, }, { 4, 6, }, { 5, }, { 6, }, {}, };

    private static final String TEST1 = "class Foo {" + PMD.EOL + " void test_1() {" + PMD.EOL + "  int x = 0;"
            + PMD.EOL + "  if (x == 0) {" + PMD.EOL + "   x++;" + PMD.EOL + "   x = 0;" + PMD.EOL + "  }" + PMD.EOL
            + " }" + PMD.EOL + " }";

    private static final int[][] TEST2_NODES = { { 1, }, { 2, }, { 3, }, { 5, 7, }, { 3, }, { 6, }, { 4, }, {}, };

    private static final String TEST2 = "class Foo {" + PMD.EOL + " public void test_2() {" + PMD.EOL
            + "  for (int i = 0; i < 1; i++) {" + PMD.EOL + "   i++;" + PMD.EOL + "   i = 8;" + PMD.EOL + "  }"
            + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST3_NODES = { { 1, }, { 2, }, { 3, }, { 4, 5, }, { 3, }, {}, };

    private static final String TEST3 = "public class Foo {" + PMD.EOL + " public void test_3() {" + PMD.EOL
            + "  for (int i = 0; i < 1; i++) {" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST4_NODES = { { 1, }, { 2, }, { 3, }, {}, };

    private static final String TEST4 = "public class Foo {" + PMD.EOL + " public void test_4() {" + PMD.EOL
            + "  for (; ;) {" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST5_NODES = { { 1, }, { 2, }, { 3, }, { 4, }, {}, };

    private static final String TEST6 = "public class Foo {" + PMD.EOL + " public void test_6() {" + PMD.EOL
            + "  for (int i = 0; ;) {" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final String TEST7 = "public class Foo {" + PMD.EOL + " public void test_7() {" + PMD.EOL
            + "  for (int i = 0; i < 0;) {" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST8_NODES = { { 1, }, { 2, }, { 3, }, { 4, 5, }, { 3, }, {}, };

    public static final String TEST8 = "public class Foo {" + PMD.EOL + " public void test_8() {" + PMD.EOL
            + "  for (int i = 0; ; i++) {" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    public static final String TEST9 = "public class Foo {" + PMD.EOL + " public void test_9() {" + PMD.EOL
            + "  int i = 0;" + PMD.EOL + "  for (; i < 0;) {" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    public static final String TEST10 = "public class Foo {" + PMD.EOL + " public void test_10() {" + PMD.EOL
            + "  int i = 0;" + PMD.EOL + "  for (; i < 0; i++) {" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    public static final String TEST11 = "public class Foo {" + PMD.EOL + " public void test_11() {" + PMD.EOL
            + "  int i = 0;" + PMD.EOL + "  for (; ; i++) {" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST12_NODES = { { 1, }, { 2, }, { 3, }, { 4, 5, }, { 3, }, {}, };
    public static final String TEST12 = "public class Foo {" + PMD.EOL + " public void test_12() {" + PMD.EOL
            + "  for (; ;) {" + PMD.EOL + "   int i = 0;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST13_NODES = { { 1, }, { 2, }, { 3, }, { 5, 9, }, { 3, }, { 6, }, { 7, 8, }, { 8, }, { 4, },
        {}, };
    public static final String TEST13 = "public class Foo {" + PMD.EOL + " public void test_13() {" + PMD.EOL
            + "  for (int i = 0; i < 0; i++) {" + PMD.EOL + "   i = 9;" + PMD.EOL + "   if (i < 8) {" + PMD.EOL
            + "    i = 7;" + PMD.EOL + "   }" + PMD.EOL + "   i = 6;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL
            + " }";

    private static final int[][] TEST14_NODES = { { 1 }, { 2 }, { 3 }, { 5, 8 }, { 3 }, { 6 }, { 7, 4 }, { 4 }, {} };
    public static final String TEST14 = "public class Foo {" + PMD.EOL + " public void test_14() {" + PMD.EOL
            + "  for (int i = 0; i < 0; i++) {" + PMD.EOL + "   i = 9;" + PMD.EOL + "   if (i < 8) {" + PMD.EOL
            + "    i = 7;" + PMD.EOL + "   }" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST15_NODES = { { 1 }, { 2 }, { 3 }, { 5, 7 }, { 3 }, { 6, 4 }, { 4 }, {} };
    public static final String TEST15 = "public class Foo {" + PMD.EOL + " public void test_15() {" + PMD.EOL
            + "  for (int i = 0; i < 0; i++) {" + PMD.EOL + "   if (i < 8) {" + PMD.EOL + "    i = 7;" + PMD.EOL
            + "   }" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST16_NODES = { { 1 }, { 2 }, { 3 }, { 5, 8 }, { 3 }, { 6, 7 }, { 4 }, { 4 }, {} };
    public static final String TEST16 = "public class Foo {" + PMD.EOL + " public void test_16() {" + PMD.EOL
            + "  for (int i = 0; i < 0; i++) {" + PMD.EOL + "   if (i < 8) {" + PMD.EOL + "    i = 7;" + PMD.EOL
            + "   } else {" + PMD.EOL + "    i = 6;" + PMD.EOL + "   }" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL
            + " }";

    private static final int[][] TEST17_NODES = { { 1 }, { 2 }, { 3 }, { 5, 10 }, { 3 }, { 6, 7 }, { 4 }, { 8, 9 },
        { 4 }, { 4 }, {}, };
    public static final String TEST17 = "public class Foo {" + PMD.EOL + " public void test_17() {" + PMD.EOL
            + "  for (int i = 0; i < 0; i++) {" + PMD.EOL + "   if (i < 6) {" + PMD.EOL + "    i = 7;" + PMD.EOL
            + "   } else if (i > 8) {" + PMD.EOL + "    i = 9;" + PMD.EOL + "   } else {" + PMD.EOL + "    i = 10;"
            + PMD.EOL + "   }" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST18_NODES = { { 1 }, { 2 }, { 3 }, { 5, 9 }, { 3 }, { 6 }, { 8, 4 }, { 6 }, { 7 },
        {}, };
    public static final String TEST18 = "public class Foo {" + PMD.EOL + " public void test_18() {" + PMD.EOL
            + "  for (int i = 0; i < 0; i++) {" + PMD.EOL + "   for (int j = 0; j < 0; j++) {" + PMD.EOL + "    j++;"
            + PMD.EOL + "   }" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST19_NODES = { { 1 }, { 2 }, { 3 }, { 4, 5 }, { 10 }, { 6, 7 }, { 10 }, { 8, 9 },
        { 10 }, { 10 }, {}, };
    public static final String TEST19 = "public class Foo {" + PMD.EOL + " public void test_19() {" + PMD.EOL
            + "  int i = 0;" + PMD.EOL + "  if (i == 1) {" + PMD.EOL + "   i = 2;" + PMD.EOL + "  } else if (i == 3) {"
            + PMD.EOL + "   i = 4;" + PMD.EOL + "  } else if (i == 5) {" + PMD.EOL + "   i = 6;" + PMD.EOL
            + "  } else {" + PMD.EOL + "   i = 7;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST20_NODES = { { 1 }, { 2 }, { 3 }, { 4, 6 }, { 5, 7 }, { 7 }, { 7 }, {} };
    public static final String TEST20 = "public class Foo {" + PMD.EOL + " public void test_20() {" + PMD.EOL
            + "  int i = 0;" + PMD.EOL + "  if (i == 1) {" + PMD.EOL + "   if (i == 2) {" + PMD.EOL + "    i = 3;"
            + PMD.EOL + "   }" + PMD.EOL + "  } else {" + PMD.EOL + "   i = 7;" + PMD.EOL + "  }" + PMD.EOL + " }"
            + PMD.EOL + " }";

    private static final int[][] TEST21_NODES = { { 1 }, { 2 }, { 3 }, { 4, 9 }, { 5 }, { 7, 8 }, { 5 }, { 6 }, { 11 },
        { 10, 11 }, { 11 }, {}, };
    public static final String TEST21 = "public class Foo {" + PMD.EOL + " public void test_21() {" + PMD.EOL
            + "  int i = 0;" + PMD.EOL + "  if (i == 1) {" + PMD.EOL + "   for (i = 3; i < 4; i++) {" + PMD.EOL
            + "    i = 5;" + PMD.EOL + "   }" + PMD.EOL + "   i++;" + PMD.EOL + "  } else if (i < 6) {" + PMD.EOL
            + "   i = 7;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST22_NODES = { { 1 }, { 2 }, { 3 }, { 4, 8 }, { 5 }, { 7, 9 }, { 5 }, { 6 }, { 9 },
        {}, };
    public static final String TEST22 = "public class Foo {" + PMD.EOL + " public void test_22() {" + PMD.EOL
            + "  int i = 0;" + PMD.EOL + "  if (i == 1) {" + PMD.EOL + "   for (i = 3; i < 4; i++) {" + PMD.EOL
            + "    i = 5;" + PMD.EOL + "   }" + PMD.EOL + "  } else {" + PMD.EOL + "   i = 7;" + PMD.EOL + "  }"
            + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST23_NODES = { { 1 }, { 2 }, { 3 }, { 4, 8 }, { 5 }, { 7, 10 }, { 5 }, { 6 },
        { 9, 10 }, { 10 }, {}, };
    public static final String TEST23 = "public class Foo {" + PMD.EOL + " public void test_23() {" + PMD.EOL
            + "  int i = 0;" + PMD.EOL + "  if (i == 1) {" + PMD.EOL + "   for (i = 3; i < 4; i++) {" + PMD.EOL
            + "    i = 5;" + PMD.EOL + "   }" + PMD.EOL + "  } else if (i < 6) {" + PMD.EOL + "   i = 7;" + PMD.EOL
            + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST24_NODES = { { 1 }, { 2 }, { 3 }, { 4, 9 }, { 5 }, { 7, 11 }, { 5 }, { 8, 6 },
        { 6 }, { 10, 11 }, { 11 }, {}, };
    public static final String TEST24 = "public class Foo {" + PMD.EOL + " public void test_24() {" + PMD.EOL
            + "  int x = 0;" + PMD.EOL + "  if (x > 2) {" + PMD.EOL + "   for (int i = 0; i < 1; i++) {" + PMD.EOL
            + "    if (x > 3) {" + PMD.EOL + "     x++;" + PMD.EOL + "    }" + PMD.EOL + "   }" + PMD.EOL
            + "  } else if (x > 4) {" + PMD.EOL + "   x++;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST25_NODES = { { 1 }, { 2 }, { 3 }, { 4, 5 }, { 5 }, {} };
    public static final String TEST25 = "public class Foo {" + PMD.EOL + " public void test_25() {" + PMD.EOL
            + "  int x = 0;" + PMD.EOL + "  switch (x) {" + PMD.EOL + "   default:" + PMD.EOL + "    x = 9;" + PMD.EOL
            + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST26_NODES = { { 1 }, { 2 }, { 3 }, { 4 }, { 5, 6 }, { 6 }, { 7 }, { 8, 3 }, { 9 },
        {}, };
    public static final String TEST26 = "public class Foo {" + PMD.EOL + " public void test_26() {" + PMD.EOL
            + "  int x = 0;" + PMD.EOL + "  do {" + PMD.EOL + "   if (x > 0) {" + PMD.EOL + "    x++;" + PMD.EOL
            + "   }" + PMD.EOL + "   x++;" + PMD.EOL + "  } while (x < 9);" + PMD.EOL + "  x++;" + PMD.EOL + " }"
            + PMD.EOL + " }";

    private static final int[][] TEST27_NODES = { { 1 }, { 2 }, { 3 }, { 5, 9 }, { 3 }, { 6 }, { 7 }, { 8 }, { 6, 4 }, {}, };
    public static final String TEST27 = "public class Foo {" + PMD.EOL + " public void test_27() {" + PMD.EOL
            + "  for (int i = 0; i < 36; i++) {" + PMD.EOL + "   int x = 0;" + PMD.EOL + "   do {" + PMD.EOL
            + "    x++;" + PMD.EOL + "   } while (x < 9);" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST28_NODES = { { 1 }, { 2 }, { 3 }, { 5, 14 }, { 3 }, { 6 }, { 7 }, { 8, 12 }, { 9 },
        { 10, 12 }, { 11 }, { 12 }, { 13 }, { 6, 4 }, {}, };
    public static final String TEST28 = "public class Foo {" + PMD.EOL + " private void test_28() {" + PMD.EOL
            + "  for (int i = 0; i < 36; i++) {" + PMD.EOL + "   int x = 0;" + PMD.EOL + "   do {" + PMD.EOL
            + "    if (x > 0) {" + PMD.EOL + "     x++;" + PMD.EOL + "     switch (i) {" + PMD.EOL + "      case 0:"
            + PMD.EOL + "       x = 0;" + PMD.EOL + "       break;" + PMD.EOL + "     }" + PMD.EOL + "    }" + PMD.EOL
            + "    x++;" + PMD.EOL + "   } while (x < 9);" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + " }";

    private static final int[][] TEST29_NODES = { { 1 }, { 2 }, { 3, 4, 5 }, { 6 }, { 6 }, { 6 }, {}, };
    public static final String TEST29 = "public class Foo {" + PMD.EOL + " private void test_29() {" + PMD.EOL
            + "  switch(x) {" + PMD.EOL + "   case 1:" + PMD.EOL + "    break; " + PMD.EOL + "   default: " + PMD.EOL
            + "    break;" + PMD.EOL + "   case 2:" + PMD.EOL + "    break;" + PMD.EOL + "  }" + PMD.EOL + " }"
            + PMD.EOL + "}";

    private static final int[][] TEST30_NODES = { { 1 }, { 2 }, { 3 }, { 4, 7 }, { 5, 6 }, { 4 }, { 3 }, {} };
    public static final String TEST30 = "public class Foo {" + PMD.EOL + " private void test_30() {" + PMD.EOL
            + "  int x = 0;" + PMD.EOL + "  while (true) {" + PMD.EOL + "   while (x>0) {" + PMD.EOL + "     x++;"
            + PMD.EOL + "   }" + PMD.EOL + "   continue;" + PMD.EOL + "  }" + PMD.EOL + " }" + PMD.EOL + "}";
}
