/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.database;

import java.io.InputStream;

/**
 * Helper class for retrieving resources relative to the installation.
 * @author Stuart Turton
 */
public class ResourceLoader {

  public InputStream getResourceStream(String path) throws java.io.IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    if (cl == null) {
      cl = ClassLoader.getSystemClassLoader();
    }
    InputStream stream = cl.getResourceAsStream(path);
    if (stream == null) {
      throw new java.io.IOException("Resource not found: " + path);
    }
    return stream;
  }
  
}
