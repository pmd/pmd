package net.sourceforge.pmd.cpd.cppast;

import java.util.Hashtable;

public class Scope
{
   /**
    * Name of the scope (set only for class/function scopes).
    */
   String scopeName;

   /**
    * Indicates whether this is a class scope or not.
    */
   boolean type;     // Indicates if this is a type.

   /**
    * (partial) table of type symbols introduced in this scope.
    */
   Hashtable typeTable = new Hashtable();

   /**
    * Parent scope. (null if it is the global scope).
    */
   Scope parent;

   /**
    * Creates a scope object with a given name.
    */
   public Scope(String name, boolean isType, Scope p)
   {
      scopeName = name;
      type = isType;
      parent = p;
   }

   /**
    * Creates an unnamed scope (like for compound statements).
    */
   public Scope(Scope p)
   {
      type = false;
      parent = p;
   }

   /**
    * Inserts a name into the table to say that it is the name of a type.
    */
   public void PutTypeName(String name)
   {
      typeTable.put(name, name);
   }

   /**
    * A type with a scope (class/struct/union).
    */
   public void PutTypeName(String name, Scope sc)
   {
      typeTable.put(name, sc);
   }

   /** 
    * Checks if a given name is the name of a type in this scope.
    */
   public boolean IsTypeName(String name)
   {
      return typeTable.get(name) != null;
   }

   public Scope GetScope(String name)
   {
      Object sc = typeTable.get(name);

      if (sc instanceof Scope || sc instanceof ClassScope)
         return (Scope)sc;

      return null;
   }
}
