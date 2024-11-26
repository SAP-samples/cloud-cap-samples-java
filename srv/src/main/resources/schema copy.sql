
DROP VIEW IF EXISTS localized_AdminService_Orders_changes;
DROP VIEW IF EXISTS localized_AdminService_Orders;
DROP VIEW IF EXISTS localized_ReviewService_Authors;
DROP VIEW IF EXISTS localized_ReviewService_Reviews;
DROP VIEW IF EXISTS localized_CatalogService_Reviews;
DROP VIEW IF EXISTS localized_CatalogService_Authors;
DROP VIEW IF EXISTS localized_AdminService_OrderItems;
DROP VIEW IF EXISTS localized_AdminService_Authors;
DROP VIEW IF EXISTS localized_ReviewService_Currencies;
DROP VIEW IF EXISTS localized_ReviewService_Genres;
DROP VIEW IF EXISTS localized_CatalogService_Currencies;
DROP VIEW IF EXISTS localized_CatalogService_Genres;
DROP VIEW IF EXISTS localized_AdminService_Currencies;
DROP VIEW IF EXISTS localized_AdminService_Genres;
DROP VIEW IF EXISTS localized_ReviewService_Books;
DROP VIEW IF EXISTS localized_CatalogService_Books;
DROP VIEW IF EXISTS localized_AdminService_Languages;
DROP VIEW IF EXISTS localized_AdminService_Books;
DROP VIEW IF EXISTS NotesService_Addresses;
DROP VIEW IF EXISTS ReviewService_DraftAdministrativeData;
DROP VIEW IF EXISTS NotesService_DraftAdministrativeData;
DROP VIEW IF EXISTS AdminService_DraftAdministrativeData;
DROP VIEW IF EXISTS localized_my_bookshop_Orders_changes;
DROP VIEW IF EXISTS localized_my_bookshop_Reviews;
DROP VIEW IF EXISTS localized_my_bookshop_OrderItems;
DROP VIEW IF EXISTS localized_my_bookshop_Orders;
DROP VIEW IF EXISTS localized_my_bookshop_Authors;
DROP VIEW IF EXISTS localized_sap_common_Currencies;
DROP VIEW IF EXISTS localized_sap_common_Languages;
DROP VIEW IF EXISTS localized_my_bookshop_Genres;
DROP VIEW IF EXISTS localized_my_bookshop_Books;
DROP VIEW IF EXISTS ReviewService_Currencies_texts;
DROP VIEW IF EXISTS ReviewService_Genres_texts;
DROP VIEW IF EXISTS CatalogService_Currencies_texts;
DROP VIEW IF EXISTS CatalogService_Genres_texts;
DROP VIEW IF EXISTS AdminService_Changes;
DROP VIEW IF EXISTS AdminService_Currencies_texts;
DROP VIEW IF EXISTS AdminService_Genres_texts;
DROP VIEW IF EXISTS ReviewService_Books_texts;
DROP VIEW IF EXISTS ReviewService_Currencies;
DROP VIEW IF EXISTS ReviewService_Genres;
DROP VIEW IF EXISTS CatalogService_Books_texts;
DROP VIEW IF EXISTS CatalogService_Currencies;
DROP VIEW IF EXISTS CatalogService_Genres;
DROP VIEW IF EXISTS AdminService_Languages_texts;
DROP VIEW IF EXISTS AdminService_Addresses;
DROP VIEW IF EXISTS AdminService_Orders_changes;
DROP VIEW IF EXISTS AdminService_OrderItems;
DROP VIEW IF EXISTS AdminService_Books_texts;
DROP VIEW IF EXISTS AdminService_Currencies;
DROP VIEW IF EXISTS AdminService_Genres;
DROP VIEW IF EXISTS ReviewService_Authors;
DROP VIEW IF EXISTS ReviewService_Books;
DROP VIEW IF EXISTS ReviewService_Reviews;
DROP VIEW IF EXISTS NotesService_Notes;
DROP VIEW IF EXISTS my_bookshop_NoteableAddresses;
DROP VIEW IF EXISTS CatalogService_Reviews;
DROP VIEW IF EXISTS CatalogService_Authors;
DROP VIEW IF EXISTS CatalogService_Books;
DROP VIEW IF EXISTS AdminService_Languages;
DROP VIEW IF EXISTS AdminService_Orders;
DROP VIEW IF EXISTS AdminService_Authors;
DROP VIEW IF EXISTS AdminService_Books;
DROP TABLE IF EXISTS ReviewService_Reviews_drafts;
DROP TABLE IF EXISTS NotesService_Notes_drafts;
DROP TABLE IF EXISTS AdminService_OrderItems_drafts;
DROP TABLE IF EXISTS AdminService_Orders_drafts;
DROP TABLE IF EXISTS AdminService_Books_texts_drafts;
DROP TABLE IF EXISTS AdminService_Books_drafts;
DROP TABLE IF EXISTS DRAFT_DraftAdministrativeData;
DROP TABLE IF EXISTS sap_common_Currencies_texts;
DROP TABLE IF EXISTS sap_common_Languages_texts;
DROP TABLE IF EXISTS my_bookshop_Orders_changes;
DROP TABLE IF EXISTS my_bookshop_Genres_texts;
DROP TABLE IF EXISTS my_bookshop_Books_texts;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_SupplierWithHoldingTax;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_SupplierPurchasingOrg;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_SupplierPartnerFunc;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_SupplierDunning;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_SupplierCompany;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_Supplier;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_CustSalesPartnerFunc;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_CustomerWithHoldingTax;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_CustomerSalesAreaTax;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_CustomerSalesArea;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_CustomerDunning;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_CustomerCompany;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_Customer;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BusinessPartnerTaxNumber;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BusinessPartnerRole;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BusinessPartnerContact;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BusinessPartnerBank;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BusinessPartnerAddress;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BusinessPartner;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BuPaIndustry;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BuPaIdentification;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BuPaAddressUsage;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BPContactToFuncAndDept;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_BPContactToAddress;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_AddressPhoneNumber;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_AddressHomePageURL;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_AddressFaxNumber;
DROP TABLE IF EXISTS API_BUSINESS_PARTNER_A_AddressEmailAddress;
DROP TABLE IF EXISTS sap_changelog_Changes;
DROP TABLE IF EXISTS sap_common_Currencies;
DROP TABLE IF EXISTS sap_common_Languages;
DROP TABLE IF EXISTS cds_outbox_Messages;
DROP TABLE IF EXISTS my_bookshop_Notes;
DROP TABLE IF EXISTS my_bookshop_Reviews;
DROP TABLE IF EXISTS my_bookshop_OrderItems;
DROP TABLE IF EXISTS my_bookshop_Orders;
DROP TABLE IF EXISTS my_bookshop_Genres;
DROP TABLE IF EXISTS my_bookshop_Authors;
DROP TABLE IF EXISTS my_bookshop_Books;
DROP TABLE IF EXISTS cds_xt_Extensions;
DROP TABLE IF EXISTS my_bookshop_Addresses;
DROP TABLE IF EXISTS AdminService_Upload;

CREATE TABLE AdminService_Upload (
  csv BINARY LARGE OBJECT
); 

CREATE TABLE my_bookshop_Addresses (
  ID NVARCHAR(10) NOT NULL,
  businessPartner NVARCHAR(10) NOT NULL,
  country NVARCHAR(3),
  city NVARCHAR(40),
  postalCode NVARCHAR(10),
  street NVARCHAR(60),
  houseNumber NVARCHAR(10),
  tombstone BOOLEAN,
  PRIMARY KEY(ID, businessPartner)
); 

CREATE TABLE cds_xt_Extensions (
  ID NVARCHAR(36) NOT NULL,
  tag NVARCHAR(255),
  csn NCLOB,
  i18n NCLOB,
  sources BINARY LARGE OBJECT,
  activated NVARCHAR(255),
  timestamp TIMESTAMP(7),
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_Books (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  title NVARCHAR(111),
  descr NVARCHAR(1111),
  author_ID NVARCHAR(36),
  genre_ID INTEGER,
  stock INTEGER,
  price DECIMAL(9, 2),
  currency_code NVARCHAR(3),
  rating DECIMAL(2, 1),
  isReviewable BOOLEAN NOT NULL DEFAULT TRUE,
  isbn NVARCHAR(40),
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_Authors (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  name NVARCHAR(111),
  dateOfBirth DATE,
  dateOfDeath DATE,
  placeOfBirth NVARCHAR(255),
  placeOfDeath NVARCHAR(255),
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_Genres (
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  ID INTEGER NOT NULL,
  parent_ID INTEGER,
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_Orders (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  OrderNo NVARCHAR(255),
  buyer NVARCHAR(255),
  total DECIMAL(9, 2),
  currency_code NVARCHAR(3),
  shippingAddress_ID NVARCHAR(10),
  shippingAddress_businessPartner NVARCHAR(10),
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_OrderItems (
  ID NVARCHAR(36) NOT NULL,
  parent_ID NVARCHAR(36),
  book_ID NVARCHAR(36),
  quantity INTEGER,
  amount DECIMAL(9, 2),
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_Reviews (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  book_ID NVARCHAR(36),
  rating INTEGER,
  title NVARCHAR(111),
  text NVARCHAR(1111),
  PRIMARY KEY(ID)
); 

CREATE TABLE my_bookshop_Notes (
  ID NVARCHAR(36) NOT NULL,
  note NVARCHAR(255),
  address_ID NVARCHAR(10),
  address_businessPartner NVARCHAR(10),
  PRIMARY KEY(ID)
); 

CREATE TABLE cds_outbox_Messages (
  ID NVARCHAR(36) NOT NULL,
  timestamp TIMESTAMP(7),
  target NVARCHAR(255),
  msg NCLOB,
  attempts INTEGER DEFAULT 0,
  "PARTITION" INTEGER DEFAULT 0,
  lastError NCLOB,
  lastAttemptTimestamp TIMESTAMP(7),
  PRIMARY KEY(ID)
); 

CREATE TABLE sap_common_Languages (
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(14) NOT NULL,
  PRIMARY KEY(code)
); 

CREATE TABLE sap_common_Currencies (
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(3) NOT NULL,
  symbol NVARCHAR(5),
  minorUnit SMALLINT,
  PRIMARY KEY(code)
); 

CREATE TABLE sap_changelog_Changes (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  changeLogID NVARCHAR(36),
  rootEntity NVARCHAR(255),
  rootIdentifier NVARCHAR(255),
  attribute NVARCHAR(255),
  valueChangedFrom NVARCHAR(5000),
  valueChangedTo NVARCHAR(5000),
  valueDataType NVARCHAR(255),
  targetIdentifier NVARCHAR(255),
  targetEntity NVARCHAR(255),
  path NVARCHAR(5000),
  modification NVARCHAR(255),
  PRIMARY KEY(ID)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_AddressEmailAddress (
  AddressID NVARCHAR(10) NOT NULL,
  Person NVARCHAR(10) NOT NULL,
  OrdinalNumber NVARCHAR(3) NOT NULL,
  IsDefaultEmailAddress BOOLEAN,
  EmailAddress NVARCHAR(241),
  SearchEmailAddress NVARCHAR(20),
  AddressCommunicationRemarkText NVARCHAR(50),
  PRIMARY KEY(AddressID, Person, OrdinalNumber)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_AddressFaxNumber (
  AddressID NVARCHAR(10) NOT NULL,
  Person NVARCHAR(10) NOT NULL,
  OrdinalNumber NVARCHAR(3) NOT NULL,
  IsDefaultFaxNumber BOOLEAN,
  FaxCountry NVARCHAR(3),
  FaxNumber NVARCHAR(30),
  FaxNumberExtension NVARCHAR(10),
  InternationalFaxNumber NVARCHAR(30),
  AddressCommunicationRemarkText NVARCHAR(50),
  PRIMARY KEY(AddressID, Person, OrdinalNumber)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_AddressHomePageURL (
  AddressID NVARCHAR(10) NOT NULL,
  Person NVARCHAR(10) NOT NULL,
  OrdinalNumber NVARCHAR(3) NOT NULL,
  ValidityStartDate DATE NOT NULL,
  IsDefaultURLAddress BOOLEAN NOT NULL,
  SearchURLAddress NVARCHAR(50),
  AddressCommunicationRemarkText NVARCHAR(50),
  URLFieldLength INTEGER,
  WebsiteURL NVARCHAR(2048),
  PRIMARY KEY(AddressID, Person, OrdinalNumber, ValidityStartDate, IsDefaultURLAddress)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_AddressPhoneNumber (
  AddressID NVARCHAR(10) NOT NULL,
  Person NVARCHAR(10) NOT NULL,
  OrdinalNumber NVARCHAR(3) NOT NULL,
  DestinationLocationCountry NVARCHAR(3),
  IsDefaultPhoneNumber BOOLEAN,
  PhoneNumber NVARCHAR(30),
  PhoneNumberExtension NVARCHAR(10),
  InternationalPhoneNumber NVARCHAR(30),
  PhoneNumberType NVARCHAR(1),
  AddressCommunicationRemarkText NVARCHAR(50),
  PRIMARY KEY(AddressID, Person, OrdinalNumber)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BPContactToAddress (
  RelationshipNumber NVARCHAR(12) NOT NULL,
  BusinessPartnerCompany NVARCHAR(10) NOT NULL,
  BusinessPartnerPerson NVARCHAR(10) NOT NULL,
  ValidityEndDate DATE NOT NULL,
  AddressID NVARCHAR(10) NOT NULL,
  AddressNumber NVARCHAR(10),
  AdditionalStreetPrefixName NVARCHAR(40),
  AdditionalStreetSuffixName NVARCHAR(40),
  AddressTimeZone NVARCHAR(6),
  CareOfName NVARCHAR(40),
  CityCode NVARCHAR(12),
  CityName NVARCHAR(40),
  CompanyPostalCode NVARCHAR(10),
  Country NVARCHAR(3),
  County NVARCHAR(40),
  DeliveryServiceNumber NVARCHAR(10),
  DeliveryServiceTypeCode NVARCHAR(4),
  District NVARCHAR(40),
  FormOfAddress NVARCHAR(4),
  FullName NVARCHAR(80),
  HomeCityName NVARCHAR(40),
  HouseNumber NVARCHAR(10),
  HouseNumberSupplementText NVARCHAR(10),
  Language NVARCHAR(2),
  POBox NVARCHAR(10),
  POBoxDeviatingCityName NVARCHAR(40),
  POBoxDeviatingCountry NVARCHAR(3),
  POBoxDeviatingRegion NVARCHAR(3),
  POBoxIsWithoutNumber BOOLEAN,
  POBoxLobbyName NVARCHAR(40),
  POBoxPostalCode NVARCHAR(10),
  Person NVARCHAR(10),
  PostalCode NVARCHAR(10),
  PrfrdCommMediumType NVARCHAR(3),
  Region NVARCHAR(3),
  StreetName NVARCHAR(60),
  StreetPrefixName NVARCHAR(40),
  StreetSuffixName NVARCHAR(40),
  TaxJurisdiction NVARCHAR(15),
  TransportZone NVARCHAR(10),
  PRIMARY KEY(RelationshipNumber, BusinessPartnerCompany, BusinessPartnerPerson, ValidityEndDate, AddressID)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BPContactToFuncAndDept (
  RelationshipNumber NVARCHAR(12) NOT NULL,
  BusinessPartnerCompany NVARCHAR(10) NOT NULL,
  BusinessPartnerPerson NVARCHAR(10) NOT NULL,
  ValidityEndDate DATE NOT NULL,
  ContactPersonFunction NVARCHAR(4),
  ContactPersonDepartment NVARCHAR(4),
  PhoneNumber NVARCHAR(30),
  PhoneNumberExtension NVARCHAR(10),
  FaxNumber NVARCHAR(30),
  FaxNumberExtension NVARCHAR(10),
  EmailAddress NVARCHAR(241),
  RelationshipCategory NVARCHAR(6),
  PRIMARY KEY(RelationshipNumber, BusinessPartnerCompany, BusinessPartnerPerson, ValidityEndDate)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BuPaAddressUsage (
  BusinessPartner NVARCHAR(10) NOT NULL,
  ValidityEndDate TIMESTAMP(0) NOT NULL,
  AddressUsage NVARCHAR(10) NOT NULL,
  AddressID NVARCHAR(10) NOT NULL,
  ValidityStartDate TIMESTAMP(0),
  StandardUsage BOOLEAN,
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(BusinessPartner, ValidityEndDate, AddressUsage, AddressID)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BuPaIdentification (
  BusinessPartner NVARCHAR(10) NOT NULL,
  BPIdentificationType NVARCHAR(6) NOT NULL,
  BPIdentificationNumber NVARCHAR(60) NOT NULL,
  BPIdnNmbrIssuingInstitute NVARCHAR(40),
  BPIdentificationEntryDate DATE,
  Country NVARCHAR(3),
  Region NVARCHAR(3),
  ValidityStartDate DATE,
  ValidityEndDate DATE,
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(BusinessPartner, BPIdentificationType, BPIdentificationNumber)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BuPaIndustry (
  IndustrySector NVARCHAR(10) NOT NULL,
  IndustrySystemType NVARCHAR(4) NOT NULL,
  BusinessPartner NVARCHAR(10) NOT NULL,
  IsStandardIndustry NVARCHAR(1),
  IndustryKeyDescription NVARCHAR(100),
  PRIMARY KEY(IndustrySector, IndustrySystemType, BusinessPartner)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BusinessPartner (
  BusinessPartner NVARCHAR(10) NOT NULL,
  Customer NVARCHAR(10),
  Supplier NVARCHAR(10),
  AcademicTitle NVARCHAR(4),
  AuthorizationGroup NVARCHAR(4),
  BusinessPartnerCategory NVARCHAR(1),
  BusinessPartnerFullName NVARCHAR(81),
  BusinessPartnerGrouping NVARCHAR(4),
  BusinessPartnerName NVARCHAR(81),
  BusinessPartnerUUID NVARCHAR(36),
  CorrespondenceLanguage NVARCHAR(2),
  CreatedByUser NVARCHAR(12),
  CreationDate DATE,
  CreationTime TIME,
  FirstName NVARCHAR(40),
  FormOfAddress NVARCHAR(4),
  Industry NVARCHAR(10),
  InternationalLocationNumber1 NVARCHAR(7),
  InternationalLocationNumber2 NVARCHAR(5),
  IsFemale BOOLEAN,
  IsMale BOOLEAN,
  IsNaturalPerson NVARCHAR(1),
  IsSexUnknown BOOLEAN,
  GenderCodeName NVARCHAR(1),
  Language NVARCHAR(2),
  LastChangeDate DATE,
  LastChangeTime TIME,
  LastChangedByUser NVARCHAR(12),
  LastName NVARCHAR(40),
  LegalForm NVARCHAR(2),
  OrganizationBPName1 NVARCHAR(40),
  OrganizationBPName2 NVARCHAR(40),
  OrganizationBPName3 NVARCHAR(40),
  OrganizationBPName4 NVARCHAR(40),
  OrganizationFoundationDate DATE,
  OrganizationLiquidationDate DATE,
  SearchTerm1 NVARCHAR(20),
  SearchTerm2 NVARCHAR(20),
  AdditionalLastName NVARCHAR(40),
  BirthDate DATE,
  BusinessPartnerBirthplaceName NVARCHAR(40),
  BusinessPartnerIsBlocked BOOLEAN,
  BusinessPartnerType NVARCHAR(4),
  ETag NVARCHAR(26),
  GroupBusinessPartnerName1 NVARCHAR(40),
  GroupBusinessPartnerName2 NVARCHAR(40),
  IndependentAddressID NVARCHAR(10),
  InternationalLocationNumber3 NVARCHAR(1),
  MiddleName NVARCHAR(40),
  NameCountry NVARCHAR(3),
  NameFormat NVARCHAR(2),
  PersonFullName NVARCHAR(80),
  PersonNumber NVARCHAR(10),
  IsMarkedForArchiving BOOLEAN,
  BusinessPartnerIDByExtSystem NVARCHAR(20),
  TradingPartner NVARCHAR(6),
  PRIMARY KEY(BusinessPartner)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BusinessPartnerAddress (
  BusinessPartner NVARCHAR(10) NOT NULL,
  AddressID NVARCHAR(10) NOT NULL,
  ValidityStartDate TIMESTAMP(0),
  ValidityEndDate TIMESTAMP(0),
  AuthorizationGroup NVARCHAR(4),
  AddressUUID NVARCHAR(36),
  AdditionalStreetPrefixName NVARCHAR(40),
  AdditionalStreetSuffixName NVARCHAR(40),
  AddressTimeZone NVARCHAR(6),
  CareOfName NVARCHAR(40),
  CityCode NVARCHAR(12),
  CityName NVARCHAR(40),
  CompanyPostalCode NVARCHAR(10),
  Country NVARCHAR(3),
  County NVARCHAR(40),
  DeliveryServiceNumber NVARCHAR(10),
  DeliveryServiceTypeCode NVARCHAR(4),
  District NVARCHAR(40),
  FormOfAddress NVARCHAR(4),
  FullName NVARCHAR(80),
  HomeCityName NVARCHAR(40),
  HouseNumber NVARCHAR(10),
  HouseNumberSupplementText NVARCHAR(10),
  Language NVARCHAR(2),
  POBox NVARCHAR(10),
  POBoxDeviatingCityName NVARCHAR(40),
  POBoxDeviatingCountry NVARCHAR(3),
  POBoxDeviatingRegion NVARCHAR(3),
  POBoxIsWithoutNumber BOOLEAN,
  POBoxLobbyName NVARCHAR(40),
  POBoxPostalCode NVARCHAR(10),
  Person NVARCHAR(10),
  PostalCode NVARCHAR(10),
  PrfrdCommMediumType NVARCHAR(3),
  Region NVARCHAR(3),
  StreetName NVARCHAR(60),
  StreetPrefixName NVARCHAR(40),
  StreetSuffixName NVARCHAR(40),
  TaxJurisdiction NVARCHAR(15),
  TransportZone NVARCHAR(10),
  AddressIDByExternalSystem NVARCHAR(20),
  PRIMARY KEY(BusinessPartner, AddressID)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BusinessPartnerBank (
  BusinessPartner NVARCHAR(10) NOT NULL,
  BankIdentification NVARCHAR(4) NOT NULL,
  BankCountryKey NVARCHAR(3),
  BankName NVARCHAR(60),
  BankNumber NVARCHAR(15),
  SWIFTCode NVARCHAR(11),
  BankControlKey NVARCHAR(2),
  BankAccountHolderName NVARCHAR(60),
  BankAccountName NVARCHAR(40),
  ValidityStartDate TIMESTAMP(0),
  ValidityEndDate TIMESTAMP(0),
  IBAN NVARCHAR(34),
  IBANValidityStartDate DATE,
  BankAccount NVARCHAR(18),
  BankAccountReferenceText NVARCHAR(20),
  CollectionAuthInd BOOLEAN,
  CityName NVARCHAR(35),
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(BusinessPartner, BankIdentification)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BusinessPartnerContact (
  RelationshipNumber NVARCHAR(12) NOT NULL,
  BusinessPartnerCompany NVARCHAR(10) NOT NULL,
  BusinessPartnerPerson NVARCHAR(10) NOT NULL,
  ValidityEndDate DATE NOT NULL,
  ValidityStartDate DATE,
  IsStandardRelationship BOOLEAN,
  RelationshipCategory NVARCHAR(6),
  PRIMARY KEY(RelationshipNumber, BusinessPartnerCompany, BusinessPartnerPerson, ValidityEndDate)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BusinessPartnerRole (
  BusinessPartner NVARCHAR(10) NOT NULL,
  BusinessPartnerRole NVARCHAR(6) NOT NULL,
  ValidFrom TIMESTAMP(0),
  ValidTo TIMESTAMP(0),
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(BusinessPartner, BusinessPartnerRole)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_BusinessPartnerTaxNumber (
  BusinessPartner NVARCHAR(10) NOT NULL,
  BPTaxType NVARCHAR(4) NOT NULL,
  BPTaxNumber NVARCHAR(20),
  BPTaxLongNumber NVARCHAR(60),
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(BusinessPartner, BPTaxType)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_Customer (
  Customer NVARCHAR(10) NOT NULL,
  AuthorizationGroup NVARCHAR(4),
  BillingIsBlockedForCustomer NVARCHAR(2),
  CreatedByUser NVARCHAR(12),
  CreationDate DATE,
  CustomerAccountGroup NVARCHAR(4),
  CustomerClassification NVARCHAR(2),
  CustomerFullName NVARCHAR(220),
  CustomerName NVARCHAR(80),
  DeliveryIsBlocked NVARCHAR(2),
  NFPartnerIsNaturalPerson NVARCHAR(1),
  OrderIsBlockedForCustomer NVARCHAR(2),
  PostingIsBlocked BOOLEAN,
  Supplier NVARCHAR(10),
  CustomerCorporateGroup NVARCHAR(10),
  FiscalAddress NVARCHAR(10),
  Industry NVARCHAR(4),
  IndustryCode1 NVARCHAR(10),
  IndustryCode2 NVARCHAR(10),
  IndustryCode3 NVARCHAR(10),
  IndustryCode4 NVARCHAR(10),
  IndustryCode5 NVARCHAR(10),
  InternationalLocationNumber1 NVARCHAR(7),
  NielsenRegion NVARCHAR(2),
  ResponsibleType NVARCHAR(2),
  TaxNumber1 NVARCHAR(16),
  TaxNumber2 NVARCHAR(11),
  TaxNumber3 NVARCHAR(18),
  TaxNumber4 NVARCHAR(18),
  TaxNumber5 NVARCHAR(60),
  TaxNumberType NVARCHAR(2),
  VATRegistration NVARCHAR(20),
  DeletionIndicator BOOLEAN,
  PRIMARY KEY(Customer)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_CustomerCompany (
  Customer NVARCHAR(10) NOT NULL,
  CompanyCode NVARCHAR(4) NOT NULL,
  APARToleranceGroup NVARCHAR(4),
  AccountByCustomer NVARCHAR(12),
  AccountingClerk NVARCHAR(2),
  AccountingClerkFaxNumber NVARCHAR(31),
  AccountingClerkInternetAddress NVARCHAR(130),
  AccountingClerkPhoneNumber NVARCHAR(30),
  AlternativePayerAccount NVARCHAR(10),
  AuthorizationGroup NVARCHAR(4),
  CollectiveInvoiceVariant NVARCHAR(1),
  CustomerAccountNote NVARCHAR(30),
  CustomerHeadOffice NVARCHAR(10),
  CustomerSupplierClearingIsUsed BOOLEAN,
  HouseBank NVARCHAR(5),
  InterestCalculationCode NVARCHAR(2),
  InterestCalculationDate DATE,
  IsToBeLocallyProcessed BOOLEAN,
  ItemIsToBePaidSeparately BOOLEAN,
  LayoutSortingRule NVARCHAR(3),
  PaymentBlockingReason NVARCHAR(1),
  PaymentMethodsList NVARCHAR(10),
  PaymentTerms NVARCHAR(4),
  PaytAdviceIsSentbyEDI BOOLEAN,
  PhysicalInventoryBlockInd BOOLEAN,
  ReconciliationAccount NVARCHAR(10),
  RecordPaymentHistoryIndicator BOOLEAN,
  UserAtCustomer NVARCHAR(15),
  DeletionIndicator BOOLEAN,
  CashPlanningGroup NVARCHAR(10),
  KnownOrNegotiatedLeave NVARCHAR(4),
  ValueAdjustmentKey NVARCHAR(2),
  CustomerAccountGroup NVARCHAR(4),
  PRIMARY KEY(Customer, CompanyCode)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_CustomerDunning (
  Customer NVARCHAR(10) NOT NULL,
  CompanyCode NVARCHAR(4) NOT NULL,
  DunningArea NVARCHAR(2) NOT NULL,
  DunningBlock NVARCHAR(1),
  DunningLevel NVARCHAR(1),
  DunningProcedure NVARCHAR(4),
  DunningRecipient NVARCHAR(10),
  LastDunnedOn DATE,
  LegDunningProcedureOn DATE,
  DunningClerk NVARCHAR(2),
  AuthorizationGroup NVARCHAR(4),
  CustomerAccountGroup NVARCHAR(4),
  PRIMARY KEY(Customer, CompanyCode, DunningArea)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_CustomerSalesArea (
  Customer NVARCHAR(10) NOT NULL,
  SalesOrganization NVARCHAR(4) NOT NULL,
  DistributionChannel NVARCHAR(2) NOT NULL,
  Division NVARCHAR(2) NOT NULL,
  AccountByCustomer NVARCHAR(12),
  AuthorizationGroup NVARCHAR(4),
  BillingIsBlockedForCustomer NVARCHAR(2),
  CompleteDeliveryIsDefined BOOLEAN,
  Currency NVARCHAR(5),
  CustomerABCClassification NVARCHAR(2),
  CustomerAccountAssignmentGroup NVARCHAR(2),
  CustomerGroup NVARCHAR(2),
  CustomerPaymentTerms NVARCHAR(4),
  CustomerPriceGroup NVARCHAR(2),
  CustomerPricingProcedure NVARCHAR(2),
  DeliveryIsBlockedForCustomer NVARCHAR(2),
  DeliveryPriority NVARCHAR(2),
  IncotermsClassification NVARCHAR(3),
  IncotermsLocation2 NVARCHAR(70),
  IncotermsVersion NVARCHAR(4),
  IncotermsLocation1 NVARCHAR(70),
  DeletionIndicator BOOLEAN,
  IncotermsTransferLocation NVARCHAR(28),
  InvoiceDate NVARCHAR(2),
  ItemOrderProbabilityInPercent NVARCHAR(3),
  OrderCombinationIsAllowed BOOLEAN,
  OrderIsBlockedForCustomer NVARCHAR(2),
  PartialDeliveryIsAllowed NVARCHAR(1),
  PriceListType NVARCHAR(2),
  SalesGroup NVARCHAR(3),
  SalesOffice NVARCHAR(4),
  ShippingCondition NVARCHAR(2),
  SupplyingPlant NVARCHAR(4),
  SalesDistrict NVARCHAR(6),
  InvoiceListSchedule NVARCHAR(2),
  ExchangeRateType NVARCHAR(4),
  AdditionalCustomerGroup1 NVARCHAR(3),
  AdditionalCustomerGroup2 NVARCHAR(3),
  AdditionalCustomerGroup3 NVARCHAR(3),
  AdditionalCustomerGroup4 NVARCHAR(3),
  AdditionalCustomerGroup5 NVARCHAR(3),
  PaymentGuaranteeProcedure NVARCHAR(4),
  CustomerAccountGroup NVARCHAR(4),
  PRIMARY KEY(Customer, SalesOrganization, DistributionChannel, Division)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_CustomerSalesAreaTax (
  Customer NVARCHAR(10) NOT NULL,
  SalesOrganization NVARCHAR(4) NOT NULL,
  DistributionChannel NVARCHAR(2) NOT NULL,
  Division NVARCHAR(2) NOT NULL,
  DepartureCountry NVARCHAR(3) NOT NULL,
  CustomerTaxCategory NVARCHAR(4) NOT NULL,
  CustomerTaxClassification NVARCHAR(1),
  PRIMARY KEY(Customer, SalesOrganization, DistributionChannel, Division, DepartureCountry, CustomerTaxCategory)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_CustomerWithHoldingTax (
  Customer NVARCHAR(10) NOT NULL,
  CompanyCode NVARCHAR(4) NOT NULL,
  WithholdingTaxType NVARCHAR(2) NOT NULL,
  WithholdingTaxCode NVARCHAR(2),
  WithholdingTaxAgent BOOLEAN,
  ObligationDateBegin DATE,
  ObligationDateEnd DATE,
  WithholdingTaxNumber NVARCHAR(16),
  WithholdingTaxCertificate NVARCHAR(25),
  WithholdingTaxExmptPercent DECIMAL(5, 2),
  ExemptionDateBegin DATE,
  ExemptionDateEnd DATE,
  ExemptionReason NVARCHAR(2),
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(Customer, CompanyCode, WithholdingTaxType)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_CustSalesPartnerFunc (
  Customer NVARCHAR(10) NOT NULL,
  SalesOrganization NVARCHAR(4) NOT NULL,
  DistributionChannel NVARCHAR(2) NOT NULL,
  Division NVARCHAR(2) NOT NULL,
  PartnerCounter NVARCHAR(3) NOT NULL,
  PartnerFunction NVARCHAR(2) NOT NULL,
  BPCustomerNumber NVARCHAR(10),
  CustomerPartnerDescription NVARCHAR(30),
  DefaultPartner BOOLEAN,
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(Customer, SalesOrganization, DistributionChannel, Division, PartnerCounter, PartnerFunction)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_Supplier (
  Supplier NVARCHAR(10) NOT NULL,
  AlternativePayeeAccountNumber NVARCHAR(10),
  AuthorizationGroup NVARCHAR(4),
  CreatedByUser NVARCHAR(12),
  CreationDate DATE,
  Customer NVARCHAR(10),
  PaymentIsBlockedForSupplier BOOLEAN,
  PostingIsBlocked BOOLEAN,
  PurchasingIsBlocked BOOLEAN,
  SupplierAccountGroup NVARCHAR(4),
  SupplierFullName NVARCHAR(220),
  SupplierName NVARCHAR(80),
  VATRegistration NVARCHAR(20),
  BirthDate DATE,
  ConcatenatedInternationalLocNo NVARCHAR(20),
  DeletionIndicator BOOLEAN,
  FiscalAddress NVARCHAR(10),
  Industry NVARCHAR(4),
  InternationalLocationNumber1 NVARCHAR(7),
  InternationalLocationNumber2 NVARCHAR(5),
  InternationalLocationNumber3 NVARCHAR(1),
  IsNaturalPerson NVARCHAR(1),
  ResponsibleType NVARCHAR(2),
  SuplrQltyInProcmtCertfnValidTo DATE,
  SuplrQualityManagementSystem NVARCHAR(4),
  SupplierCorporateGroup NVARCHAR(10),
  SupplierProcurementBlock NVARCHAR(2),
  TaxNumber1 NVARCHAR(16),
  TaxNumber2 NVARCHAR(11),
  TaxNumber3 NVARCHAR(18),
  TaxNumber4 NVARCHAR(18),
  TaxNumber5 NVARCHAR(60),
  TaxNumberResponsible NVARCHAR(18),
  TaxNumberType NVARCHAR(2),
  SuplrProofOfDelivRlvtCode NVARCHAR(1),
  BR_TaxIsSplit BOOLEAN,
  PRIMARY KEY(Supplier)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_SupplierCompany (
  Supplier NVARCHAR(10) NOT NULL,
  CompanyCode NVARCHAR(4) NOT NULL,
  AuthorizationGroup NVARCHAR(4),
  CompanyCodeName NVARCHAR(25),
  PaymentBlockingReason NVARCHAR(1),
  SupplierIsBlockedForPosting BOOLEAN,
  AccountingClerk NVARCHAR(2),
  AccountingClerkFaxNumber NVARCHAR(31),
  AccountingClerkPhoneNumber NVARCHAR(30),
  SupplierClerk NVARCHAR(15),
  SupplierClerkURL NVARCHAR(130),
  PaymentMethodsList NVARCHAR(10),
  PaymentTerms NVARCHAR(4),
  ClearCustomerSupplier BOOLEAN,
  IsToBeLocallyProcessed BOOLEAN,
  ItemIsToBePaidSeparately BOOLEAN,
  PaymentIsToBeSentByEDI BOOLEAN,
  HouseBank NVARCHAR(5),
  CheckPaidDurationInDays DECIMAL(3, 0),
  Currency NVARCHAR(5),
  BillOfExchLmtAmtInCoCodeCrcy DECIMAL(14, 3),
  SupplierClerkIDBySupplier NVARCHAR(12),
  ReconciliationAccount NVARCHAR(10),
  InterestCalculationCode NVARCHAR(2),
  InterestCalculationDate DATE,
  SupplierHeadOffice NVARCHAR(10),
  AlternativePayee NVARCHAR(10),
  LayoutSortingRule NVARCHAR(3),
  APARToleranceGroup NVARCHAR(4),
  SupplierCertificationDate DATE,
  SupplierAccountNote NVARCHAR(30),
  WithholdingTaxCountry NVARCHAR(3),
  DeletionIndicator BOOLEAN,
  CashPlanningGroup NVARCHAR(10),
  IsToBeCheckedForDuplicates BOOLEAN,
  MinorityGroup NVARCHAR(3),
  SupplierAccountGroup NVARCHAR(4),
  PRIMARY KEY(Supplier, CompanyCode)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_SupplierDunning (
  Supplier NVARCHAR(10) NOT NULL,
  CompanyCode NVARCHAR(4) NOT NULL,
  DunningArea NVARCHAR(2) NOT NULL,
  DunningBlock NVARCHAR(1),
  DunningLevel NVARCHAR(1),
  DunningProcedure NVARCHAR(4),
  DunningRecipient NVARCHAR(10),
  LastDunnedOn DATE,
  LegDunningProcedureOn DATE,
  DunningClerk NVARCHAR(2),
  AuthorizationGroup NVARCHAR(4),
  SupplierAccountGroup NVARCHAR(4),
  PRIMARY KEY(Supplier, CompanyCode, DunningArea)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_SupplierPartnerFunc (
  Supplier NVARCHAR(10) NOT NULL,
  PurchasingOrganization NVARCHAR(4) NOT NULL,
  SupplierSubrange NVARCHAR(6) NOT NULL,
  Plant NVARCHAR(4) NOT NULL,
  PartnerFunction NVARCHAR(2) NOT NULL,
  PartnerCounter NVARCHAR(3) NOT NULL,
  DefaultPartner BOOLEAN,
  CreationDate DATE,
  CreatedByUser NVARCHAR(12),
  ReferenceSupplier NVARCHAR(10),
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(Supplier, PurchasingOrganization, SupplierSubrange, Plant, PartnerFunction, PartnerCounter)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_SupplierPurchasingOrg (
  Supplier NVARCHAR(10) NOT NULL,
  PurchasingOrganization NVARCHAR(4) NOT NULL,
  CalculationSchemaGroupCode NVARCHAR(2),
  DeletionIndicator BOOLEAN,
  IncotermsClassification NVARCHAR(3),
  IncotermsTransferLocation NVARCHAR(28),
  IncotermsVersion NVARCHAR(4),
  IncotermsLocation1 NVARCHAR(70),
  IncotermsLocation2 NVARCHAR(70),
  InvoiceIsGoodsReceiptBased BOOLEAN,
  MaterialPlannedDeliveryDurn DECIMAL(3, 0),
  MinimumOrderAmount DECIMAL(14, 3),
  PaymentTerms NVARCHAR(4),
  PricingDateControl NVARCHAR(1),
  PurOrdAutoGenerationIsAllowed BOOLEAN,
  PurchaseOrderCurrency NVARCHAR(5),
  PurchasingGroup NVARCHAR(3),
  PurchasingIsBlockedForSupplier BOOLEAN,
  ShippingCondition NVARCHAR(2),
  SupplierABCClassificationCode NVARCHAR(1),
  SupplierPhoneNumber NVARCHAR(16),
  SupplierRespSalesPersonName NVARCHAR(30),
  AuthorizationGroup NVARCHAR(4),
  SupplierAccountGroup NVARCHAR(4),
  PRIMARY KEY(Supplier, PurchasingOrganization)
); 

CREATE TABLE API_BUSINESS_PARTNER_A_SupplierWithHoldingTax (
  Supplier NVARCHAR(10) NOT NULL,
  CompanyCode NVARCHAR(4) NOT NULL,
  WithholdingTaxType NVARCHAR(2) NOT NULL,
  ExemptionDateBegin DATE,
  ExemptionDateEnd DATE,
  ExemptionReason NVARCHAR(2),
  IsWithholdingTaxSubject BOOLEAN,
  RecipientType NVARCHAR(2),
  WithholdingTaxCertificate NVARCHAR(25),
  WithholdingTaxCode NVARCHAR(2),
  WithholdingTaxExmptPercent DECIMAL(5, 2),
  WithholdingTaxNumber NVARCHAR(16),
  AuthorizationGroup NVARCHAR(4),
  PRIMARY KEY(Supplier, CompanyCode, WithholdingTaxType)
); 

CREATE TABLE my_bookshop_Books_texts (
  ID_texts NVARCHAR(36) NOT NULL,
  locale NVARCHAR(14),
  ID NVARCHAR(36),
  title NVARCHAR(111),
  descr NVARCHAR(1111),
  PRIMARY KEY(ID_texts),
  CONSTRAINT my_bookshop_Books_texts_locale UNIQUE (locale, ID)
); 

CREATE TABLE my_bookshop_Genres_texts (
  locale NVARCHAR(14) NOT NULL,
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  ID INTEGER NOT NULL,
  PRIMARY KEY(locale, ID)
); 

CREATE TABLE my_bookshop_Orders_changes (
  up__ID NVARCHAR(36) NOT NULL,
  change_ID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(up__ID, change_ID)
); 

CREATE TABLE sap_common_Languages_texts (
  locale NVARCHAR(14) NOT NULL,
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(14) NOT NULL,
  PRIMARY KEY(locale, code)
); 

CREATE TABLE sap_common_Currencies_texts (
  locale NVARCHAR(14) NOT NULL,
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(3) NOT NULL,
  PRIMARY KEY(locale, code)
); 

CREATE TABLE DRAFT_DraftAdministrativeData (
  DraftUUID NVARCHAR(36) NOT NULL,
  CreationDateTime TIMESTAMP(7),
  CreatedByUser NVARCHAR(256),
  DraftIsCreatedByMe BOOLEAN,
  LastChangeDateTime TIMESTAMP(7),
  LastChangedByUser NVARCHAR(256),
  InProcessByUser NVARCHAR(256),
  DraftIsProcessedByMe BOOLEAN,
  PRIMARY KEY(DraftUUID)
); 

CREATE TABLE AdminService_Books_drafts (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7) NULL,
  createdBy NVARCHAR(255) NULL,
  modifiedAt TIMESTAMP(7) NULL,
  modifiedBy NVARCHAR(255) NULL,
  title NVARCHAR(111) NULL,
  descr NVARCHAR(1111) NULL,
  author_ID NVARCHAR(36) NULL,
  genre_ID INTEGER NULL,
  stock INTEGER NULL,
  price DECIMAL(9, 2) NULL,
  currency_code NVARCHAR(3) NULL,
  rating DECIMAL(2, 1) NULL,
  isReviewable BOOLEAN NULL DEFAULT TRUE,
  isbn NVARCHAR(40) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(ID),
  FOREIGN KEY (DraftAdministrativeData_DraftUUID) REFERENCES DRAFT_DraftAdministrativeData(DraftUUID) ON DELETE CASCADE
); 

CREATE TABLE AdminService_Books_texts_drafts (
  ID_texts NVARCHAR(36) NOT NULL,
  locale NVARCHAR(14) NULL,
  ID NVARCHAR(36) NULL,
  title NVARCHAR(111) NULL,
  descr NVARCHAR(1111) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(ID_texts),
  FOREIGN KEY (DraftAdministrativeData_DraftUUID) REFERENCES DRAFT_DraftAdministrativeData(DraftUUID) ON DELETE CASCADE
); 

CREATE TABLE AdminService_Orders_drafts (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7) NULL,
  createdBy NVARCHAR(255) NULL,
  modifiedAt TIMESTAMP(7) NULL,
  modifiedBy NVARCHAR(255) NULL,
  OrderNo NVARCHAR(255) NULL,
  buyer NVARCHAR(255) NULL,
  total DECIMAL(9, 2) NULL,
  currency_code NVARCHAR(3) NULL,
  shippingAddress_ID NVARCHAR(10) NULL,
  shippingAddress_businessPartner NVARCHAR(10) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(ID),
  FOREIGN KEY (DraftAdministrativeData_DraftUUID) REFERENCES DRAFT_DraftAdministrativeData(DraftUUID) ON DELETE CASCADE

); 

CREATE TABLE AdminService_OrderItems_drafts (
  ID NVARCHAR(36) NOT NULL,
  parent_ID NVARCHAR(36) NULL,
  book_ID NVARCHAR(36) NULL,
  quantity INTEGER NULL,
  amount DECIMAL(9, 2) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(ID),
  FOREIGN KEY (DraftAdministrativeData_DraftUUID) REFERENCES DRAFT_DraftAdministrativeData(DraftUUID) ON DELETE CASCADE

); 

CREATE TABLE NotesService_Notes_drafts (
  ID NVARCHAR(36) NOT NULL,
  note NVARCHAR(255) NULL,
  address_ID NVARCHAR(10) NULL,
  address_businessPartner NVARCHAR(10) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(ID),
  FOREIGN KEY (DraftAdministrativeData_DraftUUID) REFERENCES DRAFT_DraftAdministrativeData(DraftUUID) ON DELETE CASCADE
); 

CREATE TABLE ReviewService_Reviews_drafts (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7) NULL,
  createdBy NVARCHAR(255) NULL,
  modifiedAt TIMESTAMP(7) NULL,
  modifiedBy NVARCHAR(255) NULL,
  book_ID NVARCHAR(36) NULL,
  rating INTEGER NULL,
  title NVARCHAR(111) NULL,
  text NVARCHAR(1111) NULL,
  IsActiveEntity BOOLEAN,
  HasActiveEntity BOOLEAN,
  HasDraftEntity BOOLEAN,
  DraftAdministrativeData_DraftUUID NVARCHAR(36) NOT NULL,
  PRIMARY KEY(ID),
  FOREIGN KEY (DraftAdministrativeData_DraftUUID) REFERENCES DRAFT_DraftAdministrativeData(DraftUUID) ON DELETE CASCADE
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
  Books_0.rating,
  Books_0.isReviewable,
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

CREATE VIEW AdminService_Orders AS SELECT
  Orders_0.ID,
  Orders_0.createdAt,
  Orders_0.createdBy,
  Orders_0.modifiedAt,
  Orders_0.modifiedBy,
  Orders_0.OrderNo,
  Orders_0.buyer,
  Orders_0.total,
  Orders_0.currency_code,
  Orders_0.shippingAddress_ID,
  Orders_0.shippingAddress_businessPartner
FROM my_bookshop_Orders AS Orders_0; 

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
  Books_0.isReviewable,
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

CREATE VIEW CatalogService_Reviews AS SELECT
  Reviews_0.ID,
  Reviews_0.createdAt,
  Reviews_0.createdBy,
  Reviews_0.modifiedAt,
  Reviews_0.modifiedBy,
  Reviews_0.book_ID,
  Reviews_0.rating,
  Reviews_0.title,
  Reviews_0.text
FROM my_bookshop_Reviews AS Reviews_0; 

CREATE VIEW my_bookshop_NoteableAddresses AS SELECT
  A_BusinessPartnerAddress_0.AddressID AS ID,
  A_BusinessPartnerAddress_0.BusinessPartner AS businessPartner,
  A_BusinessPartnerAddress_0.Country AS country,
  A_BusinessPartnerAddress_0.CityName AS city,
  A_BusinessPartnerAddress_0.PostalCode AS postalCode,
  A_BusinessPartnerAddress_0.StreetName AS street,
  A_BusinessPartnerAddress_0.HouseNumber AS houseNumber
FROM API_BUSINESS_PARTNER_A_BusinessPartnerAddress AS A_BusinessPartnerAddress_0; 

CREATE VIEW NotesService_Notes AS SELECT
  Notes_0.ID,
  Notes_0.note,
  Notes_0.address_ID,
  Notes_0.address_businessPartner
FROM my_bookshop_Notes AS Notes_0; 

CREATE VIEW ReviewService_Reviews AS SELECT
  Reviews_0.ID,
  Reviews_0.createdAt,
  Reviews_0.createdBy,
  Reviews_0.modifiedAt,
  Reviews_0.modifiedBy,
  Reviews_0.book_ID,
  Reviews_0.rating,
  Reviews_0.title,
  Reviews_0.text
FROM my_bookshop_Reviews AS Reviews_0; 

CREATE VIEW ReviewService_Books AS SELECT
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
  Books_0.isReviewable,
  Books_0.isbn
FROM my_bookshop_Books AS Books_0; 

CREATE VIEW ReviewService_Authors AS SELECT
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

CREATE VIEW AdminService_OrderItems AS SELECT
  OrderItems_0.ID,
  OrderItems_0.parent_ID,
  OrderItems_0.book_ID,
  OrderItems_0.quantity,
  OrderItems_0.amount
FROM my_bookshop_OrderItems AS OrderItems_0; 

CREATE VIEW AdminService_Orders_changes AS SELECT
  changes_0.up__ID,
  changes_0.change_ID
FROM my_bookshop_Orders_changes AS changes_0; 

CREATE VIEW AdminService_Addresses AS SELECT
  Addresses_0.ID,
  Addresses_0.businessPartner,
  Addresses_0.country,
  Addresses_0.city,
  Addresses_0.postalCode,
  Addresses_0.street,
  Addresses_0.houseNumber,
  Addresses_0.tombstone
FROM my_bookshop_Addresses AS Addresses_0; 

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

CREATE VIEW ReviewService_Genres AS SELECT
  Genres_0.name,
  Genres_0.descr,
  Genres_0.ID,
  Genres_0.parent_ID
FROM my_bookshop_Genres AS Genres_0; 

CREATE VIEW ReviewService_Currencies AS SELECT
  Currencies_0.name,
  Currencies_0.descr,
  Currencies_0.code,
  Currencies_0.symbol,
  Currencies_0.minorUnit
FROM sap_common_Currencies AS Currencies_0; 

CREATE VIEW ReviewService_Books_texts AS SELECT
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

CREATE VIEW AdminService_Changes AS SELECT
  Changes_0.ID,
  Changes_0.createdAt,
  Changes_0.createdBy,
  Changes_0.changeLogID,
  Changes_0.rootEntity,
  Changes_0.rootIdentifier,
  Changes_0.attribute,
  Changes_0.valueChangedFrom,
  Changes_0.valueChangedTo,
  Changes_0.valueDataType,
  Changes_0.targetIdentifier,
  Changes_0.targetEntity,
  Changes_0.path,
  Changes_0.modification
FROM sap_changelog_Changes AS Changes_0; 

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

CREATE VIEW ReviewService_Genres_texts AS SELECT
  texts_0.locale,
  texts_0.name,
  texts_0.descr,
  texts_0.ID
FROM my_bookshop_Genres_texts AS texts_0; 

CREATE VIEW ReviewService_Currencies_texts AS SELECT
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
  L_0.isReviewable,
  L_0.isbn
FROM (my_bookshop_Books AS L_0 LEFT JOIN my_bookshop_Books_texts AS localized_1 ON localized_1.ID = L_0.ID AND localized_1.locale = @locale); 

CREATE VIEW localized_my_bookshop_Genres AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.ID,
  L_0.parent_ID
FROM (my_bookshop_Genres AS L_0 LEFT JOIN my_bookshop_Genres_texts AS localized_1 ON localized_1.ID = L_0.ID AND localized_1.locale = @locale); 

CREATE VIEW localized_sap_common_Languages AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.code
FROM (sap_common_Languages AS L_0 LEFT JOIN sap_common_Languages_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = @locale); 

CREATE VIEW localized_sap_common_Currencies AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.code,
  L_0.symbol,
  L_0.minorUnit
FROM (sap_common_Currencies AS L_0 LEFT JOIN sap_common_Currencies_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = @locale); 

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

CREATE VIEW localized_my_bookshop_Orders AS SELECT
  L.ID,
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.OrderNo,
  L.buyer,
  L.total,
  L.currency_code,
  L.shippingAddress_ID,
  L.shippingAddress_businessPartner
FROM my_bookshop_Orders AS L; 

CREATE VIEW localized_my_bookshop_OrderItems AS SELECT
  L.ID,
  L.parent_ID,
  L.book_ID,
  L.quantity,
  L.amount
FROM my_bookshop_OrderItems AS L; 

CREATE VIEW localized_my_bookshop_Reviews AS SELECT
  L.ID,
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.book_ID,
  L.rating,
  L.title,
  L.text
FROM my_bookshop_Reviews AS L; 

CREATE VIEW localized_my_bookshop_Orders_changes AS SELECT
  L.up__ID,
  L.change_ID
FROM my_bookshop_Orders_changes AS L; 

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

CREATE VIEW NotesService_DraftAdministrativeData AS SELECT
  DraftAdministrativeData.DraftUUID,
  DraftAdministrativeData.CreationDateTime,
  DraftAdministrativeData.CreatedByUser,
  DraftAdministrativeData.DraftIsCreatedByMe,
  DraftAdministrativeData.LastChangeDateTime,
  DraftAdministrativeData.LastChangedByUser,
  DraftAdministrativeData.InProcessByUser,
  DraftAdministrativeData.DraftIsProcessedByMe
FROM DRAFT_DraftAdministrativeData AS DraftAdministrativeData; 

CREATE VIEW ReviewService_DraftAdministrativeData AS SELECT
  DraftAdministrativeData.DraftUUID,
  DraftAdministrativeData.CreationDateTime,
  DraftAdministrativeData.CreatedByUser,
  DraftAdministrativeData.DraftIsCreatedByMe,
  DraftAdministrativeData.LastChangeDateTime,
  DraftAdministrativeData.LastChangedByUser,
  DraftAdministrativeData.InProcessByUser,
  DraftAdministrativeData.DraftIsProcessedByMe
FROM DRAFT_DraftAdministrativeData AS DraftAdministrativeData; 

CREATE VIEW NotesService_Addresses AS SELECT
  NoteableAddresses_0.ID,
  NoteableAddresses_0.businessPartner,
  NoteableAddresses_0.country,
  NoteableAddresses_0.city,
  NoteableAddresses_0.postalCode,
  NoteableAddresses_0.street,
  NoteableAddresses_0.houseNumber
FROM my_bookshop_NoteableAddresses AS NoteableAddresses_0; 

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
  Books_0.isReviewable,
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
  Books_0.isReviewable,
  Books_0.isbn
FROM localized_my_bookshop_Books AS Books_0; 

CREATE VIEW localized_ReviewService_Books AS SELECT
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
  Books_0.isReviewable,
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

CREATE VIEW localized_ReviewService_Genres AS SELECT
  Genres_0.name,
  Genres_0.descr,
  Genres_0.ID,
  Genres_0.parent_ID
FROM localized_my_bookshop_Genres AS Genres_0; 

CREATE VIEW localized_ReviewService_Currencies AS SELECT
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

CREATE VIEW localized_AdminService_OrderItems AS SELECT
  OrderItems_0.ID,
  OrderItems_0.parent_ID,
  OrderItems_0.book_ID,
  OrderItems_0.quantity,
  OrderItems_0.amount
FROM localized_my_bookshop_OrderItems AS OrderItems_0; 

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

CREATE VIEW localized_CatalogService_Reviews AS SELECT
  Reviews_0.ID,
  Reviews_0.createdAt,
  Reviews_0.createdBy,
  Reviews_0.modifiedAt,
  Reviews_0.modifiedBy,
  Reviews_0.book_ID,
  Reviews_0.rating,
  Reviews_0.title,
  Reviews_0.text
FROM localized_my_bookshop_Reviews AS Reviews_0; 

CREATE VIEW localized_ReviewService_Reviews AS SELECT
  Reviews_0.ID,
  Reviews_0.createdAt,
  Reviews_0.createdBy,
  Reviews_0.modifiedAt,
  Reviews_0.modifiedBy,
  Reviews_0.book_ID,
  Reviews_0.rating,
  Reviews_0.title,
  Reviews_0.text
FROM localized_my_bookshop_Reviews AS Reviews_0; 

CREATE VIEW localized_ReviewService_Authors AS SELECT
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

CREATE VIEW localized_AdminService_Orders AS SELECT
  Orders_0.ID,
  Orders_0.createdAt,
  Orders_0.createdBy,
  Orders_0.modifiedAt,
  Orders_0.modifiedBy,
  Orders_0.OrderNo,
  Orders_0.buyer,
  Orders_0.total,
  Orders_0.currency_code,
  Orders_0.shippingAddress_ID,
  Orders_0.shippingAddress_businessPartner
FROM localized_my_bookshop_Orders AS Orders_0; 

CREATE VIEW localized_AdminService_Orders_changes AS SELECT
  changes_0.up__ID,
  changes_0.change_ID
FROM localized_my_bookshop_Orders_changes AS changes_0; 

