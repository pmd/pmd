package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class ConfusingTernaryRuleTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-design";

    @Before
    public void setUp() {
        addRule(RULESET, "ConfusingTernary");
    }

    /*
public class BadTernaries {
  public static void main(String[] args) {
    int i = 0;
    int j = 1;
    int k = 2;
    boolean x = true;
    boolean y = false;
    boolean z = true;

    // flag all of these, lines 11 - 42:
    if (i != 11) {a();} else {b();}
    if (i != 12 && j != 0) {a();} else {b();}
    if (i != 13 || j != 0) {a();} else {b();}
    if (i != 14 && j != 0 && k != 0) {a();} else {b();}
    if (i != 15 || j != 0 || k != 0) {a();} else {b();}
    if (i != 16) {a();} else if (i != j) {b();} else{c();}
    if (i != 17) {a();} else if (i == j) {b();} else{c();}
    if (i == 18) {a();} else if (i != j) {b();} else{c();}
    x = (!y ? x : y);
    x = (!(x && y) ? y : z);
    x = (!(x || y) ? y : z);
    x = ((!x && !y) ? y : z);
    x = ((!x || !y) ? y : z);
    if (i != 24 && !x) {a();} else {b();}
    if (i != 25 || !x) {a();} else {b();}
    if (i != 26 && j != 0 && !y) {a();} else {b();}
    if (i != 27 || j != 0 || !y) {a();} else {b();}
    if (i != 28) {a();} else if (!x) {b();} else{c();}
    if (i != 29) {a();} else if (x) {b();} else{c();}
    if (i == 30) {a();} else if (!x) {b();} else{c();}
    x = !(c() == y) ? y : !z;
    if (!c()) {a();} else {b();}
    if (c() != x) {a();} else {b();}
    if (!c() != x) {a();} else {b();}
    if (!c() != !x) {a();} else {b();}
    if ((i != 36) || !(j == 0)) {a();} else {b();}
    if ((i != 37) || !(x ? y : z)) {a();} else {b();}
    if ((i != 38)) {a();} else {b();}
    if (i != 39 || (j != 0 || k != 0)) {a();} else {b();}
    if (i != 40 && (j != 0 && k != 0)) {a();} else {b();}
    if (!x && (j != 41 && k != 0)) {a();} else {b();}
    if (((x != y)) || !(x)) { a(); } else { b(); }

    // don't flag these:
    if (i != 0) {a();}
    if (!x) {a();}
    if (i == 0) {a();} else {b();}
    if (i == 0 && j != 0) {a();} else {b();}
    if (i == 0 || j != 0) {a();} else {b();}
    if (i == 0 && !x) {a();} else {b();}
    if (x) {a();} else {b();}
    if (x ? y : !z) {a();} else {b();}
    if (c() == !x) {a();} else {b();}
    if (c() ? !x : !c()) {a();} else {b();}
    if (!x && d() instanceof String) {a();} else {b();}
    if (!x && (d() instanceof String)) {a();} else {b();}
  }

  private static void a() { }
  private static void b() { }
  private static boolean c() { return true; }
  private static Object d() { return null; }
}

    */

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ConfusingTernaryRuleTest.class);
    }
}
