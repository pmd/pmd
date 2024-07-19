--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

-- See https://github.com/pmd/pmd/issues/1934

BEGIN

  MERGE INTO jhs_translations b
      USING ( SELECT 'PROM_EDIT_PROM_NR' key1,'Edycja promocji nr' text,123123 lce_id FROM dual ) e
      ON (b.key1 = e.key1 and b.lce_id=e.lce_id)
      WHEN MATCHED
        THEN  UPDATE SET b.text = e.text
      WHEN NOT MATCHED
        THEN  INSERT (ID,KEY1, TEXT,LCE_ID) values (JHS_SEQ.NEXTVAL,'PROM_EDIT_PROM_NR','Edycja promocji nr',123123);


  -- Missing alias
  MERGE INTO b
      USING ( SELECT 'PROM_EDIT_PROM_NR' key1,'Edycja promocji nr' text,123123 lce_id FROM dual ) e
      ON (b.key1 = e.key1 and b.lce_id=e.lce_id)
      WHEN MATCHED
        THEN  UPDATE SET b.text = e.text
      WHEN NOT MATCHED
        THEN  INSERT (ID,KEY1, TEXT,LCE_ID) values (JHS_SEQ.NEXTVAL,'PROM_EDIT_PROM_NR','Edycja promocji nr',123123);

  -- Both aliases missing
  MERGE INTO b
      USING e
      ON (b.key1 = e.key1 and b.lce_id=e.lce_id)
      WHEN MATCHED
        THEN  UPDATE SET b.text = e.text
      WHEN NOT MATCHED
        THEN  INSERT (ID,KEY1, TEXT,LCE_ID) values (JHS_SEQ.NEXTVAL,'PROM_EDIT_PROM_NR','Edycja promocji nr',123123);

END;
/
