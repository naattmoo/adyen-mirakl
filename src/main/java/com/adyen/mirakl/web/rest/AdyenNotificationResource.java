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

package com.adyen.mirakl.web.rest;

import com.adyen.mirakl.domain.AdyenNotification;
import com.adyen.mirakl.events.AdyenNotifcationEvent;
import com.adyen.mirakl.repository.AdyenNotificationRepository;
import com.adyen.mirakl.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST controller for managing AdyenNotification.
 */
@RestController
@RequestMapping("/api")
public class AdyenNotificationResource {

    private final Logger log = LoggerFactory.getLogger(AdyenNotificationResource.class);

    private static final String ENTITY_NAME = "adyenNotification";

    private final AdyenNotificationRepository adyenNotificationRepository;

    private final ApplicationEventPublisher publisher;

    public AdyenNotificationResource(AdyenNotificationRepository adyenNotificationRepository, ApplicationEventPublisher publisher) {
        this.adyenNotificationRepository = adyenNotificationRepository;
        this.publisher = publisher;
    }

    /**
     * POST  /adyen-notifications : Create a new adyenNotification.
     *
     * @param adyenNotification the adyenNotification to create
     * @return the ResponseEntity with status 201 (Created) and with body the new adyenNotification, or with status 400 (Bad Request) if the adyenNotification has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/adyen-notifications")
    public ResponseEntity<AdyenNotification> createAdyenNotification(@RequestBody String adyenNotification) throws URISyntaxException {
        final AdyenNotification entity = new AdyenNotification();
        entity.setRawAdyenNotification(adyenNotification);
        AdyenNotification result = adyenNotificationRepository.save(entity);
        publisher.publishEvent(new AdyenNotifcationEvent(result.getId()));
        return ResponseEntity.created(new URI("/api/adyen-notifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

}
