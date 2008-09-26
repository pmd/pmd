package net.sourceforge.pmd.eclipse.runtime.writer.impl;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.WriterException;

/**
 * Generate an XML rule set file from a rule set
 * This class is a rewritting of the original from PMD engine
 * that doesn't support xpath properties !
 *
 * @author Philippe Herlin
 * @version $Revision$
 *
 * $Log$
 * Revision 1.5  2007/06/24 16:41:31  phherlin
 * Integrate PMD v4.0rc1
 *
 * Revision 1.4  2007/06/24 15:10:18  phherlin
 * Integrate PMD v4.0rc1
 * Prepare release 3.2.2
 *
 * Revision 1.3  2007/02/15 22:27:15  phherlin
 * Fix 1641930 Creation of ruleset.xml file causes error in Eclipse
 *
 * Revision 1.2  2006/06/20 21:01:49  phherlin
 * Enable PMD and fix error level violations
 *
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.7  2005/06/11 22:09:57  phherlin
 * Update to PMD 3.2: the symbol table attribute is no more used
 *
 * Revision 1.6  2005/05/07 13:32:06  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.5  2005/01/31 23:39:37  phherlin
 * Upgrading to PMD 2.2
 *
 * Revision 1.4  2005/01/16 22:53:03  phherlin
 * Upgrade to PMD 2.1: take into account new rules attributes symboltable and dfa
 *
 * Revision 1.3  2003/12/18 23:58:37  phherlin
 * Fixing malformed UTF-8 characters in generated xml files
 *
 * Revision 1.2  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.1  2003/11/07 14:31:48  phherlin
 * Reverse to identation usage, remove dummy text nodes
 *
 * Revision 1.1  2003/10/16 22:26:37  phherlin
 * Fix bug #810858.
 * Complete refactoring of rule set generation. Using a DOM tree and the Xerces 2 serializer.
 *
 *
 * -- Renaming to RuleSetWriterImpl --
 *
 * Revision 1.2  2003/10/14 21:26:32  phherlin
 * Upgrading to PMD 1.2.2
 *
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
class RuleSetWriterImpl implements IRuleSetWriter {

    /**
     * Write a ruleset as an XML stream
     * @param writer the output writer
     * @param ruleSet the ruleset to serialize
     */
    public void write(OutputStream outputStream, RuleSet ruleSet) throws WriterException {
        try {
        	RuleSetWriter ruleSetWriter = new RuleSetWriter(outputStream);
        	ruleSetWriter.write(ruleSet);
        	outputStream.flush();
        } catch (RuntimeException e) {
            throw new WriterException(e);
        } catch (IOException e) {
            throw new WriterException(e);
        }
    }
}
