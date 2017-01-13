# BreakLayout
Это layout (раскладка) для позиционирования потомков (view) в структуру типа тблицы.

![Демонстрация](/images/main.jpg)

### Возможные режимы раскладки

 - [left](/breaklayout/src/main/java/com/turlir/breaklayout/LeftStrategy.java), прижато к левому краю

 - [center](/breaklayout/src/main/java/com/turlir/breaklayout/CenterStrategy.java), по центру см. иллюстрацию

 - [right](/breaklayout/src/main/java/com/turlir/breaklayout/RightStrategy.java), прижато к правому краю

 - [edge](/breaklayout/src/main/java/com/turlir/breaklayout/EdgeStrategy.java), по краям

 - [as is](/breaklayout/src/main/java/com/turlir/breaklayout/AsIsStrategy.java), как есть

Указанные классы являются потомками
[AlignStrategy](/breaklayout/src/main/java/com/turlir/breaklayout/AlignStrategy.java),
который в свою очередь реализует интерфейс
[Strategy](/breaklayout/src/main/java/com/turlir/breaklayout/Strategy.java).
Реализовав этот интерфейс можно создать собственный режим.

Модуль `app` содержит демо-приложение, модуль `breaklayout` - библиотеку.
Демо-приложение не использует appcompat. Библиотека доступна с api 15, но из-за своей простоты
обеспечивает совместимость и с более низкими версиями.