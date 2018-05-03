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

package com.adyen.mirakl.startup;

import com.adyen.mirakl.domain.EmailError;
import com.adyen.mirakl.domain.ProcessEmail;
import com.adyen.mirakl.domain.enumeration.EmailState;
import com.adyen.mirakl.repository.EmailErrorsRepository;
import com.adyen.mirakl.repository.ProcessEmailRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ResetEmailsAtStartup implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ProcessEmailRepository processEmailRepository;
    @Resource
    private EmailErrorsRepository emailErrorsRepository;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        final List<ProcessEmail> processingEmails = processEmailRepository.findByState(EmailState.PROCESSING);
        processingEmails.forEach(email -> {
            final EmailError emailError = new EmailError();
            emailError.setError("Application shutdown");
            emailError.setProcessEmail(email);
            emailErrorsRepository.save(emailError);
            email.addEmailError(emailError);
            email.setState(EmailState.FAILED);

        });
        processEmailRepository.save(processingEmails);
        processEmailRepository.flush();
    }
}
