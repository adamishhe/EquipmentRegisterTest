# Реестр техники

- Язык программирования: Java 17;
- Frameworks: Spring Boot Starter, Spring Web, Spring JPA, Spring Hibernate;
- База данных: Postgres SQL;
- Библиотека для генерации документации: springdoc-openapi v2.6.0

## Инструкция по запуску
Нужно клонировать репозиторий:  
```sh
git clone https://github.com/adamishhe/EquipmentRegisterTest.git
```

Далее нужно перейти в директорию проекта и собрать его через Maven:
```sh
mvn clean install
```

Осталось только запустить приложение и перейти на страницу с документацией:
```sh
mvn spring-boot:run
```
http://localhost:8080/swagger-ui/index.html# 
