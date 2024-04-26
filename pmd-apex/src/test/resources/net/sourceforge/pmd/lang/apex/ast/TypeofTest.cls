public class TypeofTest {
	static void test() {
		Case c = [
			SELECT
				Id,
				Subject,
				Description,
				TYPEOF Owner
					WHEN Group THEN Name
					WHEN User THEN Name
				END,
				(
					SELECT
						Id,
						Subject,
						Description,
						TYPEOF Owner
							WHEN Group THEN Name
							WHEN User THEN Name
						END
					FROM Cases
				)
			FROM Case
		];
	}
}
