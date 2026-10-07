import java.util.Date

class JodaDateTimeFormatterCtorUsage {
    fun test() {
        val d1 = Date()                // line 5 - simple ctor, no args
        val d2 = java.util.Date()      // line 6 - FQN ctor, no args
    }
}
