--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

DECLARE
    a   NUMBER(8,2);
    b   NUMBER(8,2);
BEGIN

SELECT SUM(field1),
       MAX(field2)
    INTO a,b
    FROM  test_tbl
    GROUP BY fieldx;

END;
/