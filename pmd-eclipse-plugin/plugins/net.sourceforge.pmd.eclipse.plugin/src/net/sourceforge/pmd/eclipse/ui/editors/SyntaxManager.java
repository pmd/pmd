package net.sourceforge.pmd.eclipse.ui.editors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * This class manages the syntax coloring and styling data
 */
public class SyntaxManager {

  private static Map<String, SyntaxData> syntaxByExtension = new Hashtable<String, SyntaxData>();

  public static ModifyListener adapt(final StyledText codeField, String languageCode, ModifyListener oldListener) {
  	
  	if (oldListener != null) {
  		codeField.removeModifyListener(oldListener);
  	}
  	
      SyntaxData sd = SyntaxManager.getSyntaxData(languageCode);
      if (sd == null) {
      	//codeField.set	clear the existing style ranges	TODO
      	return null;
      }
            
      final BasicLineStyleListener blsl = new BasicLineStyleListener(sd);
      codeField.addLineStyleListener(blsl);  
      
      ModifyListener ml = new ModifyListener() {
          public void modifyText(ModifyEvent event) {           
          	blsl.refreshMultilineComments(codeField.getText());
              codeField.redraw(); 
          }
      };
      codeField.addModifyListener(ml);
      
      return ml;
  }
  
  /**
   * Gets the syntax data for an extension
   */
  public static synchronized SyntaxData getSyntaxData(String extension) {
    // Check in cache
    SyntaxData sd = syntaxByExtension.get(extension);
    if (sd == null) {
      // Not in cache; load it and put in cache
      sd = loadSyntaxData(extension);
      if (sd != null)
    	  syntaxByExtension.put(sd.getExtension(), sd);
    }
    return sd;
  }

  /**
   * Loads the syntax data for an extension
   * 
   * @param extension
   *          the extension to load
   * @return SyntaxData
   */
  private static SyntaxData loadSyntaxData(String filename) {
    SyntaxData sd = null;
    try {
      ResourceBundle rb = ResourceBundle.getBundle("net.sourceforge.pmd.eclipse.ui.editors." + filename);
      sd = new SyntaxData(filename);

      sd.stringStart = rb.getString("stringstart");
      sd.stringEnd = rb.getString("stringend");
      sd.setMultiLineCommentStart(rb.getString("multilinecommentstart"));
      sd.setMultiLineCommentEnd(rb.getString("multilinecommentend"));

      // Load the keywords
      Collection<String> keywords = new HashSet<String>();
      for (StringTokenizer st = new StringTokenizer(rb.getString("keywords"), " "); st.hasMoreTokens();) {
        keywords.add(st.nextToken());
      }
      sd.setKeywords(keywords);

      // Load the punctuation
      sd.setPunctuation(rb.getString("punctuation"));
      
      if (rb.containsKey("comment")) {
    	  sd.setComment( rb.getString("comment") );
      }  
      
      if (rb.containsKey("varnamedelimiter")) {
    	  sd.varnameReference = rb.getString("varnamedelimiter");
      }      
      
    } catch (MissingResourceException e) {
      // Ignore
    }
    return sd;
  }
}