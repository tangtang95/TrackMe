package com.trackme.trackmeapplication.sharedData;

public class CompanyDetail implements ThirdPartyInterface{

    private String id;
    private String companyName;
    private ThirdPartyCustomer thirdPartyCustomer;
    private String address;
    private String dunsNumber;

    public CompanyDetail(String companyName, String email, String password, String address, String dunsNumber) {
        this.companyName = companyName;
        this.thirdPartyCustomer = new ThirdPartyCustomer(email, password);
        this.address = address;
        this.dunsNumber = dunsNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDunsNumber() {
        return dunsNumber;
    }

    @Override
    public String getName() {
        return companyName;
    }

    @Override
    public String getEmail() {
        return thirdPartyCustomer.getEmail();
    }

    @Override
    public String getPassword() {
        return thirdPartyCustomer.getPassword();
    }

}