-- Так как в H2 не работает ON CONFLICT DO NOTHING, ON DUPLICATE... пришлось составить для каждого такие запросы

MERGE INTO mpa_rating
USING (SELECT CAST('G' AS varchar)) AS g (name)
ON (mpa_rating.name = g.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('G');

MERGE INTO mpa_rating
USING (SELECT CAST('PG' AS varchar)) AS pg (name)
ON (mpa_rating.name = pg.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('PG');

MERGE INTO mpa_rating
USING (SELECT CAST('PG-13' AS varchar)) AS pg13 (name)
ON (mpa_rating.name = pg13.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('PG-13');

MERGE INTO mpa_rating
USING (SELECT CAST('R' AS varchar)) AS r (name)
ON (mpa_rating.name = r.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('R');

MERGE INTO mpa_rating
USING (SELECT CAST('NC-17' AS varchar)) AS nc17 (name)
ON (mpa_rating.name = nc17.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('NC-17');

MERGE INTO genre
USING (SELECT CAST('Комедия' AS varchar)) AS c (name)
ON (genre.name = c.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('Комедия');

MERGE INTO genre
USING (SELECT CAST('Драма' AS varchar)) AS d (name)
ON (genre.name = d.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('Драма');

MERGE INTO genre
USING (SELECT CAST('Мультфильм' AS varchar)) AS h (name)
ON (genre.name = h.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('Мультфильм');

MERGE INTO genre
USING (SELECT CAST('Триллер' AS varchar)) AS a (name)
ON (genre.name = a.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('Триллер');

MERGE INTO genre
USING (SELECT CAST('Документальный' AS varchar)) AS t (name)
ON (genre.name = t.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('Документальный');

MERGE INTO genre
USING (SELECT CAST('Боевик' AS varchar)) AS t (name)
ON (genre.name = t.name)
WHEN NOT MATCHED THEN
    INSERT (name)
    VALUES ('Боевик');