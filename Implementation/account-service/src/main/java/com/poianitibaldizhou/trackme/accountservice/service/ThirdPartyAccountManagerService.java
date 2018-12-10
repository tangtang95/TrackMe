package com.poianitibaldizhou.trackme.accountservice.service;

import com.poianitibaldizhou.trackme.accountservice.util.ThirdPartyCompanyWrapper;
import com.poianitibaldizhou.trackme.accountservice.util.ThirdPartyPrivateWrapper;
import com.poianitibaldizhou.trackme.accountservice.util.ThirdPartyWrapper;

import java.util.Optional;

/**
 * Interfaces provided to offer the services that manage the accounts of third party customers
 */
public interface ThirdPartyAccountManagerService {

    /**
     * Retrieves a third party customer based on email, which is the id of the entity.
     * This requires, to be successful, that a customer with that email exists
     * The third party has to be registered and releted to a company.
     *
     * @param email email that identifies the requested customer
     * @return customer identified by the email
     */
    Optional<ThirdPartyCompanyWrapper> getThirdPartyCompanyByEmail(String email);

    /**
     * Retrieves a third party customer based on email, which is the id of the entity.
     * This requires, to be successful, that a customer with that email exists.
     * The third party has to be registered as a private one
     *
     * @param email email that identifies the requested customer
     * @return customer identified by the email
     */
    Optional<ThirdPartyPrivateWrapper> getThirdPartyPrivateByEmail(String email);



    /**
     * Register a third party customer that is a company. This requires information such as password,
     * email, and company details, that are contained in the wrapper parameter
     * The method, to be successful, requires that no other third party customer with the same e-mail or duns
     * number is present,
     *
     * @param thirdPartyCompanyWrapper information regarding the third party customer that is registering into the system
     *                                 as a company
     * @return registered third party customer
     */
    ThirdPartyCompanyWrapper registerThirdPartyCompany(ThirdPartyCompanyWrapper thirdPartyCompanyWrapper);

    /**
     * Register a third party customer that is not a company. This requires information such as email, password
     * and personal information of the customer, such as: first name, last name, ssn, birth date and birth city.
     * No other third party customer with same email or ssn has to be present.
     *
     * @param thirdPartyPrivateWrapper information regarding the third party customer that is registering into the system
     *                                 as private person (i.e. not a company)
     * @return registered third party customer
     */
    ThirdPartyPrivateWrapper registerThirdPartyPrivate(ThirdPartyPrivateWrapper thirdPartyPrivateWrapper);

    /**
     * Verify that the parameters provided math a third party who has registered to the system. No distinction between
     * company third party and private third party is applied here
     *
     * @param email username of the user
     * @param password password for the user identified by the username
     * @return true if the credential are matched with a registered user, false otherwise
     */
    boolean verifyThirdPartyCredential(String email, String password);
}
