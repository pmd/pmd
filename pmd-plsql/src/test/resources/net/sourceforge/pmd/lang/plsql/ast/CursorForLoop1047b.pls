--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

-- this one was processed without errors
CREATE OR REPLACE PROCEDURE test2 ( a OUT number, b OUT number )
 AS
BEGIN
        FOR registro IN ( SELECT  SUM(field1),MAX(field2) INTO a,b FROM  test_tbl GROUP BY fieldx )
        LOOP
            null;
        END LOOP;
END test2;
