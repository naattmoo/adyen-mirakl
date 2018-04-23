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

import com.adyen.mirakl.domain.ProcessEmail;
import com.adyen.mirakl.domain.enumeration.EmailState;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data JPA repository for the ProcessEmail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProcessEmailRepository extends JpaRepository<ProcessEmail, Long> {

    @Query("select distinct e from ProcessEmail as e left join fetch e.emailErrors where e.emailIdentifier = ?1")
    Optional<ProcessEmail> findOneByEmailIdentifier(String emailIdentifier);

    @Query("select distinct e from ProcessEmail as e left join fetch e.emailErrors where e.state = ?1")
    List<ProcessEmail> findByState(EmailState emailState);

}
