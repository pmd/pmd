--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

CREATE OR REPLACE PROCEDURE test ( p_num_reg OUT number )
AS
    v_query clob;
BEGIN
        v_query:='select count(1) from test_tbl where id =:param';

        execute immediate v_query into p_num_reg USING 'P';
END test;
