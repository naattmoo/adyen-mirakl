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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A ShareholderMapping.
 */
@Entity
@Table(name = "shareholder_mapping")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@IdClass(ShareHolderMappingPk.class )
public class ShareholderMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "mirakl_shop_id", nullable = false)
    private String miraklShopId;

    @Id
    @NotNull
    @Column(name = "mirakl_ubo_number", nullable = false)
    private Integer miraklUboNumber;

    @NotNull
    @Column(name = "adyen_shareholder_code", nullable = false)
    private String adyenShareholderCode;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getMiraklShopId() {
        return miraklShopId;
    }

    public ShareholderMapping miraklShopId(String miraklShopId) {
        this.miraklShopId = miraklShopId;
        return this;
    }

    public void setMiraklShopId(String miraklShopId) {
        this.miraklShopId = miraklShopId;
    }

    public Integer getMiraklUboNumber() {
        return miraklUboNumber;
    }

    public ShareholderMapping miraklUboNumber(Integer miraklUboNumber) {
        this.miraklUboNumber = miraklUboNumber;
        return this;
    }

    public void setMiraklUboNumber(Integer miraklUboNumber) {
        this.miraklUboNumber = miraklUboNumber;
    }

    public String getAdyenShareholderCode() {
        return adyenShareholderCode;
    }

    public ShareholderMapping adyenShareholderCode(String adyenShareholderCode) {
        this.adyenShareholderCode = adyenShareholderCode;
        return this;
    }

    public void setAdyenShareholderCode(String adyenShareholderCode) {
        this.adyenShareholderCode = adyenShareholderCode;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShareholderMapping shareholderMapping = (ShareholderMapping) o;
        if (shareholderMapping.getMiraklShopId() == null || getMiraklShopId() == null ||
            shareholderMapping.getMiraklUboNumber() == null || getMiraklUboNumber() == null) {
            return false;
        }
        return Objects.equals(getMiraklShopId()+getMiraklUboNumber(), shareholderMapping.getMiraklShopId()+shareholderMapping.getMiraklUboNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMiraklShopId()+getMiraklUboNumber());
    }

    @Override
    public String toString() {
        return "ShareholderMapping{" +
            ", miraklShopId='" + getMiraklShopId() + "'" +
            ", miraklUboNumber=" + getMiraklUboNumber() +
            ", adyenShareholderCode='" + getAdyenShareholderCode() + "'" +
            "}";
    }
}
