package com.smacon.fish2marine.HelperClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by smacon on 13/1/17.
 */

public final class AddressListItem {

    String customer_id;
    String firstname;
    String lastname;
    String company;
    String city;
    String country;
    String region;
    String postcode;
    String telephone;
    String state;
    String address_id;
    String region_id;
    String street1,street2;
    String countryName;
    String is_default_billing;
    String is_default_shipping;
    List<String> street;

    public final void setStreet(List<String> street) {
        this.street = street;
    }

    public List<String> getStreet() {
        return street;
    }

    public final void setIs_default_shipping(String is_default_shipping) {
        this.is_default_shipping = is_default_shipping;
    }

    public final void setIs_default_billing(String is_default_billing) {
        this.is_default_billing = is_default_billing;
    }

    public final void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public final void setStreet1(String street1) {
        this.street1 = street1;
    }
    public final void setStreet2(String street2) {
        this.street2 = street2;
    }

    public final void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public final void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public final void setState(String state) {
        this.state = state;
    }

    public final void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public final void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public final void setRegion(String region) {
        this.region = region;
    }

    public final void setCountry(String country) {
        this.country = country;
    }

    public final void setCity(String city) {
        this.city = city;
    }

    public final void setCompany(String company) {
        this.company = company;
    }

    public final void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public final void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public final void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getCompany() {
        return company;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getState() {
        return state;
    }

    public String getAddress_id() {
        return address_id;
    }

    public String getRegion_id() {
        return region_id;
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getIs_default_billing() {
        return is_default_billing;
    }

    public String getIs_default_shipping() {
        return is_default_shipping;
    }

}
