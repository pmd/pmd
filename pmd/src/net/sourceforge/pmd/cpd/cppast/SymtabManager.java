package net.sourceforge.pmd.cpd.cppast;

import java.util.Hashtable;

/**
 * Manages the symbol table and scopes within a given compilation unit.
 */
public class SymtabManager
{
   /**
    * Global symbol table indexed by the name of the scope (class/function).
    */
   static Hashtable scopeTable = new Hashtable();

   /**
    * Stack of scopes. Currently max. nesting allowed is 100.
    */
   static Scope[] scopeStack = new Scope[100];

   /**
    * Current depth of scope nesting.
    */
   static int depth = 0;

   /**
    * Dummy at the bottom of the stack so that no need to check for null.
    */
   static
   {
      scopeStack[depth] = new Scope(null);
   }

   /**
    * Opens a new scope (with optional name and type flag).
    */
   public static Scope OpenScope(String scopeName, boolean isType)
   {
      Scope newScope;

      if (scopeName != null)
      {
         if (isType)
         {
            newScope = new ClassScope(scopeName, scopeStack[depth]);
            scopeStack[depth].PutTypeName(scopeName, newScope);
         }
         else
         {
            newScope = new Scope(scopeName, isType, scopeStack[depth]);
         }

         scopeTable.put(scopeName, newScope);
      }
      else
         newScope = new Scope(scopeStack[depth]);

      scopeStack[++depth] = newScope;
      return newScope;
   }

   public static void OpenScope(Scope sc)
   {
      scopeStack[++depth] = sc;
   }

   public static void PutTypeName(String name)
   {
      scopeStack[depth].PutTypeName(name);
   }

   public static boolean IsFullyScopedTypeName(String name)
   {
      if (name == null)
         return false;

      if (name.indexOf("::") == -1)
         return IsTypeName(name);

      Scope sc = GetScopeOfFullyScopedName(name);

      if (sc != null)
         return sc.IsTypeName(name.substring(name.lastIndexOf("::") + 2,
                                                     name.length()));

      return false;
   }

   public static boolean IsTypeName(String name)
   {
      int i = depth;

      while (i >= 0)
      {
         if (scopeStack[i--].IsTypeName(name))
            return true;
      }

      return false;
   }

   public static void CloseScope()
   {
      depth--;
   }

   /**
    * For now, we just say if it is a class name, it is OK to call it a 
    * constructor.
    */
   public static boolean IsCtor(String name)
   {
      if (name == null)
         return false;

      if (name.indexOf("::") == -1)
         return GetScope(name) != null;

      Scope sc = GetScopeOfFullyScopedName(name);

      if (sc != null && sc.parent != null)
         return sc.parent.GetScope(name.substring(name.lastIndexOf("::") + 2,
                                                     name.length())) == sc;

      return false;
   }

   public static Scope GetCurScope()
   {
      return scopeStack[depth];
   }

   public static Scope GetScope(String name)
   {
      int i = depth;
      Scope sc = null;

      while (i >= 0)
         if ((sc = scopeStack[i--].GetScope(name)) != null)
            return sc;

      return null;
   }

   /**
    * Returns the Scope of B in A::B::C.
    */
   public static Scope GetScopeOfFullyScopedName(String name)
   {
      Scope sc;
      int i = 0, j = 0;

      if (name.indexOf("::") == -1)
         return GetScope(name);

      if (name.indexOf("::") == 0)
      {
         sc = scopeStack[1];
         j = 2;
      }
      else
         sc = GetCurScope();

      String tmp = name.substring(j, name.lastIndexOf("::"));
      
      while ((j = tmp.indexOf("::", i)) != -1)
      {
         sc = sc.GetScope(tmp.substring(i, j));
         i = j + 2;

         if (sc == null)
            return null;
      }

      if (sc == GetCurScope())
         return GetScope(tmp.substring(i, tmp.length()));

      return sc.GetScope(tmp.substring(i, tmp.length()));
   }

   public static boolean IsGlobalScope()
   {
      return depth == 1 || depth == 2;
   }
}
