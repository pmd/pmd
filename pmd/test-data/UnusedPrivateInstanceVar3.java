public class UnusedPrivateInstanceVar3 {
private String foo;
public void baz() {
	bar(new Runnable() {
	public void run() {String foo = "buz";foo=null;}
});	
}
public void bar(Runnable r) {
}
}
