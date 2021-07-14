using { API_BUSINESS_PARTNER as external } from './external/API_BUSINESS_PARTNER';

/**
 * Tailor the imported API to our needs...
 */
extend service external with {

  /**
   * Simplified view on external addresses
   */
  entity Addresses as projection on external.A_BusinessPartnerAddress {
    key AddressID as ID,
    key BusinessPartner as businessPartner,
    @readonly Country as country,
    @readonly CityName as city,
    @readonly PostalCode as postalCode,
    @readonly StreetName as street,
    @readonly HouseNumber as houseNumber
  }

  /**
   * Add asynchronous eventing API
   */
  event BO_BusinessPartner_Changed {
    ![KEY]: Array of {
      BUSINESSPARTNER: String;
    };
  }

}

/**
 * Add an entity to replicate external address data for quick access,
 * e.g. when displaying lists of orders.
 */
@cds.persistence:{table,skip:false} //> create a table with the view's inferred signature
@cds.autoexpose //> auto-expose in services as targets for ValueHelps and joins
entity my.bookshop.Addresses as SELECT from external.Addresses { *, false as tombstone: Boolean };

/**
 * Extend Orders with references to replicated external Addresses
 */
using { my.bookshop } from '../db/index';
extend bookshop.Orders with {
  shippingAddress : Association to bookshop.Addresses;
}
