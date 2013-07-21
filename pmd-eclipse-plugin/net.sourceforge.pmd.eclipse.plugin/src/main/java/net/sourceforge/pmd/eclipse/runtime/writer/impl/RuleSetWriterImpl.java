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
