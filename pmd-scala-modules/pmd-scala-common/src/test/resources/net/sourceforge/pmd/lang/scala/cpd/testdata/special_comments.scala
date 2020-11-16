// Testing CPD suppression

// Irrelevant comment
// CPD-OFF
// CPD-ON

case class Foo() { // special multiline comments

  /* CPD-OFF
  *
  *
  * */ val hi = "Hello"  /* This is a comment ending in CPD-ON */

  private def bar(i: Int) : Int = {
    val CPD = 40
    val OFF = 60
    CPD-OFF   // This should tokenize
  }

  /* CPD-OFF */
  def bar2(s: String): String = "bar2"
  /* CPD-ON */

}
