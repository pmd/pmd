package net.sourceforge.pmd.cpd.cppast;

/**
 * Holds the various attributes of a declaration. This is filled up as the
 * declaration is parsed.
 */

public class Declaration
{
   /**
    * class/struct/union is indicated by CLASS.
    */
   boolean isClass;

   /**
    * Indicates if this is a typedef declaration.
    */
   boolean isTypedef;

   /**
    * Name of the declarator.
    */
   String name;

   /**
    * Scopename. By default, it is the current scope. If the name is declared
    * with scope override operator, it will be set to that scope.
    */
   Scope scope;
}
