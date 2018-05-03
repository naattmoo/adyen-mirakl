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

package com.adyen.mirakl.repository;

import com.adyen.mirakl.domain.ShareholderMapping;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;


/**
 * Spring Data JPA repository for the ShareholderMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShareholderMappingRepository extends JpaRepository<ShareholderMapping, Long> {

    Optional<ShareholderMapping> findOneByMiraklShopIdAndMiraklUboNumber(String shopCode, Integer uboNumber);

    Optional<ShareholderMapping> findOneByAdyenShareholderCode(String adyenShareholderCode);

}
