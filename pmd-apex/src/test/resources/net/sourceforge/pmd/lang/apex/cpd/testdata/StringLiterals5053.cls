/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// See https://github.com/pmd/pmd/issues/5053

public with sharing class PMD7CPD {
    public static void example(){
      String str = 'alice';
      str = str.replace('alice', '<bob></charlie>dan<!--JohannHeinrichvonThünen-->' + '\u00A0' + '"100%"');
    }
}
