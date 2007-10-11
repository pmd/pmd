/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.*;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.ast.*;

/**
 *
 *
 * @author Pierre MATHIEU
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class CloseResource extends AbstractRule
{
  private List types = new ArrayList();
  private List closers = new ArrayList();

  /**
   *
   * @param node
   * @param data
   * @return
   */
  public Object visit(ASTCompilationUnit node, Object data)
  {
    if (!importsPackage(node))
    {
      return data;
    }
    if (types.isEmpty() && getStringProperty("types") != null)
    {
      for (StringTokenizer st = new StringTokenizer(getStringProperty("types"), ","); st.hasMoreTokens();)
      {
        types.add(st.nextToken());
      }
    }
    if (closers.isEmpty() && getStringProperty("closers") != null)
    {
      for (StringTokenizer st = new StringTokenizer(getStringProperty("closers"), ","); st.hasMoreTokens();)
      {
        closers.add(st.nextToken());
      }
    }
    return super.visit(node, data);
  }

  /**
   *
   * @param node
   * @param data
   * @return
   */
  public Object visit(ASTMethodDeclaration node, Object data)
  {
    List vars = node.findChildrenOfType(ASTLocalVariableDeclaration.class);
    List ids = new Vector();
    List clazzes = new Vector();

    // find all variable references to Connection objects
    for (Iterator it = vars.iterator(); it.hasNext();)
    {
      ASTLocalVariableDeclaration var = (ASTLocalVariableDeclaration)it.next();
      ASTType type = (ASTType)var.jjtGetChild(0);

      if (type.jjtGetChild(0) instanceof ASTReferenceType)
      {
        ASTReferenceType ref = (ASTReferenceType)type.jjtGetChild(0);
        if (ref.jjtGetChild(0) instanceof ASTClassOrInterfaceType)
        {
          ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType)ref.jjtGetChild(0);
          if (types.contains(clazz.getImage()))
          {
            ASTVariableDeclaratorId id = (ASTVariableDeclaratorId)var.jjtGetChild(1).jjtGetChild(0);
            ids.add(id);
            clazzes.add(clazz.getImage());
          }
        }
      }
    }

    // if there are connections, ensure each is closed.
    for (int i = 0; i < ids.size(); i++)
    {
      ASTVariableDeclaratorId x = (ASTVariableDeclaratorId)ids.get(i);
      String clazz = (String)clazzes.get(i);
      ensureClosed(clazz, (ASTLocalVariableDeclaration)x.jjtGetParent().jjtGetParent(), x, data);
    }
    return data;
  }

  /**
   *
   * @param className
   * @param var
   * @param id
   * @param data
   */
  private void ensureClosed(String className, ASTLocalVariableDeclaration var, ASTVariableDeclaratorId id, Object data)
  {
    // What are the chances of a Connection being instantiated in a
    // for-loop init block? Anyway, I'm lazy!
    String closer = (String)closers.get(types.indexOf(className));
    String target = id.getImage() + "." + closer;
    Node n = var;

    while (!((n = n.jjtGetParent()) instanceof ASTBlock));

    ASTBlock top = (ASTBlock)n;

    List tryblocks = new Vector();
    top.findChildrenOfType(ASTTryStatement.class, tryblocks, true);

    boolean closed = false;

    // look for try blocks below the line the variable was
    // introduced and make sure there is a .close call in a finally
    // block.
    for (Iterator it = tryblocks.iterator(); it.hasNext();)
    {
      ASTTryStatement t = (ASTTryStatement)it.next();

      if ((t.getBeginLine() > id.getBeginLine()) && (t.hasFinally()))
      {
        ASTBlock f = (ASTBlock)t.getFinally().jjtGetChild(0);
        List names = new ArrayList();
        f.findChildrenOfType(ASTName.class, names, true);
        for (Iterator it2 = names.iterator(); it2.hasNext();)
        {
          if (((ASTName)it2.next()).getImage().equals(target))
          {
            closed = true;
          }
        }
      }
    }

    // if all is not well, complain
    if (!closed)
    {
      ASTType type = (ASTType)var.jjtGetChild(0);
      ASTReferenceType ref = (ASTReferenceType)type.jjtGetChild(0);
      ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType)ref.jjtGetChild(0);
      addViolation(data, id, clazz.getImage());
    }
  }

  /**
   *
   * @param node
   * @return
   */
  private boolean importsPackage(ASTCompilationUnit node)
  {
    List nodes = node.findChildrenOfType(ASTImportDeclaration.class);
    for (Iterator i = nodes.iterator(); i.hasNext();)
    {
      ASTImportDeclaration n = (ASTImportDeclaration)i.next();
      if (n.getPackageName().startsWith("java.sql"))
      {
        return true;
      }
    }
    return false;
  }
}
