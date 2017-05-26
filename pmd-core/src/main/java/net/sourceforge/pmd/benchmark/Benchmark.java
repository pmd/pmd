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
    MetricsVisitor(8, "Metrics visitor"),
    RuleChainVisit(9, "RuleChain visit"),
    Reporting(10, "Reporting"),
    RuleTotal(11, "Rule total"),
    RuleChainTotal(12, "Rule chain rule total"),
    MeasuredTotal(13, "Measured total"),
    NonMeasuredTotal(14, "Non-measured total"),
    TotalPMD(15, "Total PMD");

    public final int index;
    public final String name;

    Benchmark(int idx, String theName) {
        index = idx;
        name = theName;
    }
}
