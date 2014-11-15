/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.database;

import javax.xml.transform.URIResolver;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

public class ResourceResolver implements URIResolver
{
  @Override
  public Source resolve(String href, String base) 
                              throws TransformerException
  {
    if(null==href || href.length() == 0 ) {
      return null; // will make Oracle XSLT processor explode, 
                   // even though it's correct
    }
    try    {
      String resource = href; 
      ResourceLoader loader = new ResourceLoader();
      return new StreamSource(loader.getResourceStream(resource), resource);
    } // try
    catch(Exception ex)
    {
      throw new TransformerException(ex);
    } // catch
  } // resolve
} // ResourceResolver

