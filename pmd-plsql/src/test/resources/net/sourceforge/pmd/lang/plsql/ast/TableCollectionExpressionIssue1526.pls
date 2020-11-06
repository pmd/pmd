BEGIN

UPDATE someTable
       SET colName = 'N'
       WHERE id = other_id
        AND id NOT IN (SELECT * FROM TABLE(the_expression));

END;
/
