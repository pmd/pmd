package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {

  // Single static method, so we shouldn't allow an instance to be created
  private ResourceLoader() {}

  /**
   *
   * Method to find a file, first by finding it as a file
   * (either by the absolute or relative path), then as
   * a URL, and then finally seeing if it is on the classpath.
   *
   */
  public static InputStream loadResourceAsStream( String name ) {
    File file = new File( name );
    if ( file.exists() ) {
      try {
        return new FileInputStream( file );
      } catch (FileNotFoundException e) {
        // if the file didn't exist, we wouldn't be here
      }
    } else {
      try {
        return new URL( name ).openConnection().getInputStream();
      } catch ( Exception e ) {
        return new ResourceLoader().getClass().getClassLoader().getResourceAsStream(name);
      }
    }
    return null;
  }

}
