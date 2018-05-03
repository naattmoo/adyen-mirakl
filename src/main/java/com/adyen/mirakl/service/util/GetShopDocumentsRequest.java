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

package com.adyen.mirakl.service.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import static com.mirakl.client.core.internal.util.DateFormatter.formatDate;

public class GetShopDocumentsRequest extends MiraklGetShopDocumentsRequest {
    private Date updatedSince;

    public GetShopDocumentsRequest() {
        super(new ArrayList<>());
    }

    @Override
    public Map<String, String> getQueryParams() {
        Map<String, String> result = super.getQueryParams();

        if (updatedSince != null) {
            result.put("updated_since", formatDate(updatedSince));
        }

        return result;
    }

    public Date getUpdatedSince() {
        return updatedSince;
    }

    public void setUpdatedSince(Date updatedSince) {
        this.updatedSince = updatedSince;
    }
}
