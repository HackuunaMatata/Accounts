CREATE DATABASE Accaunts CHARACTER SET utf8 COLLATE utf8_general_ci;
USE Accaunts;


-- Создание таблицы "Вопрос" --
/* ttype: 0 - IntTable
 *        1 - StrTable
 *        2 - DataTable
 *        3 - ListsTable
 *
 * visible, cchange: 0 - false
 *                   1 - true
 *
 * formattype: 0 - без формата
 *             1 - только буквы
 *             2 - буквы и цифры
 *             3 - email
 *             4 - мобильный телефон (текст)
 *             5 - ключ (20 цифр)
 *             6 - дата и время
 *             7 - дата
 *             8 - время
 *             9 - только цифры
 */
CREATE TABLE Questions (
	id      INT NOT NULL AUTO_INCREMENT,
	name    VARCHAR(255) NOT NULL DEFAULT 'Empty questions',
	ttype   INT NOT NULL DEFAULT 0,
	visible INT NOT NULL DEFAULT 1,
	cchange INT NOT NULL DEFAULT 1,
	formattype INT NOT NULL DEFAULT 0,

	PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=UTF8 ;

INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Название проекта',3,1,0,0);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Фамилия',1,1,0,1);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Имя',1,1,0,1);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('E-mail',1,1,0,3);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Должность',3,1,0,0);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Телефон',1,1,0,4);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Номер ключа',1,1,0,5);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Аккаунт внутренний',1,1,1,2);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Аккаунт внешний',1,1,1,2);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Номер внешнего ключа',1,1,1,5);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Дата последней проверки подписи',2,1,1,6);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Имя внутреннего компьютера',1,1,1,2);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Имя внешнего компьютера',1,1,1,2);
INSERT INTO `Questions`(name,ttype,visible,cchange,formattype)
   VALUES ('Состояние внешней учётной записи',3,1,1,0);

-- Создание таблицы "Пользователь" --
CREATE TABLE Users (
	id      INT NOT NULL AUTO_INCREMENT,
	name    VARCHAR(255) NOT NULL,
	surname VARCHAR(255) NOT NULL,
	dateOfLastChange TIMESTAMP NOT NULL,

	PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=UTF8 ;

INSERT INTO `Users`(name,surname,dateOfLastChange)
   VALUES ('Антон','Жалдак', NOW());
INSERT INTO `Users`(name,surname,dateOfLastChange)
   VALUES ('Мария','Бутакова', NOW());
INSERT INTO `Users`(name,surname,dateOfLastChange)
   VALUES ('Саша','Панкратова', NOW());
INSERT INTO `Users`(name,surname,dateOfLastChange)
   VALUES ('Антон','Жалдак', NOW());

-- Создание таблицы "Даты" --
CREATE TABLE DateTable (
	id_user     INT NOT NULL,
	id_question INT NOT NULL,
	value       TIMESTAMP,

	PRIMARY KEY (id_user, id_question)
) ENGINE=InnoDB CHARACTER SET=UTF8 ;

ALTER TABLE DateTable ADD FOREIGN KEY (id_user) REFERENCES Users(id);
ALTER TABLE DateTable ADD FOREIGN KEY (id_question) REFERENCES Questions(id);

-- Создание таблицы "Строки" --
CREATE TABLE StrTable (
	id_user     INT NOT NULL,
	id_question INT NOT NULL,
	value       VARCHAR(1024),

	PRIMARY KEY (id_user, id_question)
) ENGINE=InnoDB CHARACTER SET=UTF8 ;

ALTER TABLE StrTable ADD FOREIGN KEY (id_user) REFERENCES Users(id);
ALTER TABLE StrTable ADD FOREIGN KEY (id_question) REFERENCES Questions(id);

-- Создание таблицы "Числа" --
CREATE TABLE IntTable (
	id_user     INT NOT NULL,
	id_question INT NOT NULL,
	value       INT,

	PRIMARY KEY (id_user, id_question),
	UNIQUE (id_user, id_question, value)
) ENGINE=InnoDB CHARACTER SET=UTF8 ;

ALTER TABLE IntTable ADD FOREIGN KEY (id_user) REFERENCES Users(id);
ALTER TABLE IntTable ADD FOREIGN KEY (id_question) REFERENCES Questions(id);

-- Создание таблицы "Списки" --
CREATE TABLE ListsTable (
	id_question INT NOT NULL,
	value       VARCHAR(512),

	PRIMARY KEY (id_question,value(200))
) ENGINE=InnoDB CHARACTER SET=UTF8 ;

ALTER TABLE ListsTable ADD FOREIGN KEY (id_question) REFERENCES Questions(id);

INSERT INTO `ListsTable` VALUES (1, 'Супер проект');
INSERT INTO `ListsTable` VALUES (1, 'Супер важный проект');
INSERT INTO `ListsTable` VALUES (1, 'Проект, который пилим уже полгода');
INSERT INTO `ListsTable` VALUES (5, 'Директор');
INSERT INTO `ListsTable` VALUES (5, 'Менеджер');
INSERT INTO `ListsTable` VALUES (5, 'Программист');
INSERT INTO `ListsTable` VALUES (14, 'Active');
INSERT INTO `ListsTable` VALUES (14, 'Ordered');
INSERT INTO `ListsTable` VALUES (14, 'Terminated');
