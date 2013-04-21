package net.sourceforge.pmd.eclipse.ui.views.dataflow;


/**
 * Data Class for storing an Element as well as 
 * SWT-Values, that determine how to show the Element 
 * (centered, left and so on)
 * 
 * @author SebastianRaffel  ( 02.06.2005 )
 */
public class DataflowGraphTableData {
	
	private Object data;
	private int style;

	/**
	 * Constructor
	 * 
	 * @param data, the Data to show
	 * @param style, the SWT-Style, how to show the Element
	 */
	public DataflowGraphTableData(Object data, int style) {
		this.data = data;
		this.style = style;
	}
	
	/**
	 * Sets the Style, how to show the Data
	 * 
	 * @param style
	 */
	public void setStyle(int style) {
		this.style = style;
	}
	
	/**
	 * Gets the Style, the Data are shown in
	 * 
	 * @return an SWT-Style-Int
	 */
	public int getStyle() {
		return style;
	}
	
	/**
	 * Sets the Data to show
	 * 
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * Returns the Data to show
	 * 
	 * @return the Data
	 */
	public Object getData() { 
		return data;
	}
}
