import java.lang.Exception
import java.lang.RuntimeException
import java.util.ArrayList
import java.util.HashSet

// typeIsExactly on PostfixUnaryExpression (constructor call) use cases

class Thrower {

    // line 10: throw Exception("msg") -- PostfixUnaryExpression should match typeIsExactly('java.lang.Exception')
    fun throwExact() {
        throw Exception("msg")
    }

    // line 15: throw RuntimeException("msg") -- should match typeIsExactly('java.lang.RuntimeException')
    fun throwRuntimeExact() {
        throw RuntimeException("msg")
    }

    // line 20: throw IllegalArgumentException("msg") -- should NOT match typeIsExactly('java.lang.Exception')
    fun throwSubtype() {
        throw IllegalArgumentException("msg")
    }
}

// line 26: val list: List<String> = ArrayList() -- PropertyDeclaration with interface declared type
//           typeIsExactly('java.util.ArrayList') must NOT match (declared type is List, not ArrayList)
class ListHolder {
    val items: List<String> = ArrayList()
    val set: Set<String> = HashSet()
}
