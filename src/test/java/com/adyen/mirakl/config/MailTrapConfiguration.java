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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mailtrapConfig", ignoreUnknownFields = false)
public class MailTrapConfiguration {

    public String baseMailTrapUrl;
    public String mailTrapInboxApi;
    public String mailTrapInboxId;
    public String apiToken;

    public String getBaseMailTrapUrl() {
        return baseMailTrapUrl;
    }

    public void setBaseMailTrapUrl(String baseMailTrapUrl) {
        this.baseMailTrapUrl = baseMailTrapUrl;
    }

    public String getMailTrapInboxApi() {
        return mailTrapInboxApi;
    }

    public void setMailTrapInboxApi(String mailTrapInboxApi) {
        this.mailTrapInboxApi = mailTrapInboxApi;
    }

    public String getMailTrapInboxId() {
        return mailTrapInboxId;
    }

    public void setMailTrapInboxId(String mailTrapInboxId) {
        this.mailTrapInboxId = mailTrapInboxId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    @Bean
    public String mailTrapEndPoint() {
        return getBaseMailTrapUrl() + getMailTrapInboxApi() + getMailTrapInboxId() + "/messages?api_token=" + getApiToken();
    }

    public String mailTrapHtmlBodyEndPoint(String htmlBodyEndpoint) {
        return getBaseMailTrapUrl() + htmlBodyEndpoint + "?api_token=" + getApiToken();
    }

}
