/*
 * User: tom
 * Date: Jun 21, 2002
 * Time: 2:11:15 PM
 */
package net.sourceforge.pmd;

import java.util.Stack;


/**
 *
 * Here's the problem:
 *
 * public class Outer
 * {
 *  private String foo;
 *  public void foo() {
 *      bar(new Runnable() {public void run() {String foo;}});
 *  }
 *  private void bar(Runnable r) {}
 * }
 *
 * In this example, Outer.foo and the Runnable.foo are two different variables - even though
 * Runnable.foo looks like its inside Outer, they don't conflict.
 *
 * So, a couple of SymbolTables are grouped into a Namespace.  SymbolTables are grouped so that inner classes have their own
 * "group" of symbol tables.  So a class with an inner class would look like this:
 *
 *        ST
 *        ST
 *        NS2
 * ST     ST    ST
 * ST     ST    ST
 * NS1    NS1   NS1
 *
 * That way the scopes work out nicely and inner classes can be arbitrarily nested.
 */
public class Namespace {

    private Stack tables = new Stack();

    public void addTable() {
        if (tables.empty()) {
            tables.push(new SymbolTable());
        } else {
            tables.push(new SymbolTable((SymbolTable)tables.peek()));
        }
    }

    public void removeTable() {
        tables.pop();
    }

    public SymbolTable peek() {
        return (SymbolTable)tables.peek();
    }

    public int size() {
        return tables.size();
    }

}
