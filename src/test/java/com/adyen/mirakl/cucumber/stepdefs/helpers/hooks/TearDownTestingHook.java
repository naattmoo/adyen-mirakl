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

package com.adyen.mirakl.cucumber.stepdefs.helpers.hooks;

import com.adyen.model.marketpay.notification.DeleteNotificationConfigurationRequest;
import com.adyen.service.Notification;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TearDownTestingHook implements ApplicationListener<ContextClosedEvent> {

    private static final Logger log = LoggerFactory.getLogger(TearDownTestingHook.class);

    @Resource
    private StartUpTestingHook startUpTestingHook;

    @Resource
    private Notification adyenNotification;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            removeConfigs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeConfigs() throws Exception {
        final Long notificationId = startUpTestingHook.getNotificationId();
        log.info("Deleting notification configuration: {}",notificationId);
        DeleteNotificationConfigurationRequest deleteNotificationConfigurationRequest = new DeleteNotificationConfigurationRequest();
        deleteNotificationConfigurationRequest.setNotificationIds(ImmutableList.of(notificationId));
        adyenNotification.deleteNotificationConfiguration(deleteNotificationConfigurationRequest);
    }

}

