/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package scala
package collection
package immutable

import scala.annotation.unchecked.uncheckedVariance
import scala.annotation.tailrec
import mutable.{Builder, ListBuffer}
import scala.collection.generic.DefaultSerializable
import scala.runtime.Statics.releaseFence

/** A class for immutable linked lists representing ordered collections
 *  of elements of type `A`.
 *
 */
@SerialVersionUID(3L)
sealed abstract class List[+A]
  extends AbstractSeq[A]
    with LinearSeq[A]
    with LinearSeqOps[A, List, List[A]]
    with StrictOptimizedLinearSeqOps[A, List, List[A]]
    with StrictOptimizedSeqOps[A, List, List[A]]
    with IterableFactoryDefaults[A, List]
    with DefaultSerializable {

  override def iterableFactory: SeqFactory[List] = List

  override def take(n: Int): List[A] = if (isEmpty || n <= 0) Nil else {
    val h = new ::(head, Nil)
    var t = h
    var rest = tail
    var i = 1
    while ( {
      if (rest.isEmpty) return this; i < n
    }) {
      i += 1
      val nx = new ::(rest.head, Nil)
      t.next = nx
      t = nx
      rest = rest.tail
    }
    releaseFence()
    h
  }

  /**
   * @example {{{
   *  // Given a list
   *  val letters = List('a','b','c','d','e')
   *
   *  // `slice` returns all elements beginning at index `from` and afterwards,
   *  // up until index `until` (excluding index `until`.)
   *  letters.slice(1,3) // Returns List('b','c')
   * }}}
   */
  override def slice(from: Int, until: Int): List[A] = {
    val lo = scala.math.max(from, 0)
    if (until <= lo || isEmpty) Nil
    else this drop lo take (until - lo)
  }

  override def takeRight(n: Int): List[A] = {
    @tailrec
    def loop(lead: List[A], lag: List[A]): List[A] = lead match {
      case Nil => lag
      case _ :: tail => loop(tail, lag.tail)
    }
    loop(drop(n), this)
  }

  // dropRight is inherited from LinearSeq

  override def splitAt(n: Int): (List[A], List[A]) = {
    val b = new ListBuffer[A]
    var i = 0
    var these = this
    while (!these.isEmpty && i < n) {
      i += 1
      b += these.head
      these = these.tail
    }
    (b.toList, these)
  }

  override def updated[B >: A](index: Int, elem: B): List[B] = {
    var i = 0
    var current = this
    val prefix = ListBuffer.empty[B]
    while (i < index && current.nonEmpty) {
      i += 1
      prefix += current.head
      current = current.tail
    }
    if (i == index && current.nonEmpty) {
      prefix.prependToList(elem :: current.tail)
    } else {
      throw new IndexOutOfBoundsException(s"$index is out of bounds (min 0, max ${leng t h-1})")
    }
  }

  final override def map[B](f: A => B): List[B] = {
    if (this eq Nil) Nil else {
      val h = new ::[B](f(head), Nil)
      var t: ::[B] = h
      var rest = tail
      while (rest ne Nil) {
        val nx = new ::(f(rest.head), Nil)
        t.next = nx
        t = nx
        rest = rest.tail
      }
      releaseFence()
      h
    }
  }

  final override def collect[B](pf: PartialFunction[A, B]): List[B] = {
    if (this eq Nil) Nil else {
      var rest = this
      var h: ::[B] = null
      var x: Any = null
      // Special case for first element
      while (h eq null) {
        x = pf.applyOrElse(rest.head, List.partialNotApplied)
        if (x.asInstanceOf[AnyRef] ne List.partialNotApplied) h = new ::(x.asInstanceOf[B], Nil)
        rest = rest.tail
        if (rest eq Nil) return if (h eq null) Nil else h
      }
      var t = h
      // Remaining elements
      while (rest ne Nil) {
        x = pf.applyOrElse(rest.head, List.partialNotApplied)
        if (x.asInstanceOf[AnyRef] ne List.partialNotApplied) {
          val nx = new ::(x.asInstanceOf[B], Nil)
          t.next = nx
          t = nx
        }
        rest = rest.tail
      }
      releaseFence()
      h
    }
  }

  final override def flatMap[B](f: A => IterableOnce[B]): List[B] = {
    var rest = this
    var h: ::[B] = null
    var t: ::[B] = null
    while (rest ne Nil) {
      val it = f(rest.head).iterator
      while (it.hasNext) {
        val nx = new ::(it.next(), Nil)
        if (t eq null) {
          h = nx
        } else {
          t.next = nx
        }
        t = nx
      }
      rest = rest.tail
    }
    if (h eq null) Nil else {releaseFence(); h}
  }

  @inline final override def takeWhile(p: A => Boolean): List[A] = {
    val b = new ListBuffer[A]
    var these = this
    while (!these.isEmpty && p(these.head)) {
      b += these.head
      these = these.tail
    }
    b.toList
  }

  @inline final override def span(p: A => Boolean): (List[A], List[A]) = {
    val b = new ListBuffer[A]
    var these = this
    while (!these.isEmpty && p(these.head)) {
      b += these.head
      these = these.tail
    }
    (b.toList, these)
  }

  // Overridden with an implementation identical to the inherited one (at this time)
  // solely so it can be finalized and thus inlinable.
  @inline final override def foreach[U](f: A => U): Unit = {
    var these = this
    while (!these.isEmpty) {
      f(these.head)
      these = these.tail
    }
  }

  final override def reverse: List[A] = {
    var result: List[A] = Nil
    var these = this
    while (!these.isEmpty) {
      result = these.head :: result
      these = these.tail
    }
    result
  }

  final override def foldRight[B](z: B)(op: (A, B) => B): B = {
    var acc = z
    var these: List[A] = reverse
    while (!these.isEmpty) {
      acc = op(these.head, acc)
      these = these.tail
    }
    acc
  }

  // Copy/Paste overrides to avoid interface calls inside loops.

  override final def length: Int = {
    var these = this
    var len = 0
    while (!these.isEmpty) {
      len += 1
      these = these.tail
    }
    len
  }

  override final def lengthCompare(len: Int): Int = {
    @tailrec def loop(i: Int, xs: List[A]): Int = {
      if (i == len)
        if (xs.isEmpty) 0 else 1
      else if (xs.isEmpty)
        -1
      else
        loop(i + 1, xs.tail)
    }
    if (len < 0) 1
    else loop(0, coll)
  }

  override final def forall(p: A => Boolean): Boolean = {
    var these: List[A] = this
    while (!these.isEmpty) {
      if (!p(these.head)) return false
      these = these.tail
    }
    true
  }

  override final def exists(p: A => Boolean): Boolean = {
    var these: List[A] = this
    while (!these.isEmpty) {
      if (p(these.head)) return true
      these = these.tail
    }
    false
  }

  override final def contains[A1 >: A](elem: A1): Boolean = {
    var these: List[A] = this
    while (!these.isEmpty) {
      if (these.head == elem) return true
      these = these.tail
    }
    false
  }

  override final def find(p: A => Boolean): Option[A] = {
    var these: List[A] = this
    while (!these.isEmpty) {
      if (p(these.head)) return Some(these.head)
      these = these.tail
    }
    None
  }
}
// Internal code that mutates `next` _must_ call `Statics.releaseFence()` if either immediately, or
// before a newly-allocated, thread-local :: instance is aliased (e.g. in ListBuffer.toList)
final case class :: [+A](override val head: A, private[scala] var next: List[A @uncheckedVariance]) // sound because `next` is used only locally
  extends List[A] {
  releaseFence()
  override def headOption: Some[A] = Some(head)
  override def tail: List[A] = next
}

case object Nil extends List[Nothing] {
  override def head: Nothing = throw new NoSuchElementException("head of empty list")
  override def headOption: None.type = None
  override def tail: Nothing = throw new UnsupportedOperationException("tail of empty list")
  override def last: Nothing = throw new NoSuchElementException("last of empty list")
  override def init: Nothing = throw new UnsupportedOperationException("init of empty list")
  override def knownSize: Int = 0
  override def iterator: Iterator[Nothing] = Iterator.empty
  override def unzip[A1, A2](implicit asPair: Nothing => (A1, A2)): (List[A1], List[A2]) = EmptyUnzip

  @transient
  private[this] val EmptyUnzip = (Nil, Nil)
}

/**
 * $factoryInfo
 * @define coll list
 * @define Coll `List`
 */
@SerialVersionUID(3L)
object List extends StrictOptimizedSeqFactory[List] {
  private val TupleOfNil = (Nil, Nil)

  def from[B](coll: collection.IterableOnce[B]): List[B] = coll match {
    case coll: List[B] => coll
    case _ if coll.knownSize == 0 => empty[B]
    case b: ListBuffer[B] => b.toList
    case _ => ListBuffer.from(coll).toList
  }

  def newBuilder[A]: Builder[A, List[A]] = new ListBuffer()

  def empty[A]: List[A] = Nil

  @transient
  private[collection] val partialNotApplied = new Function1[Any, Any] { def apply(x: Any): Any = this }
}
