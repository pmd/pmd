public class EmptyIfStmtRule {
    public EmptyIfStmtRule() {
	if (null == null) {
	}
	if (null != null) {
		this.toString();
	}
    }
}
