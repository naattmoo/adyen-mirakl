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

import com.adyen.model.marketpay.notification.*;
import com.adyen.service.Notification;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdyenStartupValidatorTest {

    @InjectMocks
    private AdyenStartupValidator testObj;
    @Mock
    private ContextRefreshedEvent eventMock;
    @Mock
    private NotificationConfigurationDetails notificationFromAdyenMock1, notificationFromAdyenMock2, notificationFromAdyenMock3;
    @Mock
    private Notification adyenNotificationMock;
    @Mock
    private GetNotificationConfigurationListResponse getNotificationConfigurationListResponseMock;
    @Mock
    private UpdateNotificationConfigurationResponse updateNotificationConfigurationResponse1, updateNotificationConfigurationResponse2;
    @Mock
    private CreateNotificationConfigurationResponse createNotificationConfigurationResponseMock1, createNotificationConfigurationResponseMock2;
    @Captor
    private ArgumentCaptor<UpdateNotificationConfigurationRequest> updateNotificationConfigurationRequestCaptor;
    @Captor
    private ArgumentCaptor<CreateNotificationConfigurationRequest> createNotificationConfigurationRequestCaptor;

    @Before
    public void setUp(){
        final NotificationConfigurationDetails notificationConfigurationDetail1 = new NotificationConfigurationDetails();
        notificationConfigurationDetail1.setNotifyURL("notifyUrl1");
        final NotificationConfigurationDetails notificationConfigurationDetail2 = new NotificationConfigurationDetails();
        notificationConfigurationDetail2.setNotifyURL("notifyUrl2");
        final NotificationConfigurationDetails notificationConfigurationDetail3 = new NotificationConfigurationDetails();
        notificationConfigurationDetail3.setNotifyURL("notifyUrl4");
        final NotificationConfigurationDetails notificationConfigurationDetail4 = new NotificationConfigurationDetails();
        notificationConfigurationDetail4.setNotifyURL("notifyUrl5");
        testObj.setNotificationConfigurationDetails(ImmutableList.of(notificationConfigurationDetail1, notificationConfigurationDetail2, notificationConfigurationDetail3, notificationConfigurationDetail4));
    }


    @Test
    public void shouldSendUpdateWithCurrentConfigInApplicationYaml() throws Exception {
        when(notificationFromAdyenMock1.getNotifyURL()).thenReturn("notifyUrl1");
        when(notificationFromAdyenMock1.getNotificationId()).thenReturn(1L);
        when(notificationFromAdyenMock2.getNotifyURL()).thenReturn("notifyUrl2");
        when(notificationFromAdyenMock2.getNotificationId()).thenReturn(2L);
        //ignored as we cannot match it to our config
        when(notificationFromAdyenMock3.getNotifyURL()).thenReturn("notifyUrl3");
        when(notificationFromAdyenMock3.getNotificationId()).thenReturn(3L);

        when(adyenNotificationMock.getNotificationConfigurationList()).thenReturn(getNotificationConfigurationListResponseMock);
        when(getNotificationConfigurationListResponseMock.getConfigurations()).thenReturn(ImmutableList.of(notificationFromAdyenMock1, notificationFromAdyenMock2));

        when(adyenNotificationMock.updateNotificationConfiguration(updateNotificationConfigurationRequestCaptor.capture()))
            .thenReturn(updateNotificationConfigurationResponse1)
            .thenReturn(updateNotificationConfigurationResponse2);
        when(adyenNotificationMock.createNotificationConfiguration(createNotificationConfigurationRequestCaptor.capture()))
            .thenReturn(createNotificationConfigurationResponseMock1)
            .thenReturn(createNotificationConfigurationResponseMock2);

        when(updateNotificationConfigurationResponse1.getPspReference()).thenReturn("pspReference1");
        when(updateNotificationConfigurationResponse2.getPspReference()).thenReturn("pspReference2");
        when(createNotificationConfigurationResponseMock1.getPspReference()).thenReturn("pspReference3");
        when(createNotificationConfigurationResponseMock2.getPspReference()).thenReturn("pspReference4");

        testObj.onApplicationEvent(eventMock);

        final List<UpdateNotificationConfigurationRequest> allValuesToUpdate = updateNotificationConfigurationRequestCaptor.getAllValues();
        final UpdateNotificationConfigurationRequest updateNotificationConfigurationRequest1 = allValuesToUpdate.get(0);
        final UpdateNotificationConfigurationRequest updateNotificationConfigurationRequest2 = allValuesToUpdate.get(1);
        verify(adyenNotificationMock).updateNotificationConfiguration(updateNotificationConfigurationRequest1);
        verify(adyenNotificationMock).updateNotificationConfiguration(updateNotificationConfigurationRequest2);
        Assertions.assertThat(updateNotificationConfigurationRequest1.getConfigurationDetails().getNotificationId()).isEqualTo(1L);
        Assertions.assertThat(updateNotificationConfigurationRequest2.getConfigurationDetails().getNotificationId()).isEqualTo(2L);

        final List<CreateNotificationConfigurationRequest> allValuesToCreate = createNotificationConfigurationRequestCaptor.getAllValues();
        final CreateNotificationConfigurationRequest createNotificationConfigurationRequest1 = allValuesToCreate.get(0);
        final CreateNotificationConfigurationRequest createNotificationConfigurationRequest2 = allValuesToCreate.get(1);
        verify(adyenNotificationMock).createNotificationConfiguration(createNotificationConfigurationRequest1);
        verify(adyenNotificationMock).createNotificationConfiguration(createNotificationConfigurationRequest2);
        Assertions.assertThat(createNotificationConfigurationRequest1.getConfigurationDetails().getNotificationId()).isEqualTo(null);
        Assertions.assertThat(createNotificationConfigurationRequest2.getConfigurationDetails().getNotificationId()).isEqualTo(null);
    }

}
