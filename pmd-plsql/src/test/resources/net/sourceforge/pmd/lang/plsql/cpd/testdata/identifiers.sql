DECLARE
	qty_on_hand	NUMBER(5);
BEGIN
	SELECT quantity INTO qty_on_hand FROM inventory
		WHERE product = 'TENNIS RACKET' -- product gets uppercased
		  AND "Product" = 'tennis racket' -- "Product" gets uppercased and quotes removed
          AND Product = 'tennis racket' -- Product gets uppercased
          AND "Product info" = 'tennis ' -- "Product info" stays the same
          AND "an_id" = 'foo' -- an_id gets uppercased
          AND " not an id" = 'foo' -- " not an id" stays the same
		FOR UPDATE OF quantity;
	COMMIT;
END;
