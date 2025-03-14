DECLARE
    bonus   NUMBER(8,2);
BEGIN

SELECT COUNT(*) INTO x FROM y1;
SELECT COUNT (*)  INTO x FROM y2;
SELECT COUNT ( * )  INTO x FROM y2;
SELECT COUNT(DISTINCT other_id) INTO x FROM y3;
SELECT COUNT(ALL other_id) INTO x FROM y3;
SELECT COUNT(UNIQUE other_id) INTO x FROM y3;

SELECT
   some_col "alias in quotes"
   INTO some_record
   FROM some_table;

SELECT
        COUNT(*) "Total Empl"
   INTO some_record
   FROM some_table;

END;
/
