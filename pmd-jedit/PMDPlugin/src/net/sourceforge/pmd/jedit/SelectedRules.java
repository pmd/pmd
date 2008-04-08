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

    private Comparator<Rule> ruleSorter = new Comparator<Rule>() {
                public int compare( Rule r1, Rule r2 ) {
                    if (r1 == null) {
                        return 1;
                    }
                    if (r2 == null) {
                        return -1;
                    }
                    return r1.getName().compareTo( r2.getName() );
                }
            };

    private Comparator<RuleSet> rulesetSorter = new Comparator<RuleSet>() {
                public int compare( RuleSet r1, RuleSet r2 ) {
                    if (r1 == null) {
                        return 1;
                    }
                    if (r2 == null) {
                        return -1;
                    }
                    return r1.getName().compareTo( r2.getName() );
                }
            };

    // root of tree to show rule sets and rules
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();

    // model of the checkboxes for the tree that shows the rule sets and rules
    private TreeCheckingModel checkingModel = null;

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
        List<RuleSet> rulesets = new ArrayList<RuleSet>();

        RuleSetFactory rsf = new RuleSetFactory();
        for ( Iterator<RuleSet> i = rsf.getRegisteredRuleSets(); i.hasNext(); ) {
            RuleSet rs = i.next();
            //System.out.println("Added RuleSet " + rs.getName() + " descriprion "+ rs.getDescription() +" language "+ rs.getLanguage());
            rulesets.add(rs);
        }

        // Load custom RuleSets if any, but do not die if there is any problem
        // a custom ruleset.
        try {
            String customRuleSetPath = jEdit.getProperty( "pmd.customRulesPath" );
            if ( !( customRuleSetPath == null ) ) {
                RuleSets ruleSets = rsf.createRuleSets( customRuleSetPath );    // rsnfe
                if ( ruleSets.getAllRuleSets() != null ) {
                    for (RuleSet rs : ruleSets.getAllRuleSets()) {
                        rulesets.add( rs );
                    }
                }
            }
        }
        catch(RuleSetNotFoundException e) {
            JOptionPane.showMessageDialog(null, "There was an error loading one or more custom rulesets, so no custom rulesets were loaded", "Error Loading Custom Ruleset", JOptionPane.ERROR_MESSAGE);
        }

        // sort all rulesets by name and load the tree model and root node
        Collections.sort( rulesets, rulesetSorter );
        for (RuleSet rs : rulesets) {
            addRuleSet2Rules( rs );
        }

        TreeModel treeModel = new DefaultTreeModel( root );
        checkingModel = new DefaultTreeCheckingModel( treeModel );
        selectedRules = new RuleSets();

        // load the previously saved and selected rules from jEdit properties, this
        // builds the checking model for the tree as well as the RuleSets for the
        // selected rules to pass to PMD.
        for ( int i = 0; i < root.getChildCount(); i++ ) {
            DefaultMutableTreeNode ruleSetNode = ( DefaultMutableTreeNode ) root.getChildAt( i );
            RuleSet ruleset = ( ( RuleSetNode ) ruleSetNode.getUserObject() ).getRuleSet();
            boolean hadCheckedRule = false;
            for ( int j = 0; j < ruleSetNode.getChildCount(); j++ ) {
                DefaultMutableTreeNode ruleNode = ( DefaultMutableTreeNode ) ruleSetNode.getChildAt( j );
                TreePath path = new TreePath( ruleNode.getPath() );
                RuleNode rn = ( RuleNode ) ruleNode.getUserObject();
                boolean checked = jEdit.getBooleanProperty( PMDJEditPlugin.OPTION_RULES_PREFIX + rn.getRule().getName(), true );
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


    public TreeCheckingModel getCheckingModel() {
        return checkingModel;
    }

    // save the rules selected by the user in the options pane.
    public void save( TreeCheckingModel tcm ) {
        checkingModel = tcm;
        root = ( DefaultMutableTreeNode ) checkingModel.getTreeModel().getRoot();
        selectedRules = new RuleSets();

        // need to go through all the tree nodes to turn off those that may have
        // been on and to turn on those that may have been off.  The tree is only
        // 2 levels deep, so no need for recursion.
        for ( int i = 0; i < root.getChildCount(); i++ ) {
            DefaultMutableTreeNode ruleSetNode = ( DefaultMutableTreeNode ) root.getChildAt( i );
            RuleSet ruleset = ( ( RuleSetNode ) ruleSetNode.getUserObject() ).getRuleSet();
            boolean hadCheckedRule = false;
            for ( int j = 0; j < ruleSetNode.getChildCount(); j++ ) {
                DefaultMutableTreeNode ruleNode = ( DefaultMutableTreeNode ) ruleSetNode.getChildAt( j );
                TreePath path = new TreePath( ruleNode.getPath() );
                boolean checked = checkingModel.isPathChecked( path );
                RuleNode rn = ( RuleNode ) ruleNode.getUserObject();
                jEdit.setBooleanProperty( PMDJEditPlugin.OPTION_RULES_PREFIX + rn.getRule().getName(), checked );
                if ( checked ) {
                    ruleset.addRule( rn.getRule() );
                    hadCheckedRule = true;
                }
            }
            if ( hadCheckedRule ) {
                selectedRules.addRuleSet( ruleset );
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
            DefaultMutableTreeNode ruleNode = new DefaultMutableTreeNode( new RuleNode( rule ) );
            node.add( ruleNode );
        }
        root.add( node );
    }


    public TreeNode getRoot() {
        return root;
    }
}