public class UnusedPrivateInstanceVar5 {
    public void bar() {
        Runnable r = new Runnable() {
		public void run() {
	            String foo = "";
		}
        };
	r = null;
    }
    private String foo;
}
