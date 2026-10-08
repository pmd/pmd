/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

fun variableTypes(items: List<String>, pairs: List<Pair<Int, String>>) {
    val single: String = "x"
    val (first, second) = Pair(1, "y")
    for (item in items) { }
    for ((number, text) in pairs) { }
    pairs.forEach { (left, right) -> }
    items.forEach { typed: String -> }
    items.forEach { inferred -> }
}
