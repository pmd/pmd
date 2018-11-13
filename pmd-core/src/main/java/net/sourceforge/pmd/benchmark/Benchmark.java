/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.benchmark;

/**
 * Represents an execution phase for benchmarking purposes.
 *
 * @author Brian Remedios
 */
@Deprecated
public enum Benchmark {
    // The constants must be sorted in execution order,
    // the index is derived from the ordinal of the constant
    Rule(null),
    RuleChainRule(null),
    CollectFiles("Collect files"),
    LoadRules("Load rules"),
    Parser("Parser"),
    QualifiedNameResolution("Qualified name resolution"),
    SymbolTable("Symbol table"),
    DFA("DFA"),
    TypeResolution("Type resolution"),
    RuleChainVisit("RuleChain visit"),
    Multifile("Multifile analysis"),
    Reporting("Reporting"),
    RuleTotal("Rule total"),
    RuleChainTotal("Rule chain rule total"),
    MeasuredTotal("Measured total"),
    NonMeasuredTotal("Non-measured total"),
    TotalPMD("Total PMD");

    public final int index = ordinal();
    public final String name;


    Benchmark(String theName) {
        name = theName;
    }
}
