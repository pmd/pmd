package net.sourceforge.pmd.cpd.cppast;

import java.util.Vector;

/**
 * Class scope extends Scope in that its search method also searches all its
 * superclasses.
 */

public class ClassScope extends Scope
{
   /**
    * The list of scopes corresponding to classes this class inherits.
    */
   Vector superClasses;

   /**
    * Add a super class.
    */
   public void AddSuper(Scope sc)
   {
      if (sc == null)
         return;

      if (superClasses == null)
         superClasses = new Vector();

      superClasses.addElement(sc);
   }

   /**
    * Overrides the method in Scope. It also searches in the inherited classes'
    * scopes also.
    */
   public boolean IsTypeName(String name)
   {
      if (super.IsTypeName(name))
         return true;

      if (superClasses == null)
         return false;

      for (int i = 0; i < superClasses.size(); i++)
         if (((Scope)superClasses.elementAt(i)).IsTypeName(name))
            return true;

      return false;
   }

   /**
    * Creates a new class scope in a given scope.
    */
   public ClassScope(String name, Scope parent)
   {
      super(name, true, parent);
   }
}
