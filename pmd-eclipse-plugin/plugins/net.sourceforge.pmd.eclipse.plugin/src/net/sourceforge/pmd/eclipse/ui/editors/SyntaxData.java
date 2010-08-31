package net.sourceforge.pmd.eclipse.ui.editors;

import java.util.Collection;

/**
 * This class contains information for syntax coloring and styling for an
 * extension
 */
public class SyntaxData {
	
  private String extension;
  private Collection<String> keywords;
  private String punctuation;
  private String comment;
  private String multiLineCommentStart;
  private String multiLineCommentEnd;
  public String varnameReference;
  public String stringStart;  
  public String stringEnd;
  
  /**
   * Constructs a SyntaxData
   * 
   * @param extension
   *          the extension
   */
  public SyntaxData(String extension) {
    this.extension = extension;
  }

  public boolean matches(String otherExtension) {
	  return extension.equals(otherExtension);
  }
  
  public String getExtension() {
    return extension;
  }

  public void setVarnameReference(String refId) {
	  varnameReference = refId;
  }
  
  public String getVarnameReference() {
	  return varnameReference;
  }
  
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public boolean isKeyword(String word) {
	  return keywords != null && keywords.contains(word);
  }
  
  public boolean isPunctuation(char ch) {
	  return punctuation != null && punctuation.indexOf(ch) >= 0;
  }

  public void setKeywords(Collection<String> keywords) {
    this.keywords = keywords;
  }

  public String getMultiLineCommentEnd() {
    return multiLineCommentEnd;
  }

  public void setMultiLineCommentEnd(String multiLineCommentEnd) {
    this.multiLineCommentEnd = multiLineCommentEnd;
  }

  public String getMultiLineCommentStart() {
    return multiLineCommentStart;
  }

  public void setMultiLineCommentStart(String multiLineCommentStart) {
    this.multiLineCommentStart = multiLineCommentStart;
  }

  public void setPunctuation(String thePunctuationChars) {
    punctuation = thePunctuationChars;
  }
}