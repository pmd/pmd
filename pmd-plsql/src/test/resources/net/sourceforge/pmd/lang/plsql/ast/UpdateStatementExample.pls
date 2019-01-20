--
-- Example 5-55, "Updating Rows with Records"
-- https://docs.oracle.com/en/database/oracle/oracle-database/18/lnpls/plsql-collections-and-records.html#GUID-11D63245-591D-4CBF-BFBA-8F3C0AE0E968__BABDGCDI
--

DECLARE
  default_week  schedule%ROWTYPE;
BEGIN
  default_week.Mon := 'Day Off';
  default_week.Tue := '0900-1800';
  default_week.Wed := '0900-1800';
  default_week.Thu := '0900-1800';
  default_week.Fri := '0900-1800';
  default_week.Sat := '0900-1800';
  default_week.Sun := 'Day Off';

  FOR i IN 1..3 LOOP
    default_week.week    := i;

    UPDATE schedule
      SET ROW = default_week
      WHERE week = i;

    UPDATE schedule
      SET Mon = 'Day Off',
          Tue = 'Day Off'
      WHERE week = i;
  END LOOP;
END;
/

SELECT * FROM schedule;
