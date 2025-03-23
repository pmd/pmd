DECLARE
	qty_on_hand	NUMBER(5);
BEGIN
	SELECT quantity INTO qty_on_hand FROM inventory
		WHERE product = 'TENNIS RACKET'
		FOR UPDATE OF quantity;
	IF qty_on_hand > 0 THEN  -- check quantity
		UPDATE inventory SET quantity = quantity - 1
			WHERE product = 'TENNIS RACKET';
		INSERT INTO purchase_record
			VALUES ('Tennis racket purchased', SYSDATE);
	ELSE
		INSERT INTO purchase_record
			VALUES ('Out of tennis rackets', SYSDATE);
	END IF;
	COMMIT;
END;
