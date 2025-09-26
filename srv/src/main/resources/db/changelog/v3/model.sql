
DROP VIEW localized_CatalogService_Authors;
DROP VIEW localized_AdminService_Authors;
DROP VIEW localized_my_bookshop_Authors;
DROP VIEW localized_CatalogService_Currencies;
DROP VIEW localized_CatalogService_Genres;
DROP VIEW localized_AdminService_Currencies;
DROP VIEW localized_AdminService_Genres;
DROP VIEW localized_CatalogService_Books;
DROP VIEW localized_AdminService_Languages;
DROP VIEW localized_AdminService_Books;
DROP VIEW AdminService_DraftAdministrativeData;
DROP VIEW localized_sap_common_Currencies;
DROP VIEW localized_sap_common_Languages;
DROP VIEW localized_my_bookshop_Genres;
DROP VIEW localized_my_bookshop_Books;
DROP VIEW CatalogService_Currencies_texts;
DROP VIEW CatalogService_Genres_texts;
DROP VIEW AdminService_Currencies_texts;
DROP VIEW AdminService_Genres_texts;
DROP VIEW CatalogService_Books_texts;
DROP VIEW CatalogService_Currencies;
DROP VIEW CatalogService_Genres;
DROP VIEW AdminService_Languages_texts;
DROP VIEW AdminService_Books_texts;
DROP VIEW AdminService_Currencies;
DROP VIEW AdminService_Genres;
DROP VIEW CatalogService_Authors;
DROP VIEW CatalogService_Books;
DROP VIEW AdminService_Languages;
DROP VIEW AdminService_Authors;
DROP VIEW AdminService_Books;

CREATE TABLE cds_outbox_Messages (
  ID VARCHAR(36) NOT NULL,
  timestamp TIMESTAMP,
  target VARCHAR(255),
  msg TEXT,
  attempts INTEGER DEFAULT 0,
  partition INTEGER DEFAULT 0,
  lastError TEXT,
  lastAttemptTimestamp TIMESTAMP,
  status VARCHAR(23),
  PRIMARY KEY(ID)
);


ALTER TABLE DRAFT_DraftAdministrativeData ADD CreatedByUserDescription VARCHAR(256);


ALTER TABLE DRAFT_DraftAdministrativeData ADD LastChangedByUserDescription VARCHAR(256);


ALTER TABLE DRAFT_DraftAdministrativeData ADD InProcessByUserDescription VARCHAR(256);


ALTER TABLE DRAFT_DraftAdministrativeData ADD DraftMessages TEXT;


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
  Books_0.rating,
  Books_0.isbn
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
  Books_0.rating,
  Books_0.isbn
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
  L_0.rating,
  L_0.isbn
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


CREATE VIEW AdminService_DraftAdministrativeData AS SELECT
  DraftAdministrativeData.DraftUUID,
  DraftAdministrativeData.CreationDateTime,
  DraftAdministrativeData.CreatedByUser,
  DraftAdministrativeData.CreatedByUserDescription,
  DraftAdministrativeData.DraftIsCreatedByMe,
  DraftAdministrativeData.LastChangeDateTime,
  DraftAdministrativeData.LastChangedByUser,
  DraftAdministrativeData.LastChangedByUserDescription,
  DraftAdministrativeData.InProcessByUser,
  DraftAdministrativeData.InProcessByUserDescription,
  DraftAdministrativeData.DraftIsProcessedByMe,
  DraftAdministrativeData.DraftMessages
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
  Books_0.rating,
  Books_0.isbn
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
  Books_0.rating,
  Books_0.isbn
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

