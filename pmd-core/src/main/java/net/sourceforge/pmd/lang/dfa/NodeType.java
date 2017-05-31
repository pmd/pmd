/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dfa;

/**
 * @author raik
 */
// TODO Remove ids
public enum NodeType {
    ROOT(-1, "<ROOT>"),

    IF_EXPR(1, "IF_EXPR"),
    IF_LAST_STATEMENT(2, "IF_LAST_STATEMENT"),
    IF_LAST_STATEMENT_WITHOUT_ELSE(3, "IF_LAST_STATEMENT_WITHOUT_ELSE"),
    ELSE_LAST_STATEMENT(4, "ELSE_LAST_STATEMENT"),

    WHILE_EXPR(10, "WHILE_EXPR"),
    WHILE_LAST_STATEMENT(11, "WHILE_LAST_STATEMENT"),

    SWITCH_START(20, "SWITCH_START"),
    CASE_LAST_STATEMENT(21, "CASE_LAST_STATEMENT"),
    SWITCH_LAST_DEFAULT_STATEMENT(22, "SWITCH_LAST_DEFAULT_STATEMENT"),
    SWITCH_END(23, "SWITCH_END"),

    FOR_INIT(30, "FOR_INIT"),
    FOR_EXPR(31, "FOR_EXPR"),
    FOR_UPDATE(32, "FOR_UPDATE"),
    FOR_BEFORE_FIRST_STATEMENT(33, "FOR_BEFORE_FIRST_STATEMENT"),
    FOR_END(34, "FOR_END"),

    DO_BEFORE_FIRST_STATEMENT(40, "DO_BEFORE_FIRST_STATEMENT"),
    DO_EXPR(41, "DO_EXPR"),

    RETURN_STATEMENT(50, "RETURN_STATEMENT"),
    BREAK_STATEMENT(51, "BREAK_STATEMENT"),
    CONTINUE_STATEMENT(52, "CONTINUE_STATEMENT"),

    LABEL_STATEMENT(60, "LABEL_STATEMENT"),
    LABEL_LAST_STATEMENT(61, "LABEL_LAST_STATEMENT"),

    // TODO - throw statements?
    THROW_STATEMENT(70, "THROW_STATEMENT");

    private int id;
    private String description;

    NodeType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
