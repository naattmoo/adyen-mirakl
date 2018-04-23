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

package com.adyen.mirakl.service;

import com.adyen.mirakl.AdyenMiraklConnectorApp;
import com.adyen.mirakl.domain.EmailError;
import com.adyen.mirakl.domain.ProcessEmail;
import com.adyen.mirakl.domain.enumeration.EmailState;
import com.adyen.mirakl.repository.EmailErrorsRepository;
import com.adyen.mirakl.repository.ProcessEmailRepository;
import liquibase.util.MD5Util;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdyenMiraklConnectorApp.class)
@Transactional
public class RetryEmailServiceTest {

    @Autowired
    private RetryEmailService retryEmailService;

    @Autowired
    private ProcessEmailRepository processEmailRepository;

    @Autowired
    private EmailErrorsRepository emailErrorsRepository;

    @MockBean
    private MailService mailService;

    @Test
    public void shouldRetryWithOnlyFailures(){
        createProcessEmail("to1", "subject", "content", false, false, EmailState.PROCESSING);
        createProcessEmail("to2", "subject", "content", false, false, EmailState.FAILED);
        createProcessEmail("to3", "subject", "content", false, false, EmailState.SENT);
        createProcessEmail("to4", "subject", "content", false, false, EmailState.FAILED);

        retryEmailService.retryFailedEmails();

        verify(mailService, never()).sendEmail("to1", "subject", "content", false, false);
        verify(mailService).sendEmail("to2", "subject", "content", false, false);
        verify(mailService, never()).sendEmail("to3", "subject", "content", false, false);
        verify(mailService).sendEmail("to4", "subject", "content", false, false);
        verify(mailService, never()).sendEmail("to5", "subject", "content", false, false);
    }

    @Test
    public void shouldNotRetry(){
        retryEmailService.retryFailedEmails();

        verify(mailService, never()).sendEmail(anyString(), anyString(), anyString(), anyBoolean(), anyBoolean());
    }

    @Test
    public void shouldRemoveSentEmailsAndErrors(){
        String subject = "subject";
        String content = "content";
        final ProcessEmail processEmail = createProcessEmail("to1", subject, content, false, false, EmailState.SENT);

        final EmailError emailError1 = new EmailError();
        final EmailError emailError2 = new EmailError();
        final EmailError emailError3 = new EmailError();
        emailError1.setError("error1");
        emailError2.setError("error2");
        emailError3.setError("error3");
        processEmail.addEmailError(emailError1);
        processEmail.addEmailError(emailError2);
        processEmail.addEmailError(emailError3);
        emailErrorsRepository.saveAndFlush(emailError1);
        emailErrorsRepository.saveAndFlush(emailError2);
        emailErrorsRepository.saveAndFlush(emailError3);

        processEmailRepository.saveAndFlush(processEmail);

        final ProcessEmail processEmail2 = createProcessEmail("to2", subject, content, false, false, EmailState.FAILED);
        final EmailError emailError4 = new EmailError();
        emailError4.setError("error4");

        processEmail2.addEmailError(emailError4);
        emailErrorsRepository.saveAndFlush(emailError4);
        processEmailRepository.saveAndFlush(processEmail2);

        createProcessEmail("to3", subject, content, false, false, EmailState.PROCESSING);
        createProcessEmail("to4", subject, content, false, false, EmailState.SENT);

        retryEmailService.removeSentEmails();

        final List<ProcessEmail> all = processEmailRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        String emailId1 = MD5Util.computeMD5("to2" + subject + content + false + false);
        final ProcessEmail remaining1 = processEmailRepository.findOneByEmailIdentifier(emailId1).orElse(null);

        String emailId2 = MD5Util.computeMD5("to3" + subject + content + false + false);
        final ProcessEmail remaining2 = processEmailRepository.findOneByEmailIdentifier(emailId2).orElse(null);
        Assertions.assertThat(remaining1).isNotNull();
        Assertions.assertThat(remaining1.getState()).isEqualTo(EmailState.FAILED);
        Assertions.assertThat(remaining1.getEmailErrors().size()).isOne();

        Assertions.assertThat(remaining2).isNotNull();
        Assertions.assertThat(remaining2.getState()).isEqualTo(EmailState.PROCESSING);

        final List<EmailError> allErrors = emailErrorsRepository.findAll();
        Assertions.assertThat(allErrors.size()).isOne();
        Assertions.assertThat(allErrors.iterator().next().getError()).isEqualTo("error4");
    }


    public ProcessEmail createProcessEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml, EmailState emailState){
        final ProcessEmail processEmail = new ProcessEmail();
        processEmail.setTo(to);
        processEmail.setSubject(subject);
        processEmail.setContent(content);
        processEmail.setMultipart(isMultipart);
        processEmail.setHtml(isHtml);
        processEmail.setState(emailState);
        processEmail.setEmailIdentifier(MD5Util.computeMD5(to + subject + content + isMultipart + isHtml));
        return processEmailRepository.saveAndFlush(processEmail);
    }

}
