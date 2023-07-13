

CREATE TABLE my_bookshop_Books (
  ID VARCHAR(36) NOT NULL,
  createdAt TIMESTAMP,
  createdBy VARCHAR(255),
  modifiedAt TIMESTAMP,
  modifiedBy VARCHAR(255),
  title VARCHAR(111),
  descr VARCHAR(1111),
  author_ID VARCHAR(36),
  genre_ID INTEGER,
  stock INTEGER,
  price DECIMAL(9, 2),
  currency_code VARCHAR(3),
  rating DECIMAL(2, 1),
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_Authors (
  ID VARCHAR(36) NOT NULL,
  createdAt TIMESTAMP,
  createdBy VARCHAR(255),
  modifiedAt TIMESTAMP,
  modifiedBy VARCHAR(255),
  name VARCHAR(111),
  dateOfBirth DATE,
  dateOfDeath DATE,
  placeOfBirth VARCHAR(255),
  placeOfDeath VARCHAR(255),
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_Genres (
  name VARCHAR(255),
  descr VARCHAR(1000),
  ID INTEGER NOT NULL,
  parent_ID INTEGER,
  PRIMARY KEY(ID)
); 

CREATE TABLE sap_common_Languages (
  name VARCHAR(255),
  descr VARCHAR(1000),
  code VARCHAR(14) NOT NULL,
  PRIMARY KEY(code)
); 

CREATE TABLE sap_common_Currencies (
  name VARCHAR(255),
  descr VARCHAR(1000),
  code VARCHAR(3) NOT NULL,
  symbol VARCHAR(5),
  minorUnit SMALLINT,
  PRIMARY KEY(code)
); 

CREATE TABLE my_bookshop_Books_texts (
  ID_texts VARCHAR(36) NOT NULL,
  locale VARCHAR(14),
  ID VARCHAR(36),
  title VARCHAR(111),
  descr VARCHAR(1111),
  PRIMARY KEY(ID_texts),
  CONSTRAINT my_bookshop_Books_texts_locale UNIQUE (locale, ID)
); 

CREATE TABLE my_bookshop_Genres_texts (
  locale VARCHAR(14) NOT NULL,
  name VARCHAR(255),
  descr VARCHAR(1000),
  ID INTEGER NOT NULL,
  PRIMARY KEY(locale, ID)
); 

CREATE TABLE sap_common_Languages_texts (
  locale VARCHAR(14) NOT NULL,
  name VARCHAR(255),
  descr VARCHAR(1000),
  code VARCHAR(14) NOT NULL,
  PRIMARY KEY(locale, code)
); 

CREATE TABLE sap_common_Currencies_texts (
  locale VARCHAR(14) NOT NULL,
  name VARCHAR(255),
  descr VARCHAR(1000),
  code VARCHAR(3) NOT NULL,
  PRIMARY KEY(locale, code)
); 

CREATE TABLE DRAFT_DraftAdministrativeData (
  DraftUUID VARCHAR(36) NOT NULL,
  CreationDateTime TIMESTAMP,
  CreatedByUser VARCHAR(256),
  DraftIsCreatedByMe BOOLEAN,
  LastChangeDateTime TIMESTAMP,
  LastChangedByUser VARCHAR(256),
  InProcessByUser VARCHAR(256),
  DraftIsProcessedByMe BOOLEAN,
  PRIMARY KEY(DraftUUID)
); 

CREATE TABLE AdminService_Books_drafts (
  ID VARCHAR(36) NOT NULL,
  createdAt TIMESTAMP NULL,
  createdBy VARCHAR(255) NULL,
  modifiedAt TIMESTAMP NULL,
  modifiedBy VARCHAR(255) NULL,
  title VARCHAR(111) NULL,
  descr VARCHAR(1111) NULL,
  author_ID VARCHAR(36) NULL,
  genre_ID INTEGER NULL,
  stock INTEGER NULL,
  price DECIMAL(9, 2) NULL,
  currency_code VARCHAR(3) NULL,
  rating DECIMAL(2, 1) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID VARCHAR(36) NOT NULL,
  PRIMARY KEY(ID)
); 

CREATE TABLE AdminService_Books_texts_drafts (
  ID_texts VARCHAR(36) NOT NULL,
  locale VARCHAR(14) NULL,
  ID VARCHAR(36) NULL,
  title VARCHAR(111) NULL,
  descr VARCHAR(1111) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID VARCHAR(36) NOT NULL,
  PRIMARY KEY(ID_texts)
); 

CREATE VIEW AdminService_Books AS SELECT
  Books_0.ID,
  Books_0.createdAt,
  Books_0.createdBy,
  Books_0.modifiedAt,
  Books_0.modifiedBy,
  Books_0.title,
  Books_0.descr,
  Books_0.author_ID,
  Books_0.genre_ID,
  Books_0.stock,
  Books_0.price,
  Books_0.currency_code,
  Books_0.rating
FROM my_bookshop_Books AS Books_0; 

CREATE VIEW AdminService_Authors AS SELECT
  Authors_0.ID,
  Authors_0.createdAt,
  Authors_0.createdBy,
  Authors_0.modifiedAt,
  Authors_0.modifiedBy,
  Authors_0.name,
  Authors_0.dateOfBirth,
  Authors_0.dateOfDeath,
  Authors_0.placeOfBirth,
  Authors_0.placeOfDeath
FROM my_bookshop_Authors AS Authors_0; 

CREATE VIEW AdminService_Languages AS SELECT
  CommonLanguages_0.name,
  CommonLanguages_0.descr,
  CommonLanguages_0.code
FROM sap_common_Languages AS CommonLanguages_0; 

CREATE VIEW CatalogService_Books AS SELECT
  Books_0.ID,
  Books_0.createdAt,
  Books_0.modifiedAt,
  Books_0.title,
  Books_0.descr,
  Books_0.author_ID,
  Books_0.genre_ID,
  Books_0.stock,
  Books_0.price,
  Books_0.currency_code,
  Books_0.rating
FROM my_bookshop_Books AS Books_0; 

CREATE VIEW CatalogService_Authors AS SELECT
  Authors_0.ID,
  Authors_0.createdAt,
  Authors_0.createdBy,
  Authors_0.modifiedAt,
  Authors_0.modifiedBy,
  Authors_0.name,
  Authors_0.dateOfBirth,
  Authors_0.dateOfDeath,
  Authors_0.placeOfBirth,
  Authors_0.placeOfDeath
FROM my_bookshop_Authors AS Authors_0; 

CREATE VIEW AdminService_Genres AS SELECT
  Genres_0.name,
  Genres_0.descr,
  Genres_0.ID,
  Genres_0.parent_ID
FROM my_bookshop_Genres AS Genres_0; 

CREATE VIEW AdminService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol,
  Currencies_0.minorUnit
FROM sap_common_Currencies AS Currencies_0; 

CREATE VIEW AdminService_Books_texts AS SELECT
  texts_0.ID_texts,
  texts_0.locale,
  texts_0.ID,
  texts_0.title,
  texts_0.descr
FROM my_bookshop_Books_texts AS texts_0; 

CREATE VIEW AdminService_Languages_texts AS SELECT
  texts_0.locale,
  texts_0.name,
  texts_0.descr,
  texts_0.code
FROM sap_common_Languages_texts AS texts_0; 

CREATE VIEW CatalogService_Genres AS SELECT
  Genres_0.name,
  Genres_0.descr,
  Genres_0.ID,
  Genres_0.parent_ID
FROM my_bookshop_Genres AS Genres_0; 

CREATE VIEW CatalogService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol,
  Currencies_0.minorUnit
FROM sap_common_Currencies AS Currencies_0; 

CREATE VIEW CatalogService_Books_texts AS SELECT
  texts_0.ID_texts,
  texts_0.locale,
  texts_0.ID,
  texts_0.title,
  texts_0.descr
FROM my_bookshop_Books_texts AS texts_0; 

CREATE VIEW AdminService_Genres_texts AS SELECT
  texts_0.locale,
  texts_0.name,
  texts_0.descr,
  texts_0.ID
FROM my_bookshop_Genres_texts AS texts_0; 

CREATE VIEW AdminService_Currencies_texts AS SELECT
  texts_0.locale,
  texts_0.name,
  texts_0.descr,
  texts_0.code
FROM sap_common_Currencies_texts AS texts_0; 

CREATE VIEW CatalogService_Genres_texts AS SELECT
  texts_0.locale,
  texts_0.name,
  texts_0.descr,
  texts_0.ID
FROM my_bookshop_Genres_texts AS texts_0; 

CREATE VIEW CatalogService_Currencies_texts AS SELECT
  texts_0.locale,
  texts_0.name,
  texts_0.descr,
  texts_0.code
FROM sap_common_Currencies_texts AS texts_0; 

CREATE VIEW localized_my_bookshop_Books AS SELECT
  L_0.ID,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedAt,
  L_0.modifiedBy,
  coalesce(localized_1.title, L_0.title) AS title,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.author_ID,
  L_0.genre_ID,
  L_0.stock,
  L_0.price,
  L_0.currency_code,
  L_0.rating
FROM (my_bookshop_Books AS L_0 LEFT JOIN my_bookshop_Books_texts AS localized_1 ON localized_1.ID = L_0.ID AND localized_1.locale = current_setting('cap.locale')); 

CREATE VIEW localized_my_bookshop_Genres AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.ID,
  L_0.parent_ID
FROM (my_bookshop_Genres AS L_0 LEFT JOIN my_bookshop_Genres_texts AS localized_1 ON localized_1.ID = L_0.ID AND localized_1.locale = current_setting('cap.locale')); 

CREATE VIEW localized_sap_common_Languages AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.code
FROM (sap_common_Languages AS L_0 LEFT JOIN sap_common_Languages_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = current_setting('cap.locale')); 

CREATE VIEW localized_sap_common_Currencies AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.code,
  L_0.symbol,
  L_0.minorUnit
FROM (sap_common_Currencies AS L_0 LEFT JOIN sap_common_Currencies_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = current_setting('cap.locale')); 

CREATE VIEW localized_my_bookshop_Authors AS SELECT
  L.ID,
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.name,
  L.dateOfBirth,
  L.dateOfDeath,
  L.placeOfBirth,
  L.placeOfDeath
FROM my_bookshop_Authors AS L; 

CREATE VIEW AdminService_DraftAdministrativeData AS SELECT
  DraftAdministrativeData.DraftUUID,
  DraftAdministrativeData.CreationDateTime,
  DraftAdministrativeData.CreatedByUser,
  DraftAdministrativeData.DraftIsCreatedByMe,
  DraftAdministrativeData.LastChangeDateTime,
  DraftAdministrativeData.LastChangedByUser,
  DraftAdministrativeData.InProcessByUser,
  DraftAdministrativeData.DraftIsProcessedByMe
FROM DRAFT_DraftAdministrativeData AS DraftAdministrativeData; 

CREATE VIEW localized_AdminService_Books AS SELECT
  Books_0.ID,
  Books_0.createdAt,
  Books_0.createdBy,
  Books_0.modifiedAt,
  Books_0.modifiedBy,
  Books_0.title,
  Books_0.descr,
  Books_0.author_ID,
  Books_0.genre_ID,
  Books_0.stock,
  Books_0.price,
  Books_0.currency_code,
  Books_0.rating
FROM localized_my_bookshop_Books AS Books_0; 

CREATE VIEW localized_AdminService_Languages AS SELECT
  CommonLanguages_0.name,
  CommonLanguages_0.descr,
  CommonLanguages_0.code
FROM localized_sap_common_Languages AS CommonLanguages_0; 

CREATE VIEW localized_CatalogService_Books AS SELECT
  Books_0.ID,
  Books_0.createdAt,
  Books_0.modifiedAt,
  Books_0.title,
  Books_0.descr,
  Books_0.author_ID,
  Books_0.genre_ID,
  Books_0.stock,
  Books_0.price,
  Books_0.currency_code,
  Books_0.rating
FROM localized_my_bookshop_Books AS Books_0; 

CREATE VIEW localized_AdminService_Genres AS SELECT
  Genres_0.name,
  Genres_0.descr,
  Genres_0.ID,
  Genres_0.parent_ID
FROM localized_my_bookshop_Genres AS Genres_0; 

CREATE VIEW localized_AdminService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol,
  Currencies_0.minorUnit
FROM localized_sap_common_Currencies AS Currencies_0; 

CREATE VIEW localized_CatalogService_Genres AS SELECT
  Genres_0.name,
  Genres_0.descr,
  Genres_0.ID,
  Genres_0.parent_ID
FROM localized_my_bookshop_Genres AS Genres_0; 

CREATE VIEW localized_CatalogService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol,
  Currencies_0.minorUnit
FROM localized_sap_common_Currencies AS Currencies_0; 

CREATE VIEW localized_AdminService_Authors AS SELECT
  Authors_0.ID,
  Authors_0.createdAt,
  Authors_0.createdBy,
  Authors_0.modifiedAt,
  Authors_0.modifiedBy,
  Authors_0.name,
  Authors_0.dateOfBirth,
  Authors_0.dateOfDeath,
  Authors_0.placeOfBirth,
  Authors_0.placeOfDeath
FROM localized_my_bookshop_Authors AS Authors_0; 

CREATE VIEW localized_CatalogService_Authors AS SELECT
  Authors_0.ID,
  Authors_0.createdAt,
  Authors_0.createdBy,
  Authors_0.modifiedAt,
  Authors_0.modifiedBy,
  Authors_0.name,
  Authors_0.dateOfBirth,
  Authors_0.dateOfDeath,
  Authors_0.placeOfBirth,
  Authors_0.placeOfDeath
FROM localized_my_bookshop_Authors AS Authors_0; 

