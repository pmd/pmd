/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.util.StringUtil;

/**
 * To use this, do this:
 *
 * $ cat ~/tmp/Test.java
 * package foo;
 * public class Test {
 *  private int x;
 * }
 * $ java net.sourceforge.pmd.util.XPathTest -xpath "//FieldDeclaration" -filename "/home/tom/tmp/Test.java"
 * Match at line 3 column 11; package name 'foo'; variable name 'x'
 */
public class XPathCLI {

    private static final Language LANGUAGE = LanguageRegistry.getLanguage("Java");

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("Wrong arguments.\n");
            System.err.println("Example:");
            System.err.println("java " + XPathCLI.class.getName() + " -xpath \"//FieldDeclaration\" -filename \"/home/user/Test.java\"");
            System.exit(1);
        }

        String xpath = args[0].equals("-xpath") ? args[1] : args[3];
        String filename = args[0].equals("-file") ? args[1] : args[3];
        
        Rule rule = new XPathRule(xpath);
        rule.setMessage("Got one!");
        rule.setLanguage(LANGUAGE);
        RuleSet ruleSet = RuleSet.createFor("", rule);

        RuleContext ctx = PMD.newRuleContext(filename, new File(filename));
        ctx.setLanguageVersion(LANGUAGE.getDefaultVersion());

        PMDConfiguration config = new PMDConfiguration();
        config.setDefaultLanguageVersion(LANGUAGE.getDefaultVersion());
        
        new SourceCodeProcessor(config).processSourceCode(new FileReader(filename), new RuleSets(ruleSet), ctx);

        for (Iterator<RuleViolation> i = ctx.getReport().iterator(); i.hasNext();) {
            RuleViolation rv = i.next();
            StringBuilder sb = new StringBuilder(60)
                .append("Match at line ").append(rv.getBeginLine())
                .append(" column ").append(rv.getBeginColumn());
            if (StringUtil.isNotEmpty(rv.getPackageName())) {
                sb.append("; package name '" + rv.getPackageName() + "'");
            }
            if (StringUtil.isNotEmpty(rv.getMethodName())) {
                sb.append("; method name '" + rv.getMethodName() + "'");
            }
            if (StringUtil.isNotEmpty(rv.getVariableName())) {
                sb.append("; variable name '" + rv.getVariableName() + "'");
            }
            System.out.println(sb.toString());
        }
    }
}
