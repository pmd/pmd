/*
 * Created on Mar 8, 2005 
 *
 * $Id$
 */
package net.sourceforge.pmd;

/**
 * Interface for classes that provide position information for a violation
 * 
 * @author mgriffa
 */
public interface IPositionProvider {
    
    int getBeginLine();
    int getEndLine();
    int getBeginColumn();
    int getEndColumn();
    
}
