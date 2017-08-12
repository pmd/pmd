/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

/**
 *
 * @author Brian Remedios
 */
public enum Benchmark {
    Rule(0, null),
    RuleChainRule(1, null),
    CollectFiles(2, "Collect files"),
    LoadRules(3, "Load rules"),
    Parser(4, "Parser"),
    SymbolTable(5, "Symbol table"),
    DFA(6, "DFA"),
    TypeResolution(7, "Type resolution"),
    Multifile(8, "Multifile analysis"),
    MetricsVisitor(9, "Metrics"),
    RuleChainVisit(10, "RuleChain visit"),
    Reporting(11, "Reporting"),
    RuleTotal(12, "Rule total"),
    RuleChainTotal(13, "Rule chain rule total"),
    MeasuredTotal(14, "Measured total"),
    NonMeasuredTotal(15, "Non-measured total"),
    TotalPMD(16, "Total PMD");

    public final int index;
    public final String name;

    Benchmark(int idx, String theName) {
        index = idx;
        name = theName;
    }
}
