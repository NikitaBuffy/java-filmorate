# Filmorate Database
Важные аспекты проектирования:
* Каждый столбец таблицы содержит только одно значение.
* Все неключевые атрибуты однозначно определяются ключом.
* Все неключевые атрибуты зависят только от первичного ключа, а не от других неключевых атрибутов.
* База данных должна поддерживает бизнес-логику, предусмотренную в приложении: получение всех фильмов, пользователей, выборка топ-N наиболее популярных фильмов по лайкам и списка общих друзей с другим пользователем.

### ER-диаграмма
![Filmorate ER-diagram](assets/er-diagram.jpg)

Для сохранения информации о жанрах фильмов, лучше вынести их в отдельную таблицу **(film_genre, genre)** и использовать внешний ключ для связи с таблицей **film**. Такая структура таблиц позволит удобно работать с жанрами фильмов и упростит запросы, связанные с выборкой фильмов по жанру.

Таблица **friends** хранит информацию о дружбе между пользователями. Столбцы *user1_id* и *user2_id* будут содержать ID пользователей, между которыми существует дружба. Столбец status будет хранить информацию о статусе дружбы. При этом в таблице не будет дубликатов, например, если пользователь 1 и пользователь 2 уже имеют отношения "друзья", то запись о дружбе между ними будет только одна.

Система рейтингов Американской киноассоциации вынесна в отдельную таблицу **mpa_rating**, т.к. перечень рейтингов ограничен, следует согласовываться с перечнем допустимых значений, которые представлены в этой таблице. 

### Примеры запросов

```sql
--Получение всех пользователей
SELECT *
FROM users;
```

```sql
--Выборка ТОП-5 наиболее популярных фильмов по лайкам
SELECT * 
FROM films f 
LEFT JOIN film_likes fl ON f.film_id = fl.film_id 
GROUP BY f.film_id 
ORDER BY COUNT(fl.user_id) DESC 
LIMIT 10;
```
