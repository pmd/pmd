/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.benchmark;

/**
 * 
 * @author Brian Remedios
 */
public enum Benchmark {    	
	Rule		    (0, null),
	RuleChainRule   (1, null),
	CollectFiles    (2, "Collect files"),
	LoadRules 	    (3, "Load rules"),
	Parser 	 	    (4, "Parser"),
	SymbolTable     (5, "Symbol table"),
	DFA			    (6, "DFA"),
	TypeResolution  (7, "Type resolution"),
	RuleChainVisit  (8, "RuleChain visit"),
	Reporting	    (9, "Reporting"),
	RuleTotal	    (10, "Rule total"),
	RuleChainTotal  (11, "Rule chain rule total"),
	MeasuredTotal   (12, "Measured total"),
	NonMeasuredTotal(13, "Non-measured total"),
	TotalPMD		(14, "Total PMD");
	
	public final int index;
	public final String name;
	
	private Benchmark(int idx, String theName) {
		index = idx;
		name = theName;
	}
}