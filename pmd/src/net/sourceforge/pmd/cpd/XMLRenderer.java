/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.util.StringUtil;

import java.util.Iterator;

/**
 * @author  Philippe T'Seyen
 */
public class XMLRenderer implements Renderer
{
  public String render(Iterator matches)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\"?>");
    buffer.append("<pmd-cpd>");
    for (;matches.hasNext();)
    {
      Match match = (Match) matches.next();
      buffer.append("<duplication");
      buffer.append(" lines=\"");
      buffer.append(match.getLineCount());
      buffer.append("\"");
      buffer.append(" tokens=\"");
      buffer.append(match.getTokenCount());
      buffer.append("\">");

      for (Iterator iterator = match.iterator(); iterator.hasNext();)
      {
        Mark mark = (Mark) iterator.next();
        buffer.append("<file");
        buffer.append(" line=\"");
        buffer.append(mark.getBeginLine());
        buffer.append("\"");
        buffer.append(" path=\"");
        buffer.append(mark.getTokenSrcID());
        buffer.append("\"/>");
      }
      String codeFragment = match.getSourceCodeSlice();
      if (codeFragment != null)
      {
        buffer.append("<codefragment><![CDATA[" + PMD.EOL + StringUtil.replaceString(codeFragment, "]]>", "]]&gt;") + PMD.EOL + "]]></codefragment>");
      }
      buffer.append("</duplication>");
    }
    buffer.append("</pmd-cpd>");
    return buffer.toString();
  }
}
