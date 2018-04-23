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
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.event.ContextRefreshedEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResetEmailsAtStartupTest {

    @InjectMocks
    private ResetEmailsAtStartup testObj;

    @Mock
    private ProcessEmailRepository processEmailRepository;
    @Mock
    private EmailErrorsRepository emailErrorsRepository;
    @Mock
    private ContextRefreshedEvent eventMock;
    @Mock
    private ProcessEmail emailMock1, emailMock2;
    @Captor
    private ArgumentCaptor<EmailError> emailErrorCaptor1, emailErrorCaptor2;

    @Test
    public void shouldResetAllEmailsWithNewError(){
        when(processEmailRepository.findByState(EmailState.PROCESSING)).thenReturn(ImmutableList.of(emailMock1, emailMock2));

        testObj.onApplicationEvent(eventMock);

        verify(emailMock1).setState(EmailState.FAILED);
        verify(emailMock1).addEmailError(emailErrorCaptor1.capture());
        verify(emailMock2).setState(EmailState.FAILED);
        verify(emailMock2).addEmailError(emailErrorCaptor2.capture());

        final EmailError error1 = emailErrorCaptor1.getValue();
        Assertions.assertThat(error1.getError()).isEqualTo("Application shutdown");
        Assertions.assertThat(error1.getProcessEmail()).isEqualTo(emailMock1);

        final EmailError error2 = emailErrorCaptor2.getValue();
        Assertions.assertThat(error2.getError()).isEqualTo("Application shutdown");
        Assertions.assertThat(error2.getProcessEmail()).isEqualTo(emailMock2);


        verify(emailErrorsRepository).save(error1);
        verify(emailErrorsRepository).save(error2);
        verify(processEmailRepository).save(ImmutableList.of(emailMock1, emailMock2));
        verify(processEmailRepository).flush();
    }


}
