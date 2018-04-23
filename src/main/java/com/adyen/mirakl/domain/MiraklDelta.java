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

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A MiraklDelta.
 */
@Entity
@Table(name = "mirakl_delta")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MiraklDelta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_delta")
    private ZonedDateTime shopDelta;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getShopDelta() {
        return shopDelta;
    }

    public MiraklDelta shopDelta(ZonedDateTime shopDelta) {
        this.shopDelta = shopDelta;
        return this;
    }

    public void setShopDelta(ZonedDateTime shopDelta) {
        this.shopDelta = shopDelta;
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
        MiraklDelta miraklDelta = (MiraklDelta) o;
        if (miraklDelta.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), miraklDelta.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MiraklDelta{" +
            "id=" + getId() +
            ", shopDelta='" + getShopDelta() + "'" +
            "}";
    }
}
