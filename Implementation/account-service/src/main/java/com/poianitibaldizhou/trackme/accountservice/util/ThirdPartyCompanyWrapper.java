package com.poianitibaldizhou.trackme.accountservice.util;

import com.poianitibaldizhou.trackme.accountservice.entity.CompanyDetail;
import com.poianitibaldizhou.trackme.accountservice.entity.ThirdPartyCustomer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * This class wraps the information regarding third party customer that are companies
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ThirdPartyCompanyWrapper extends ThirdPartyWrapper implements Serializable{
    private CompanyDetail companyDetail;
}
