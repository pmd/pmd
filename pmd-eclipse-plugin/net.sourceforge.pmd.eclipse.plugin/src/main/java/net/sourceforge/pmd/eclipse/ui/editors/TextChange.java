package net.sourceforge.pmd.eclipse.ui.editors;

class TextChange {
  // The starting offset of the change
  private int start;

  // The length of the change
  private int length;

  // The replaced text
  String replacedText;

  /**
   * Constructs a TextChange
   * 
   * @param start
   *          the starting offset of the change
   * @param length
   *          the length of the change
   * @param replacedText
   *          the text that was replaced
   */
  public TextChange(int start, int length, String replacedText) {
    this.start = start;
    this.length = length;
    this.replacedText = replacedText;
  }

  /**
   * Returns the start
   * 
   * @return int
   */
  public int getStart() {
    return start;
  }

  /**
   * Returns the length
   * 
   * @return int
   */
  public int getLength() {
    return length;
  }

  /**
   * Returns the replacedText
   * 
   * @return String
   */
  public String getReplacedText() {
    return replacedText;
  }
}