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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.adyen.mirakl.service.dto.IndividualDocumentDTO;
import com.adyen.model.marketpay.DocumentDetail;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;

@Service
public class IndividualService {

    private static final Logger log = LoggerFactory.getLogger(IndividualService.class);

    private static final String ADYEN_INDIVIDUAL_PHOTOID = "adyen-individual-photoid";
    private static final String ADYEN_INDIVIDUAL_PHOTOID_REAR = "adyen-individual-photoid-rear";
    private static final String ADYEN_INDIVIDUAL_PHOTOID_TYPE = "adyen-individual-photoidtype";

    private static final String SUFFIX_FRONT = "_FRONT";
    private static final String SUFFIX_BACK = "_BACK";

    @Resource
    private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

    public List<IndividualDocumentDTO> extractIndividualDocuments(List<MiraklShopDocument> miraklDocuments) {
        ImmutableList.Builder<IndividualDocumentDTO> builder = ImmutableList.builder();
        Map<String, String> internalMemoryForDocs = new HashMap<>();

        miraklDocuments.forEach(miraklShopDocument -> addToBuilder(builder, internalMemoryForDocs, miraklShopDocument));

        return builder.build();
    }

    private void addToBuilder(ImmutableList.Builder<IndividualDocumentDTO> builder, Map<String, String> internalMemoryForDocs, MiraklShopDocument miraklShopDocument) {
        if (miraklShopDocument.getTypeCode().equalsIgnoreCase(ADYEN_INDIVIDUAL_PHOTOID)) {
            final Map<Boolean, DocumentDetail.DocumentTypeEnum> documentTypeEnum = findCorrectEnum(internalMemoryForDocs, miraklShopDocument, SUFFIX_FRONT);
            if (documentTypeEnum != null) {
                addIndividualDocumentDTO(builder, miraklShopDocument, documentTypeEnum);
            } else {
                log.info("DocumentType is not supported for individual shop: [{}], skipping individualDocument", miraklShopDocument.getShopId());
            }
        }
        if (miraklShopDocument.getTypeCode().equalsIgnoreCase(ADYEN_INDIVIDUAL_PHOTOID_REAR)) {
            final Map<Boolean, DocumentDetail.DocumentTypeEnum> documentTypeEnum = findCorrectEnum(internalMemoryForDocs, miraklShopDocument, SUFFIX_BACK);
            // If the enum + BACK_SUFFIX is not found as an enum then do not send it across
            if (documentTypeEnum != null && documentTypeEnum.keySet().iterator().next()) {
                addIndividualDocumentDTO(builder, miraklShopDocument, documentTypeEnum);
            } else if (documentTypeEnum != null) {
                log.info("DocumentType [{}] is not supported for individual shop: [{}], skipping individualDocument",
                         documentTypeEnum.values().iterator().next() + SUFFIX_BACK,
                         miraklShopDocument.getShopId());
            } else {
                log.warn("DocumentType is not supported for individual shop: [{}], skipping individualDocument, please check your documentTypes in your customfields settings on Mirakl",
                         miraklShopDocument.getShopId());
            }
        }
    }

    private void addIndividualDocumentDTO(final ImmutableList.Builder<IndividualDocumentDTO> builder,
                                          final MiraklShopDocument miraklShopDocument,
                                          final Map<Boolean, DocumentDetail.DocumentTypeEnum> documentTypeEnum) {
        final IndividualDocumentDTO individualDocumentDTO = new IndividualDocumentDTO();
        individualDocumentDTO.setDocumentTypeEnum(documentTypeEnum.values().iterator().next());
        individualDocumentDTO.setMiraklShopDocument(miraklShopDocument);
        builder.add(individualDocumentDTO);
    }

    private Map<Boolean, DocumentDetail.DocumentTypeEnum> findCorrectEnum(final Map<String, String> internalMemoryForDocs, final MiraklShopDocument miraklShopDocument, String suffix) {
        String documentType = retrieveIndividualPhotoIdType(miraklShopDocument.getShopId(), internalMemoryForDocs);
        if (documentType != null) {
            if (EnumUtils.isValidEnum(DocumentDetail.DocumentTypeEnum.class, documentType + suffix)) {
                return ImmutableMap.of(true, DocumentDetail.DocumentTypeEnum.valueOf(documentType + suffix));
            } else {
                return ImmutableMap.of(false, DocumentDetail.DocumentTypeEnum.valueOf(documentType));
            }
        }
        return null;
    }

    private String retrieveIndividualPhotoIdType(final String shopId, Map<String, String> internalMemory) {
        String documentTypeEnum = internalMemory.getOrDefault(shopId, null);
        if (documentTypeEnum == null) {
            String docTypeFromMirakl = getDocTypeFromMirakl(shopId);
            if (docTypeFromMirakl != null) {
                internalMemory.put(shopId, docTypeFromMirakl);
                return docTypeFromMirakl;
            }
        }
        return documentTypeEnum;
    }

    private String getDocTypeFromMirakl(String shopId) {
        MiraklGetShopsRequest request = new MiraklGetShopsRequest();
        request.setShopIds(ImmutableList.of(shopId));
        MiraklShops shops = miraklMarketplacePlatformOperatorApiClient.getShops(request);
        MiraklShop shop = shops.getShops().iterator().next();
        Optional<MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue> photoIdType = shop.getAdditionalFieldValues()
                                                                                                   .stream()
                                                                                                   .filter(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::isInstance)
                                                                                                   .map(MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue.class::cast)
                                                                                                   .filter(x -> ADYEN_INDIVIDUAL_PHOTOID_TYPE.equalsIgnoreCase(x.getCode()))
                                                                                                   .findAny();
        return photoIdType.map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue).orElse(null);
    }
}
