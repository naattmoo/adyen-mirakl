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
import java.util.Objects;

/**
 * A AdyenNotification.
 */
@Entity
@Table(name = "adyen_notification")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AdyenNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "raw_adyen_notification")
    private String rawAdyenNotification;

    @Column(name = "processed", nullable = false)
    private boolean processed;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRawAdyenNotification() {
        return rawAdyenNotification;
    }

    public AdyenNotification rawAdyenNotification(String rawAdyenNotification) {
        this.rawAdyenNotification = rawAdyenNotification;
        return this;
    }

    public void setRawAdyenNotification(String rawAdyenNotification) {
        this.rawAdyenNotification = rawAdyenNotification;
    }

    public boolean isProcessed() {
        return processed;
    }

    public AdyenNotification processed(boolean processed) {
        this.processed = processed;
        return this;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
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
        AdyenNotification adyenNotification = (AdyenNotification) o;
        if (adyenNotification.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), adyenNotification.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AdyenNotification{" +
            "id=" + getId() +
            ", rawAdyenNotification='" + getRawAdyenNotification() + "'" +
            ", processed='" + isProcessed() + "'" +
            "}";
    }
}
