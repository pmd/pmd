/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

/**
 * @author Clément Fournier
 */
public abstract class BaseScalaTest {

    protected final ScalaParsingHelper scala = ScalaParsingHelper.DEFAULT.withResourceContext(getClass());

}
