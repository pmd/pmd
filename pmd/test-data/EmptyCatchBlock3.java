public class EmptyCatchBlock3 {

	private void bar() {}

	private void foo() {
		try {
		} finally {
		      try {
		        if (null != null) bar();
		      } catch (Exception e) {}
	    }
	}

}
