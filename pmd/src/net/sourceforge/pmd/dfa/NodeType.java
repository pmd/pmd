package net.sourceforge.pmd.dfa;

/**
 * @author raik
 */
public interface NodeType {

    //IF STATEMENT
    int IF_EXPR = 1;
    int IF_LAST_STATEMENT = 2;
    int IF_LAST_STATEMENT_WITHOUT_ELSE = 3;
    int ELSE_LAST_STATEMENT = 4;

    //WHILE STATEMENT
    int WHILE_EXPR = 10;
    int WHILE_LAST_STATEMENT = 11;

    //SWITCH STATEMENT
    int SWITCH_START = 20;
    int CASE_LAST_STATEMENT = 21;
    int SWITCH_LAST_DEFAULT_STATEMENT = 22;
    int SWITCH_END = 23;

    //FOR STATEMENT
    int FOR_INIT = 30;
    int FOR_EXPR = 31;
    int FOR_UPDATE = 32;
    int FOR_BEFORE_FIRST_STATEMENT = 33;
    int FOR_END = 34;

    //DO WHILE
    int DO_BEFORE_FIRST_STATEMENT = 40;
    int DO_EXPR = 41;

    int RETURN_STATEMENT = 50;
    int BREAK_STATEMENT = 51;
    int CONTINUE_STATEMENT = 52;
}
