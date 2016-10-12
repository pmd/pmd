public class Bug1530 {
	public void incChild() {
        ((PathElement) stack.getLastLeaf().getUserObject()).currentChild++;
    }
}