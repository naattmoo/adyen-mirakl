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

import com.mirakl.client.core.security.MiraklCredential;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApiClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "miraklFront", ignoreUnknownFields = false)
public class MiraklFrontConfiguration extends MiraklProperties {

    @Bean
    public MiraklCredential miraklFrontCredential(){
        return new MiraklCredential(getMiraklApiKey());
    }

    @Bean
    public MiraklMarketplacePlatformFrontApiClient miraklMarketplacePlatformFrontApiClient(){
        return new MiraklMarketplacePlatformFrontApiClient(getMiraklEnvUrl() + "/api", miraklFrontCredential());
    }

}
