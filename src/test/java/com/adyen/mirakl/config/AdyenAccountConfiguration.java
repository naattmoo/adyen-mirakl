/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Mirakl Connector
 *
 * Copyright (c) 2018 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 *
 */

package com.adyen.mirakl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "accounts", ignoreUnknownFields = false)
public class AdyenAccountConfiguration {

    private Map<String, Integer> accountCode;

    private Map<String, String> adyenPal;

    public Map<String, Integer> getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(Map<String, Integer> accountCode) {
        this.accountCode = accountCode;
    }

    public Map<String, String> getAdyenPal() {
        return adyenPal;
    }

    public void setAdyenPal(Map<String, String> adyenPal) {
        this.adyenPal = adyenPal;
    }
}
