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

package com.adyen.mirakl.aop;

import com.adyen.mirakl.AdyenMiraklConnectorApp;
import com.adyen.mirakl.domain.EmailError;
import com.adyen.mirakl.domain.ProcessEmail;
import com.adyen.mirakl.domain.enumeration.EmailState;
import com.adyen.mirakl.repository.EmailErrorsRepository;
import com.adyen.mirakl.repository.ProcessEmailRepository;
import liquibase.util.MD5Util;
import org.aspectj.lang.ProceedingJoinPoint;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdyenMiraklConnectorApp.class)
@Transactional
public class FailSafeEmailAspectTest {

    @Autowired
    private FailSafeEmailAspect failSafeEmailAspect;

    @Autowired
    private ProcessEmailRepository processEmailRepository;

    @Autowired
    private EmailErrorsRepository emailErrorsRepository;

    @Mock
    private ProceedingJoinPoint joingPointMock;

    private final String to = "to";
    private final String subject = "subject";
    private final String content = "content";
    private final boolean isMultipart = false;
    private final boolean isHtml = false;

    @Test
    public void shouldSaveEmailSuccessfully() throws Throwable {
        failSafeEmailAspect.logAround(joingPointMock, to, subject, content, isMultipart, isHtml);
        String emailIdentifier = MD5Util.computeMD5(to + subject + content + isMultipart + isHtml);
        final ProcessEmail existing = processEmailRepository.findOneByEmailIdentifier(emailIdentifier).orElse(null);
        Assertions.assertThat(existing).isNotNull();
        Assertions.assertThat(existing.getTo()).isEqualTo(to);
        Assertions.assertThat(existing.getSubject()).isEqualTo(subject);
        Assertions.assertThat(existing.getContent()).isEqualTo(content);
        Assertions.assertThat(existing.isMultipart()).isEqualTo(isMultipart);
        Assertions.assertThat(existing.isHtml()).isEqualTo(isHtml);
        Assertions.assertThat(existing.getState()).isEqualTo(EmailState.SENT);
        Assertions.assertThat(existing.getEmailErrors()).isEmpty();
    }

    @Test
    public void shouldSaveEmailSuccessfullyWithMultipleErrors() throws Throwable {

        when(joingPointMock.proceed())
            .thenThrow(new IllegalStateException("error1"))
            .thenThrow(new IllegalStateException("error2"));


        failSafeEmailAspect.logAround(joingPointMock, to, subject, content, isMultipart, isHtml);//error1
        failSafeEmailAspect.logAround(joingPointMock, to, subject, content, isMultipart, isHtml);//error2

        String emailIdentifier = MD5Util.computeMD5(to + subject + content + isMultipart + isHtml);
        final ProcessEmail existing = processEmailRepository.findOneByEmailIdentifier(emailIdentifier).orElse(null);
        Assertions.assertThat(existing).isNotNull();
        Assertions.assertThat(existing.getTo()).isEqualTo(to);
        Assertions.assertThat(existing.getSubject()).isEqualTo(subject);
        Assertions.assertThat(existing.getContent()).isEqualTo(content);
        Assertions.assertThat(existing.isMultipart()).isEqualTo(isMultipart);
        Assertions.assertThat(existing.isHtml()).isEqualTo(isHtml);
        Assertions.assertThat(existing.getState()).isEqualTo(EmailState.FAILED);
        final Set<EmailError> emailErrors = existing.getEmailErrors();
        Assertions.assertThat(emailErrors.size()).isEqualTo(2);
        final List<EmailError> allErrors = emailErrorsRepository.findAll();
        final List<String> errors = allErrors.stream().map(EmailError::getError).collect(Collectors.toList());
        Assertions.assertThat(errors).containsExactlyInAnyOrder("error1", "error2");
    }

    @Test
    public void shouldSaveMultipleEmailsSuccessfully() throws Throwable {
        when(joingPointMock.proceed()).thenThrow(new IllegalStateException("error1")).thenReturn(null);

        failSafeEmailAspect.logAround(joingPointMock, to, subject, content, isMultipart, isHtml);
        failSafeEmailAspect.logAround(joingPointMock, "anotherPerson", subject, content, isMultipart, isHtml);

        System.out.println(processEmailRepository.findAll().size());

        String emailIdentifier = MD5Util.computeMD5(to + subject + content + isMultipart + isHtml);
        final ProcessEmail existing = processEmailRepository.findOneByEmailIdentifier(emailIdentifier).orElse(null);
        Assertions.assertThat(existing).isNotNull();
        Assertions.assertThat(existing.getTo()).isEqualTo(to);
        Assertions.assertThat(existing.getSubject()).isEqualTo(subject);
        Assertions.assertThat(existing.getContent()).isEqualTo(content);
        Assertions.assertThat(existing.isMultipart()).isEqualTo(isMultipart);
        Assertions.assertThat(existing.isHtml()).isEqualTo(isHtml);
        Assertions.assertThat(existing.getState()).isEqualTo(EmailState.FAILED);
        final Set<EmailError> emailErrors = existing.getEmailErrors();
        Assertions.assertThat(emailErrors.size()).isEqualTo(1);
        final List<EmailError> allErrors = emailErrorsRepository.findAll();
        final List<String> errors = allErrors.stream().map(EmailError::getError).collect(Collectors.toList());
        Assertions.assertThat(errors).containsExactlyInAnyOrder("error1");

        String emailIdentifier2 = MD5Util.computeMD5("anotherPerson" + subject + content + isMultipart + isHtml);
        final ProcessEmail existing2 = processEmailRepository.findOneByEmailIdentifier(emailIdentifier2).orElse(null);
        Assertions.assertThat(existing2).isNotNull();
        Assertions.assertThat(existing2.getTo()).isEqualTo("anotherPerson");
        Assertions.assertThat(existing2.getSubject()).isEqualTo(subject);
        Assertions.assertThat(existing2.getContent()).isEqualTo(content);
        Assertions.assertThat(existing2.isMultipart()).isEqualTo(isMultipart);
        Assertions.assertThat(existing2.isHtml()).isEqualTo(isHtml);
        Assertions.assertThat(existing2.getState()).isEqualTo(EmailState.SENT);
        Assertions.assertThat(existing2.getEmailErrors()).isEmpty();
    }

}
