--
-- WITH Clause
--

BEGIN

  WITH titles as ( SELECT * FROM academic_titles )
  SELECT adt.adt_id
    FROM titles adt;

  WITH titles as ( SELECT * FROM academic_titles )
  SELECT adt.adt_id
    INTO v_adt_id
    FROM titles adt;

END;
/
