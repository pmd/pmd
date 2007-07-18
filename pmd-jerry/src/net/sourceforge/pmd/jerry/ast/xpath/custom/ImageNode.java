package net.sourceforge.pmd.jerry.ast.xpath.custom;

import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;

public class ImageNode extends SimpleNode {

	private String image;

	public ImageNode(int id) {
		super(id);
	}

	public ImageNode(XPath2Parser p, int id) {
		super(p, id);
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
