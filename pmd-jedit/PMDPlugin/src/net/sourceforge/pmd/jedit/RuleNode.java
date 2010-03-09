package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.Rule;

public class RuleNode {

    private Rule rule;
    private final String name;

    public RuleNode( Rule rule ) {
        this.rule = rule;
        StringBuilder sb = new StringBuilder( rule.getName() );
        sb.append(" (");
        switch ( rule.getPriority() ) {
            case 1:
                sb.append( "Severe Error" );
                break;      
            case 2:         
                sb.append( "Error" );
                break;      
            case 3:         
                sb.append( "Strong Warning" );
                break;      
            case 4:         
                sb.append( "Warning" );
                break;      
            default:        
                sb.append( "Informational" );
                break;
        }
        sb.append(')');
        name = sb.toString();
    }

    public String toString() {
        return name;
    }

    public Rule getRule() {
        return rule;
    }
}