/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

/**
 * @author raik
 */
// TODO This should be an enum?
public class NodeType {

    public static final int IF_EXPR = 1;
    public static final int IF_LAST_STATEMENT = 2;
    public static final int IF_LAST_STATEMENT_WITHOUT_ELSE = 3;
    public static final int ELSE_LAST_STATEMENT = 4;

    public static final int WHILE_EXPR = 10;
    public static final int WHILE_LAST_STATEMENT = 11;

    public static final int SWITCH_START = 20;
    public static final int CASE_LAST_STATEMENT = 21;
    public static final int SWITCH_LAST_DEFAULT_STATEMENT = 22;
    public static final int SWITCH_END = 23;

    public static final int FOR_INIT = 30;
    public static final int FOR_EXPR = 31;
    public static final int FOR_UPDATE = 32;
    public static final int FOR_BEFORE_FIRST_STATEMENT = 33;
    public static final int FOR_END = 34;

    public static final int DO_BEFORE_FIRST_STATEMENT = 40;
    public static final int DO_EXPR = 41;

    public static final int RETURN_STATEMENT = 50;
    public static final int BREAK_STATEMENT = 51;
    public static final int CONTINUE_STATEMENT = 52;

    public static final int LABEL_STATEMENT = 60;
    public static final int LABEL_LAST_STATEMENT = 61;

    // TODO - throw statements?
    public static final int THROW_STATEMENT = 70;
}
