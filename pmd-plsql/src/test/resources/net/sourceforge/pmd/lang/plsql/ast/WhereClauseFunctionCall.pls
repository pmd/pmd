--
-- Where Clause Conditions
--

BEGIN

  SELECT adt.adt_id
    INTO v_adt_id
    FROM academic_titles adt
   WHERE UPPER(adt.short_description) = UPPER(title_in);

  SELECT rgn.rgn_id
    INTO v_region_id
    FROM regions rgn
    WHERE (street_cny_code_in IS NULL
       OR rgn.cny_code = street_cny_code_in)
       AND UPPER(rgn.name) = UPPER(street_rgn_in);

  SELECT value
    INTO v_value
    FROM mytable
    WHERE colname = utils.get_colname('COLUMN_ID');

  SELECT foo
    INTO bar
    FROM DUAL
    WHERE a = 1 AND b = 2 AND NVL(INSTR(c || d), 3) = 3
    ORDER BY 1;

END;
/
