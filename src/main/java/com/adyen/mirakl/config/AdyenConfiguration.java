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

import com.adyen.notification.NotificationHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.adyen.Client;
import com.adyen.Config;
import com.adyen.enums.Environment;
import com.adyen.service.Account;
import com.adyen.service.Fund;
import com.adyen.service.Notification;

@Configuration
@ConfigurationProperties(prefix = "adyenConfig", ignoreUnknownFields = false)
public class AdyenConfiguration {

    private String userName;
    private String password;
    private Environment environment;
    private String appName;
    private String appVersion;

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(final String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Bean
    public Config adyenConfig() {
        final Config config = new Config();
        config.setUsername(userName);
        config.setPassword(password);
        config.setApplicationName(appName + "_" + appVersion);
        return config;
    }

    @Bean
    public Client adyenClient() {
        Client client = new Client(adyenConfig());
        client.setEnvironment(environment);
        return client;
    }

    @Bean
    public Notification adyenNotification() {
        return new Notification(adyenClient());
    }

    @Bean
    public Account adyenAccountService() {
        return new Account(adyenClient());
    }

    @Bean
    public Fund createFundService() {
        return new Fund(adyenClient());
    }

    @Bean
    public NotificationHandler notificationHandler(){
        return new NotificationHandler();
    }
}
