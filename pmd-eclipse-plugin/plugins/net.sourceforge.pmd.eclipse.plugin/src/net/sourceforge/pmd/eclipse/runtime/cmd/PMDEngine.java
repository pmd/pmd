package net.sourceforge.pmd.eclipse.runtime.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Configuration;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

/**
 * Temporary class to handle PMD 4.2.x compatibility issues.
 *
 * This should be removed once the Configuration class is working in PMD 5.0.
 *
 */
@Deprecated
public class PMDEngine {

    private Configuration configuration = new Configuration();

    public void setLanguageVersion(LanguageVersion languageVersion) {
        configuration.setDefaultLanguageVersion(languageVersion);
    }

    public void setClassLoader(ClassLoader classLoader) {
        configuration.setClassLoader(classLoader);
    }

    public ClassLoader getClassLoader() {
        return configuration.getClassLoader();
    }

    public boolean applies(File file, RuleSet ruleSet) {
	return ruleSet.applies(file);
    }

//    public void processFile(Reader input, RuleSet ruleSet, RuleContext context) throws PMDException {
//        RuleSets set = new RuleSets();
//        set.addRuleSet(ruleSet);
//
//        PMD pmd = new PMD(configuration);
//        pmd.processFile(input, set, context);
//    }

    public void processFile(File file, RuleSet ruleSet, RuleContext context) throws PMDException {

        process(new FileDataSource(file), ruleSet, context);
    }
    
    public void process(DataSource dataSource, RuleSet ruleSet, RuleContext context) throws PMDException {

        RuleSets set = new RuleSets(ruleSet);

        List<DataSource> files = new ArrayList<DataSource>(1);
        files.add(dataSource);

        PMD.processFiles(configuration, new RuleSetFactory(), files, context, new ArrayList<Renderer>());
    }
}
