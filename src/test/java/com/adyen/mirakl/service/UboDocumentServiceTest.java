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
 * Adyen Java API Library
 *
 * Copyright (c) 2019 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */

package com.adyen.mirakl.service;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.adyen.mirakl.domain.ShareholderMapping;
import com.adyen.mirakl.repository.ShareholderMappingRepository;
import com.adyen.mirakl.service.dto.UboDocumentDTO;
import com.adyen.model.marketpay.DocumentDetail;
import com.google.common.collect.ImmutableList;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UboDocumentServiceTest {

    @InjectMocks
    private UboDocumentService uboDocumentService;

    @Mock
    private ShareholderMappingRepository shareholderMappingRepositoryMock;
    @Mock
    private ShareholderMapping shareholderMappingMock1, shareholderMappingMock2, shareholderMappingMock3;
    @Mock
    private MiraklShopDocument miraklShopDocument1, miraklShopDocument2, miraklShopDocument3, miraklShopDocument4, miraklShopDocument5, miraklShopDocument6;
    @Mock
    private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;
    @Mock
    private MiraklShops miraklShops1, miraklShops2;
    @Mock
    private MiraklShop miraklShop1, miraklShop2;
    @Mock
    private MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue miraklAddtionalField1, miraklAddtionalField2, miraklAddtionalField3;

    @Captor
    private ArgumentCaptor<MiraklGetShopsRequest> miraklGetShopsRequestCaptor;

    @Test
    public void shouldExtractMiraklDocumentsRelatedToUbos() {
        uboDocumentService.setMaxUbos(4);

        //shop 1
        when(miraklShopDocument1.getTypeCode()).thenReturn("adyen-ubo1-photoid");//front passport used
        when(miraklShopDocument1.getShopId()).thenReturn("shop1");
        when(miraklShopDocument2.getTypeCode()).thenReturn("adyen-ubo1-photoid-rear");//rear passport picture ignored
        when(miraklShopDocument2.getShopId()).thenReturn("shop1");
        //shop 2
        when(miraklShopDocument3.getTypeCode()).thenReturn("adyen-ubo1-photoid");//id front used
        when(miraklShopDocument3.getShopId()).thenReturn("shop2");
        when(miraklShopDocument4.getTypeCode()).thenReturn("adyen-ubo1-photoid-rear");//rear id used
        when(miraklShopDocument4.getShopId()).thenReturn("shop2");
        when(miraklShopDocument5.getTypeCode()).thenReturn("adyen-ubo2-photoid");//front driving licence always mapped to front
        when(miraklShopDocument5.getShopId()).thenReturn("shop2");
        when(miraklShopDocument6.getTypeCode()).thenReturn("adyen-ubo2-photoid-rear");//rear driving licence always mapped to rear
        when(miraklShopDocument6.getShopId()).thenReturn("shop2");
        // result will be 4 documents sent to adyen
        // 1 passport                                                       - shop 1 ubo 1
        // 1 front id  & 1 back id                                          - shop 2 ubo 1
        // 1 front driving licence & 1 rear driving licence                 - shop 2 ubo 2

        when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestCaptor.capture())).thenReturn(miraklShops1).thenReturn(miraklShops2);
        when(miraklShops1.getShops()).thenReturn(ImmutableList.of(miraklShop1));
        when(miraklShops2.getShops()).thenReturn(ImmutableList.of(miraklShop2));
        when(miraklShop1.getAdditionalFieldValues()).thenReturn(ImmutableList.of(miraklAddtionalField1));
        when(miraklShop2.getAdditionalFieldValues()).thenReturn(ImmutableList.of(miraklAddtionalField2, miraklAddtionalField3));
        when(miraklAddtionalField1.getCode()).thenReturn("adyen-ubo1-photoidtype");
        when(miraklAddtionalField1.getValue()).thenReturn("PASSPORT");
        when(miraklAddtionalField2.getCode()).thenReturn("adyen-ubo1-photoidtype");
        when(miraklAddtionalField2.getValue()).thenReturn("ID_CARD");
        when(miraklAddtionalField3.getCode()).thenReturn("adyen-ubo2-photoidtype");
        when(miraklAddtionalField3.getValue()).thenReturn("DRIVING_LICENCE");
        when(shareholderMappingRepositoryMock.findOneByMiraklShopIdAndMiraklUboNumber("shop1", 1)).thenReturn(Optional.of(shareholderMappingMock1));
        when(shareholderMappingRepositoryMock.findOneByMiraklShopIdAndMiraklUboNumber("shop2", 1)).thenReturn(Optional.of(shareholderMappingMock2));
        when(shareholderMappingRepositoryMock.findOneByMiraklShopIdAndMiraklUboNumber("shop2", 2)).thenReturn(Optional.of(shareholderMappingMock3));
        when(shareholderMappingMock1.getAdyenShareholderCode()).thenReturn("shareholderCode1");
        when(shareholderMappingMock2.getAdyenShareholderCode()).thenReturn("shareholderCode2");
        when(shareholderMappingMock3.getAdyenShareholderCode()).thenReturn("shareholderCode3");

        final List<UboDocumentDTO> result = uboDocumentService.extractDocuments(ImmutableList.of(miraklShopDocument1,
                                                                                                 miraklShopDocument2,
                                                                                                 miraklShopDocument3,
                                                                                                 miraklShopDocument4,
                                                                                                 miraklShopDocument5,
                                                                                                 miraklShopDocument6));

        List<MiraklGetShopsRequest> requestsToMirakl = miraklGetShopsRequestCaptor.getAllValues();
        Assertions.assertThat(requestsToMirakl.size()).isEqualTo(3);
        Assertions.assertThat(requestsToMirakl.get(0).getShopIds()).containsOnly("shop1");
        Assertions.assertThat(requestsToMirakl.get(1).getShopIds()).containsOnly("shop2");
        Assertions.assertThat(result.size()).isEqualTo(5);
        Assertions.assertThat(result.get(0).getShareholderCode()).isEqualTo("shareholderCode1");
        Assertions.assertThat(result.get(0).getMiraklShopDocument().getShopId()).isEqualTo("shop1");
        Assertions.assertThat(result.get(0).getMiraklShopDocument().getTypeCode()).isEqualTo("adyen-ubo1-photoid");
        Assertions.assertThat(result.get(0).getDocumentTypeEnum()).isEqualTo(DocumentDetail.DocumentTypeEnum.PASSPORT);

        Assertions.assertThat(result.get(1).getShareholderCode()).isEqualTo("shareholderCode2");
        Assertions.assertThat(result.get(1).getMiraklShopDocument().getShopId()).isEqualTo("shop2");
        Assertions.assertThat(result.get(1).getMiraklShopDocument().getTypeCode()).isEqualTo("adyen-ubo1-photoid");
        Assertions.assertThat(result.get(1).getDocumentTypeEnum()).isEqualTo(DocumentDetail.DocumentTypeEnum.ID_CARD_FRONT);

        Assertions.assertThat(result.get(2).getShareholderCode()).isEqualTo("shareholderCode2");
        Assertions.assertThat(result.get(2).getMiraklShopDocument().getShopId()).isEqualTo("shop2");
        Assertions.assertThat(result.get(2).getMiraklShopDocument().getTypeCode()).isEqualTo("adyen-ubo1-photoid-rear");
        Assertions.assertThat(result.get(2).getDocumentTypeEnum()).isEqualTo(DocumentDetail.DocumentTypeEnum.ID_CARD_BACK);

        Assertions.assertThat(result.get(3).getShareholderCode()).isEqualTo("shareholderCode3");
        Assertions.assertThat(result.get(3).getMiraklShopDocument().getShopId()).isEqualTo("shop2");
        Assertions.assertThat(result.get(3).getMiraklShopDocument().getTypeCode()).isEqualTo("adyen-ubo2-photoid");
        Assertions.assertThat(result.get(3).getDocumentTypeEnum()).isEqualTo(DocumentDetail.DocumentTypeEnum.DRIVING_LICENCE_FRONT);

        Assertions.assertThat(result.get(4).getShareholderCode()).isEqualTo("shareholderCode3");
        Assertions.assertThat(result.get(4).getMiraklShopDocument().getShopId()).isEqualTo("shop2");
        Assertions.assertThat(result.get(4).getMiraklShopDocument().getTypeCode()).isEqualTo("adyen-ubo2-photoid-rear");
        Assertions.assertThat(result.get(4).getDocumentTypeEnum()).isEqualTo(DocumentDetail.DocumentTypeEnum.DRIVING_LICENCE_BACK);
    }

}
