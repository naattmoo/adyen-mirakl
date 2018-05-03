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

import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.adyen.mirakl.service.PayoutService;

/**
 * MiraklNotifications controller
 */
@RestController
@RequestMapping("/api/mirakl-notifications")
public class MiraklNotificationsResource {

    private final PayoutService payoutService;

    public MiraklNotificationsResource(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    /**
     * POST payout
     */
    @PostMapping("/payout")
    public void receiveNotifications(@RequestPart("file") MultipartFile csvdata) throws IOException {
        String content = new String(csvdata.getBytes());
        if (! content.isEmpty()) {
            payoutService.parseMiraklCsv(content);
            payoutService.processMiraklVoucherEntries();
        }
    }

}
