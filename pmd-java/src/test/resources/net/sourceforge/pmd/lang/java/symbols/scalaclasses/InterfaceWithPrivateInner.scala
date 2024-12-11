package net.sourceforge.pmd.lang.java.symbols.scalaclasses

/**
 * This source is compiled manually with scalac 2.12 and
 * the classes are checked in for tests. In Scala,
 * class members of interfaces can be private, not in Java.
 *
 * Note that scalac >3 does not generate private classes
 * for these!
 */
trait InterfaceWithPrivateInner {
  import InterfaceWithPrivateInner._

  // This class has modifiers private
  private class Inner

  def andThen : InterfaceWithPrivateInner = new AndThen()

}

object InterfaceWithPrivateInner {
   // This class has modifiers private static
   private class AndThen extends InterfaceWithPrivateInner
}
