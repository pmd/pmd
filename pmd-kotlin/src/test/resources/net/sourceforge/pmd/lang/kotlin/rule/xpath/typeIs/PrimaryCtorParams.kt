package nl.stokpop.kotlin

import java.io.Serializable

// Primary constructor val/var parameters -- ClassParameter nodes in the AST.
// 'value' should match typeIs('kotlin.String')
// 'id' should match typeIs('kotlin.Int') / typeIs('java.lang.Integer')
// 'tag' should NOT match typeIs('kotlin.String')
class PrimaryCtorParams(          // line 9
    val value: String,            // line 10
    val id: Int,                  // line 11
    val tag: Long                 // line 12
) : Serializable
