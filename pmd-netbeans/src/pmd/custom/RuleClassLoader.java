/*
 *  RuleClassLoader.java
 *
 *  Created on 21. februar 2003, 00:31
 */
package pmd.custom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import pmd.config.PMDOptionsSettings;

/**
 * @author ole martin mørk
 * @created 21. februar 2003
 */
public class RuleClassLoader extends ClassLoader {

	/**
	 * Creates a new instance of RuleClassLoader
	 *
	 * @param parent Description of the Parameter
	 */
	public RuleClassLoader( ClassLoader parent ) {
		super( parent );
	}


	/**
	 * Description of the Method
	 *
	 * @param name Description of the Parameter
	 * @return Description of the Return Value
	 */
	protected Class findClass( String name ) {
		System.out.println( "Loading class " + name );

		String className = name.replace( '.', '/' );
		className += ".class";

		String classPath = PMDOptionsSettings.getDefault().getClasspath();
		StringTokenizer tokens = new StringTokenizer( classPath, ";" );
		try {
			while( tokens.hasMoreTokens() ) {
				File jar = new File( tokens.nextToken() );
				JarFile jarFile = new JarFile( jar, false );
				JarEntry entry = jarFile.getJarEntry( className );
				System.out.println( entry );
				if( entry != null ) {
					BufferedInputStream stream = new BufferedInputStream( jarFile.getInputStream( entry ) );
					byte buffer[] = new byte[stream.available()];
					stream.read( buffer, 0, buffer.length );
					return defineClass( name, buffer, 0, buffer.length );
				}
			}
		}
		catch( IOException e ) {
			return null;
		}
		return null;
	}


	/**
	 * Description of the Method
	 *
	 * @param args Description of the Parameter
	 * @exception Exception Description of the Exception
	 */
	public static void main( String args[] ) throws Exception {
		Class clazz = Class.forName( "net.sourceforge.pmd.Report", true, new RuleClassLoader( RuleClassLoader.class.getClassLoader() ) );
		Object o = clazz.newInstance();
		Method method = clazz.getMethod( "hasMetrics", null );
		System.out.println( method.invoke( o, null ) );
	}
}
