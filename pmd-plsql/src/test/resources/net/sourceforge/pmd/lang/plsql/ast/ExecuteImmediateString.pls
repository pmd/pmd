--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE PROCEDURE test ( p_num_reg OUT number )
AS
BEGIN
        execute immediate 'select count(1) from test_tbl where id =:param' into p_num_reg USING 'P';
END test;
