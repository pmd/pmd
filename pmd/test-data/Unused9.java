public class Unused9 {
public void foo() {
	String x = "baf";
	bar(new Runnable() {
	public void run() {String x = "buz";}
});	
}
public void bar(Runnable r) {
}
}
