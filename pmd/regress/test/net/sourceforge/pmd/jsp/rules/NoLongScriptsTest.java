package test.net.sourceforge.pmd.jsp.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.SourceType;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class NoLongScriptsTest extends SimpleAggregatorTst {

	public void testAll() throws RuleSetNotFoundException {
		Rule rule = new RuleSetFactory()
				.createSingleRuleSet("rulesets/basic-jsp.xml").getRuleByName(
						"NoLongScripts");
		runTests(new TestDescriptor[] { 
				new TestDescriptor(VIOLATION1, "Too long javascript.", 1, rule),
				new TestDescriptor(NO_VIOLATION1, "Short javascript.", 0, rule),
			}, SourceType.JSP);
	}

	private static final String VIOLATION1 = "<HTML>\n"
			+ "<BODY>\n"
			+ "<!--Java Script-->\n"
			+ "<SCRIPT language='JavaScript' type='text/javascript'>\n"
			+ "<!--\n"
			+ "function calcDays(){\n"
			+ "  var date1 = document.getElementById('d1').lastChild.data;\n"
			+ "  var date2 = document.getElementById('d2').lastChild.data;\n"
			+ "  date1 = date1.split(\"-\");\n"
			+ "  date2 = date2.split(\"-\");\n"
			+ "  var sDate = new Date(date1[0]+\"/\"+date1[1]+\"/\"+date1[2]);\n"
			+ "  var eDate = new Date(date2[0]+\"/\"+date2[1]+\"/\"+date2[2]);\n"
			+ "  var daysApart = Math.abs(Math.round((sDate-eDate)/86400000));\n"
			+ "  document.getElementById('diffDays').lastChild.data = daysApart;\n"
			+ "}\n" + "\n" + "onload=calcDays;\n" + "//-->\n" + "</SCRIPT>\n"
			+ "</BODY>\n" + "</HTML>;\n";
	
	private static final String NO_VIOLATION1 = "<HTML>\n"
		+ "<BODY>\n"
		+ "<!--Java Script-->\n"
		+ "<SCRIPT language='JavaScript' type='text/javascript'>\n"
		+ "<!--\n"
		+ "function calcDays(){\n"
		+ "  document.getElementById('diffDays').lastChild.data = daysApart;\n"
		+ "}\n" + "\n" + "onload=calcDays;\n" + "//-->\n" + "</SCRIPT>\n"
		+ "</BODY>\n" + "</HTML>;\n";
}
