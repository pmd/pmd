--
-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html
--

BEGIN

SELECT the_id
    INTO my_record.the_id
    FROM my_table
    WHERE the_id = '1';

END;
/