package net.sourceforge.pmd.jedit;

import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.AbstractIncrementingRenderer;

import org.gjt.sp.jedit.jEdit;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

/**
 * Renderer to ErrorList.
 */
public class ErrorListRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "errorlist";

    DefaultErrorSource errorSource = null;
    
    /**
     * @param errorSource The error source to send errors to.    
     */
    public ErrorListRenderer( DefaultErrorSource errorSource ) {
        super( NAME, "ErrorList" );
        this.errorSource = errorSource;
        
        // nothing will ever be written to this writer
        setWriter(new NullWriter());  
    }
    
    /**
     * @return Always returns an empty string.    
     */
    public String defaultFileExtension() {
        return "";
    }

    /**
     * Does nothing.
     */
    @Override
    public void start() throws IOException {
    }

    /**
     * Sends the rule violations to the ErrorList plugin.
     */
    @Override
    public void renderFileViolations( Iterator<RuleViolation> violations ) throws IOException {
        if (!violations.hasNext()) {
            errorSource.clear();
            return;
        }
        
        String rulename = "";
        final boolean showRuleName = jEdit.getBooleanProperty( PMDJEditPlugin.PRINT_RULE );

        while ( violations.hasNext() ) {
            RuleViolation violation = violations.next();
            if ( showRuleName ) {
                rulename = violation.getRule().getName() + "->";
            }

            // error list doesn't handle multi-line errors, so in the case of a
            // multi-line violation, use 0 for the start and end columns. ErrorList
            // will mark the entire line as an error.
            int startLine = violation.getBeginLine() - 1;
            int startColumn = violation.getBeginLine() == violation.getEndLine() ? violation.getBeginColumn() - 1: 0;
            int endColumn = violation.getBeginLine() == violation.getEndLine() ? violation.getEndColumn() : 0;
            errorSource.addError( new DefaultErrorSource.DefaultError( errorSource, ErrorSource.WARNING, violation.getFilename(), startLine, startColumn, endColumn, rulename + violation.getDescription() ) );            // NOPMD
        }
    }
}
