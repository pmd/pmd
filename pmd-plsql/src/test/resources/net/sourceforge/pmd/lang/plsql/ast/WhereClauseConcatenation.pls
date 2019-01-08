--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--
-- See https://github.com/pmd/pmd/issues/1507
--

BEGIN

SELECT *
  INTO x
  FROM y
  WHERE a = 'a' || 'b';

END;
/