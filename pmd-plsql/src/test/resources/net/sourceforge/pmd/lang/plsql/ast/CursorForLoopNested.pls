--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

BEGIN
FOR c_cmp IN (SELECT * FROM companies) LOOP
  FOR c_con IN (SELECT * FROM contacts) LOOP
    FOR c_pa IN (SELECT * FROM parties) LOOP
      NULL;
    END LOOP;
  END LOOP;
END LOOP;
END;
/
