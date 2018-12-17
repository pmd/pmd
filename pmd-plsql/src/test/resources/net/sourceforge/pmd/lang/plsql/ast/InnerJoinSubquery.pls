BEGIN

SELECT a
  INTO b
  FROM c tablealias
  INNER JOIN ( SELECT e FROM f ) ON (f.id = tablealias.id);

SELECT a
  INTO b
  FROM c tablealias
  JOIN ( SELECT e FROM f ) ON (f.id = tablealias.id);


END;
/
