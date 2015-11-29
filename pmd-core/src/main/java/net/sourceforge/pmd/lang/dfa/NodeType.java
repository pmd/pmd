/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

import java.util.HashMap;
import java.util.Map;

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

    // Poor Man's Enum until we convert the class to real enum
    private static final Map<Integer, String> TYPE_MAP = new HashMap<>();
    static {
        TYPE_MAP.put(NodeType.IF_EXPR, "IF_EXPR");
        TYPE_MAP.put(NodeType.IF_LAST_STATEMENT, "IF_LAST_STATEMENT");
        TYPE_MAP.put(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE, "IF_LAST_STATEMENT_WITHOUT_ELSE");
        TYPE_MAP.put(NodeType.ELSE_LAST_STATEMENT, "ELSE_LAST_STATEMENT");
        TYPE_MAP.put(NodeType.WHILE_LAST_STATEMENT, "WHILE_LAST_STATEMENT");
        TYPE_MAP.put(NodeType.WHILE_EXPR, "WHILE_EXPR");
        TYPE_MAP.put(NodeType.SWITCH_START, "SWITCH_START");
        TYPE_MAP.put(NodeType.CASE_LAST_STATEMENT, "CASE_LAST_STATEMENT");
        TYPE_MAP.put(NodeType.SWITCH_LAST_DEFAULT_STATEMENT, "SWITCH_LAST_DEFAULT_STATEMENT");
        TYPE_MAP.put(NodeType.SWITCH_END, "SWITCH_END");
        TYPE_MAP.put(NodeType.FOR_INIT, "FOR_INIT");
        TYPE_MAP.put(NodeType.FOR_EXPR, "FOR_EXPR");
        TYPE_MAP.put(NodeType.FOR_UPDATE, "FOR_UPDATE");
        TYPE_MAP.put(NodeType.FOR_BEFORE_FIRST_STATEMENT, "FOR_BEFORE_FIRST_STATEMENT");
        TYPE_MAP.put(NodeType.FOR_END, "FOR_END");
        TYPE_MAP.put(NodeType.DO_BEFORE_FIRST_STATEMENT, "DO_BEFORE_FIRST_STATEMENT");
        TYPE_MAP.put(NodeType.DO_EXPR, "DO_EXPR");
        TYPE_MAP.put(NodeType.RETURN_STATEMENT, "RETURN_STATEMENT");
        TYPE_MAP.put(NodeType.BREAK_STATEMENT, "BREAK_STATEMENT");
        TYPE_MAP.put(NodeType.CONTINUE_STATEMENT, "CONTINUE_STATEMENT");
        TYPE_MAP.put(NodeType.LABEL_STATEMENT, "LABEL_STATEMENT");
        TYPE_MAP.put(NodeType.LABEL_LAST_STATEMENT, "LABEL_END");
        TYPE_MAP.put(NodeType.THROW_STATEMENT, "THROW_STATEMENT");
    }

    public static Map<Integer, String> getTypeMap() {
        return TYPE_MAP;
    }

    public static String stringFromType(int intype) {
        if (-1 == intype) {
            return "<ROOT>";
        }
        if (!TYPE_MAP.containsKey(intype)) {
            throw new RuntimeException("Couldn't find NodeType type id " + intype);
        }
        return TYPE_MAP.get(intype);
    }
}
