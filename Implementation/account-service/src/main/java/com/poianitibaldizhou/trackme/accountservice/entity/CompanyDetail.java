package com.poianitibaldizhou.trackme.accountservice.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * JPA entity object regarding the company details of a third party customer that is a company
 */
@Data
@Entity
public class CompanyDetail {

    @Id
    private Long id;

    @MapsId
    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "thirdPartyCustomer")
    private ThirdPartyCustomer thirdPartyCustomer;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String dunsNumber;

}
