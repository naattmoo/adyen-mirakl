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

package com.adyen.mirakl.domain;

import java.io.Serializable;
import java.util.Objects;


public class ShareHolderMappingPk implements Serializable {

    protected String miraklShopId;
    protected Integer miraklUboNumber;

    public ShareHolderMappingPk() {}

    public ShareHolderMappingPk(String miraklShopId, Integer miraklUboNumber) {
        this.miraklShopId = miraklShopId;
        this.miraklUboNumber = miraklUboNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShareHolderMappingPk that = (ShareHolderMappingPk) o;
        return Objects.equals(miraklShopId, that.miraklShopId) &&
            Objects.equals(miraklUboNumber, that.miraklUboNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(miraklShopId, miraklUboNumber);
    }
}
