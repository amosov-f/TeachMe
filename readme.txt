Hella!

alias mysql=/usr/local/mysql/bin/mysql

Для коммита жмешь пкм на измененном файле -> git -> commit file
Затем нужно описать свой коммит
Наводим на commit
Делаем commit and push
Нажимаем commit -> push

Для вытягивания кода из репозитория нужно сделать pull
Для этого:
пкм на любом файле -> git -> repository -> pull -> pull

Любой html запрос строится так
localhost:8083/запрос?аргумент1=значение1&аргумент2=значение2...


Зайти в базу данных из консоли
mysql -u teachme -p
пароль teachmepass

Потом пишем
use teachme;
Для просмотра таблиц
select * from tag;

Описания таблиц находятся в src/main/sql/main.sql

Таблицы вида a_b хранят связи
Таблицы вида a хранят сами объекты
