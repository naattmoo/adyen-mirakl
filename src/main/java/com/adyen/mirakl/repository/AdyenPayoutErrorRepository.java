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

import java.util.List;
import com.adyen.mirakl.domain.AdyenPayoutError;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the AdyenPayoutError entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdyenPayoutErrorRepository extends JpaRepository<AdyenPayoutError, Long> {

    @Query("select e from AdyenPayoutError as e where e.processing = 0 and e.retry < ?1")
    List<AdyenPayoutError> findByRetry(Integer retry);

    @Query("select e from AdyenPayoutError as e where e.processing = 0 and e.accountHolderCode = ?1")
    List<AdyenPayoutError> findByAccountHolderCode(String accountHolderCode);

}
