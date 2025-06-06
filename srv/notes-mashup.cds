using { API_BUSINESS_PARTNER } from './external/API_BUSINESS_PARTNER';

/**
 * Simplified view on external addresses, which is used as an association target in Notes.
 */
entity my.bookshop.NoteableAddresses as projection on API_BUSINESS_PARTNER.A_BusinessPartnerAddress {
  key AddressID as ID,
  key BusinessPartner as businessPartner,
  @readonly Country as country,
  @readonly CityName as city,
  @readonly PostalCode as postalCode,
  @readonly StreetName as street,
  @readonly HouseNumber as houseNumber,
  @odata.contained: false notes : Composition of many bookshop.Notes
      on notes.address.businessPartner = $self.businessPartner and notes.address.ID = $self.ID
};

/*
 * Extend Notes with references to external Addresses.
 */
using { my.bookshop } from '../db/index';
extend bookshop.Notes {
  address: Association to bookshop.NoteableAddresses;
}

using { NotesService } from './notes-service';
extend service NotesService with {
  @readonly
  entity Addresses as projection on bookshop.NoteableAddresses
}
