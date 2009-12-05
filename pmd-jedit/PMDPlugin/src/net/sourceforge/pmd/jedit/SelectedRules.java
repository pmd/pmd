/*
*  User: tom
*  Date: Jul 9, 2002
*  Time: 1:18:38 PM
*/
package net.sourceforge.pmd.jedit;

import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.tree.*;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;

import net.sourceforge.pmd.jedit.checkboxtree.*;

import org.gjt.sp.jedit.jEdit;


/**
 *  Description of the Class
 *
 * @author     jiger.p
 * @created    April 22, 2003
 */
public class SelectedRules {

    // root of tree to show rule sets and rules
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    // model for the checkbox tree
    private TreeModel treeModel = null;

    // model of the checkboxes for the tree that shows the rule sets and rules
    private TreeCheckingModel checkingModel = null;

    // collection of all rulesets, includes PMD rulesets and custom rulesets
    private List<RuleSet> rulesets = null;

    // collection of rulesets built into PMD, this only needs loaded once
    private List<RuleSet> pmdRulesets = null;

    // set of the rules selected in the checking model
    private RuleSets selectedRules = null;

    /**
     * Loads PMD standard rulesets and any user defined custom rulesets, creates
     * a checkbox tree model and root tree node for the rules.
     *
     * @exception  RuleSetNotFoundException  Only thrown when loading PMD standard
     * rulesets.  Any exception found while attempting to load a custom ruleset is
     * caught and a message is displayed to the user.
     */
    public SelectedRules() throws RuleSetNotFoundException {
        loadRuleSets();
        loadTree();
    }

    private void loadRuleSets() {
        rulesets = new ArrayList<RuleSet>();

        // load the rulesets built into PMD
        if ( pmdRulesets == null ) {
            pmdRulesets = loadPMDRuleSets();
        }
        for ( RuleSet ruleset : pmdRulesets ) {
            rulesets.add( ruleset );
        }

        // load any custom rulesets
        List<RuleSet> custom_rulesets = loadCustomRuleSets();
        for ( RuleSet ruleset : custom_rulesets ) {
            rulesets.add( ruleset );
        }

        // sort the rulesets and add the individual rules
        Collections.sort( rulesets, rulesetSorter );
        for ( RuleSet rs : rulesets ) {
            addRuleSet2Rules( rs );
        }

    }

    private List<RuleSet> loadPMDRuleSets() {
        List<RuleSet> rulesets = new ArrayList<RuleSet>();

        try {
            RuleSetFactory rsf = new RuleSetFactory();
            for ( Iterator<RuleSet> i = rsf.getRegisteredRuleSets(); i.hasNext(); ) {
                RuleSet rs = i.next();
                //System.out.println("Added RuleSet " + rs.getName() + " description "+ rs.getDescription() +" language "+ rs.getLanguage());
                rulesets.add( rs );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        // PMD 4.2.5 has jsf and jsp rules that are not in the rulesets.properties file.
        // Instead they are in a separate jsprulesets.properties file which is inside
        // the pmd-4.2.5.jar file.
        /// TODO: this needs work.  The jsp rules can't just be added to the standard
        // ruleset because the standard rules work on java files and the jsp rules work
        // on jsp files.  Need to make two lists of rules and use the appropriate list
        // based on the buffer mode.
        /*
        try {
            Properties props = new Properties();
            props.load( getClass().getClassLoader().getResourceAsStream( "rulesets/jsprulesets.properties" ) );
            String filename_list = props.getProperty( "rulesets.filenames" );
            if ( filename_list != null ) {
                String[] filenames = filename_list.split( "," );
                for ( String filename : filenames ) {
                    RuleSet rs = rsf.createRuleSet( getClass().getClassLoader().getResourceAsStream( filename ) );
                    if ( rs != null ) {
                        rulesets.add( rs );
                    }
                }
            }
    }
        catch ( Exception e ) {
            e.printStackTrace();
    }
        */
        return rulesets;
    }

    private List<RuleSet> loadCustomRuleSets() {
        List<RuleSet> rulesets = new ArrayList<RuleSet>();
        // Load custom RuleSets if any, but do not die if there is any problem
        // a custom ruleset.
        try {
            String customRuleSetPath = jEdit.getProperty( "pmd.customRulesPath" );
            if ( !( customRuleSetPath == null ) ) {
                RuleSetFactory rsf = new RuleSetFactory();
                RuleSets ruleSets = rsf.createRuleSets( customRuleSetPath );
                if ( ruleSets.getAllRuleSets() != null ) {
                    for ( RuleSet rs : ruleSets.getAllRuleSets() ) {
                        rulesets.add( rs );
                    }
                }
            }
        }
        catch ( RuleSetNotFoundException e ) {
            JOptionPane.showMessageDialog( null, 
                jEdit.getProperty("net.sf.pmd.There_was_an_error_loading_one_or_more_custom_rulesets,_so_no_custom_rulesets_were_loaded", "There was an error loading one or more custom rulesets, so no custom rulesets were loaded"), 
                jEdit.getProperty("net.sf.pmd.Error_Loading_Custom_Ruleset", "Error Loading Custom Ruleset"), 
                JOptionPane.ERROR_MESSAGE );
        }
        return rulesets;
    }

    protected void loadGoodRulesTree() {

        Properties goodRules = new Properties();
        try {
            goodRules.load(getClass().getClassLoader().getResourceAsStream("default_rules.props"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        treeModel = new DefaultTreeModel( root );
        checkingModel = new DefaultTreeCheckingModel( treeModel );
        selectedRules = new RuleSets();

        // load the selected rules from a list of good rules, this
        // builds the checking model for the tree as well as the RuleSets for the
        // selected rules to pass to PMD.
        for ( int i = 0; i < root.getChildCount(); i++ ) {
            DefaultMutableTreeNode ruleSetNode = ( DefaultMutableTreeNode ) root.getChildAt( i );
            RuleSet ruleset = new RuleSet();
            boolean hadCheckedRule = false;
            for ( int j = 0; j < ruleSetNode.getChildCount(); j++ ) {
                DefaultMutableTreeNode ruleNode = ( DefaultMutableTreeNode ) ruleSetNode.getChildAt( j );
                TreePath path = new TreePath( ruleNode.getPath() );
                RuleNode rn = ( RuleNode ) ruleNode.getUserObject();
                String goodRuleChecked = goodRules.getProperty(PMDJEditPlugin.OPTION_RULES_PREFIX + rn.getRule().getName());
                boolean checked = goodRuleChecked == null ? false : "true".equals(goodRuleChecked);
                if ( checked ) {
                    checkingModel.addCheckingPath( path );
                    ruleset.addRule( rn.getRule() );
                    hadCheckedRule = true;
                }
            }
            if ( hadCheckedRule ) {
                selectedRules.addRuleSet( ruleset );
            }
        }
    }

    protected void loadTree() {
        treeModel = new DefaultTreeModel( root );
        checkingModel = new DefaultTreeCheckingModel( treeModel );
        selectedRules = new RuleSets();

        // load the previously saved and selected rules from jEdit properties, this
        // builds the checking model for the tree as well as the RuleSets for the
        // selected rules to pass to PMD.
        for ( int i = 0; i < root.getChildCount(); i++ ) {
            DefaultMutableTreeNode ruleSetNode = ( DefaultMutableTreeNode ) root.getChildAt( i );
            RuleSet ruleset = new RuleSet();
            boolean hadCheckedRule = false;
            for ( int j = 0; j < ruleSetNode.getChildCount(); j++ ) {
                DefaultMutableTreeNode ruleNode = ( DefaultMutableTreeNode ) ruleSetNode.getChildAt( j );
                TreePath path = new TreePath( ruleNode.getPath() );
                RuleNode rn = ( RuleNode ) ruleNode.getUserObject();
                boolean checked = jEdit.getBooleanProperty( PMDJEditPlugin.OPTION_RULES_PREFIX + rn.getRule().getName(), false );
                if ( checked ) {
                    checkingModel.addCheckingPath( path );
                    ruleset.addRule( rn.getRule() );
                    hadCheckedRule = true;
                }
            }
            if ( hadCheckedRule ) {
                selectedRules.addRuleSet( ruleset );
            }
        }
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    public TreeCheckingModel getCheckingModel() {
        return checkingModel;
    }

    // save the rules selected by the user in the options pane.
    public void save( TreeCheckingModel tcm ) {
        checkingModel = tcm;
        root = ( DefaultMutableTreeNode ) checkingModel.getTreeModel().getRoot();

        for ( int i = 0; i < root.getChildCount(); i++ ) {
            DefaultMutableTreeNode ruleSetNode = ( DefaultMutableTreeNode ) root.getChildAt( i );
            for ( int j = 0; j < ruleSetNode.getChildCount(); j++ ) {
                DefaultMutableTreeNode ruleNode = ( DefaultMutableTreeNode ) ruleSetNode.getChildAt( j );
                TreePath path = new TreePath( ruleNode.getPath() );
                boolean checked = checkingModel.isPathChecked( path );
                RuleNode rn = ( RuleNode ) ruleNode.getUserObject();
                jEdit.setBooleanProperty( PMDJEditPlugin.OPTION_RULES_PREFIX + rn.getRule().getName(), checked );
            }
        }
    }

    /**
     *  Gets the selectedRules attribute of the SelectedRules object
     *
     * @return    The selectedRules value
     */
    public RuleSets getSelectedRules() {
        return selectedRules;
    }


    /**
     *  Adds a feature to the RuleSet2Rules attribute of the SelectedRules object
     *
     * @param  rs  The feature to be added to the RuleSet2Rules attribute
     */
    private void addRuleSet2Rules( RuleSet rs ) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( new RuleSetNode( rs ) );

        List<Rule> rules = new ArrayList<Rule>( rs.getRules() );
        Collections.sort( rules, ruleSorter );

        for ( Rule rule : rules ) {
            //System.out.println("+++++ adding rule: " + rule.getName());
            DefaultMutableTreeNode ruleNode = new DefaultMutableTreeNode( new RuleNode( rule ) );
            node.add( ruleNode );
        }
        root.add( node );
    }


    public TreeNode getRoot() {
        return root;
    }


    private final Comparator<Rule> ruleSorter = new Comparator<Rule>() {
                public int compare( Rule r1, Rule r2 ) {
                    if ( r1 == null ) {
                        return 1;
                    }
                    if ( r2 == null ) {
                        return -1;
                    }
                    return r1.getName().compareTo( r2.getName() );
                }
            };

    private final Comparator<RuleSet> rulesetSorter = new Comparator<RuleSet>() {
                public int compare( RuleSet r1, RuleSet r2 ) {
                    if ( r1 == null ) {
                        return 1;
                    }
                    if ( r2 == null ) {
                        return -1;
                    }
                    return r1.getName().compareTo( r2.getName() );
                }
            };

}