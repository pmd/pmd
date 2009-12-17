package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import common.gui.pathbuilder.PathBuilder;
import java.io.File;
import javax.swing.filechooser.FileFilter;

public class PMDCustomRulesOptionPane extends AbstractOptionPane {

    private PathBuilder pathBuilder = null;

    public PMDCustomRulesOptionPane() {
        super( PMDJEditPlugin.NAME );
    }

    public void _init() {
        pathBuilder = new PathBuilder();
        pathBuilder.setAddButtonText( "Add Ruleset" );
        pathBuilder.setRemoveButtonText( "Remove Ruleset" );
        pathBuilder.setFileDialogTitle( "Select Ruleset File" );
        pathBuilder.setFileFilter( new RulesetFileFilter() );
        add( pathBuilder );
        String paths = jEdit.getProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, "" );
        paths = paths.replaceAll( ",", File.pathSeparator );
        pathBuilder.setPath( paths );
    }

    public void _save() {
        String paths = pathBuilder.getPath();
        paths = paths.replaceAll( File.pathSeparator, "," );
        jEdit.setProperty( PMDJEditPlugin.CUSTOM_RULES_PATH_KEY, paths );
    }

    public class RulesetFileFilter extends FileFilter {
        public boolean accept( File f ) {
            return f != null && ( f.isDirectory() || f.getName().endsWith( ".xml" ) );
        }
        public String getDescription() {
            return "PMD Ruleset Files";
        }
    }
}