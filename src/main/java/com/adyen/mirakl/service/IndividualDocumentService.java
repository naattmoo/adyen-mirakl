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
import org.springframework.stereotype.Service;
import com.adyen.mirakl.service.dto.IndividualDocumentDTO;
import com.adyen.model.marketpay.DocumentDetail;
import com.google.common.collect.ImmutableList;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;

@Service
public class IndividualDocumentService extends EntityDocumentService<IndividualDocumentDTO> {

    private static final String INDIVIDUAL_ENTITY = "individual";

    @Override
    public List<IndividualDocumentDTO> extractDocuments(List<MiraklShopDocument> miraklShopDocuments) {
        ImmutableList.Builder<IndividualDocumentDTO> builder = ImmutableList.builder();

        Map<String, String> internalMemoryForDocs = new HashMap<>();
        miraklShopDocuments.forEach(miraklShopDocument -> addToBuilder(builder, internalMemoryForDocs, miraklShopDocument, INDIVIDUAL_ENTITY, null));

        return builder.build();
    }

    @Override
    void addDocumentDTO(final ImmutableList.Builder<IndividualDocumentDTO> builder,
                                final MiraklShopDocument miraklShopDocument,
                                final Integer entitySequence,
                                final Map<Boolean, DocumentDetail.DocumentTypeEnum> documentTypeEnum) {
        final IndividualDocumentDTO individualDocumentDTO = new IndividualDocumentDTO();
        individualDocumentDTO.setDocumentTypeEnum(documentTypeEnum.values().iterator().next());
        individualDocumentDTO.setMiraklShopDocument(miraklShopDocument);
        builder.add(individualDocumentDTO);
    }
}
