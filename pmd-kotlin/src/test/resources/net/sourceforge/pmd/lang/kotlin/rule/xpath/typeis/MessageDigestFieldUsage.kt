import java.security.MessageDigest

class MessageDigestFieldUsage {
    private val md: MessageDigest = MessageDigest.getInstance("SHA-256")

    fun safeUse() {
        val localMd: MessageDigest = MessageDigest.getInstance("SHA-256")
    }
}
