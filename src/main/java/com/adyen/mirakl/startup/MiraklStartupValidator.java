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

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import com.adyen.mirakl.config.ApplicationProperties;
import com.mirakl.client.mmp.domain.additionalfield.AbstractMiraklAdditionalField;
import com.mirakl.client.mmp.domain.additionalfield.MiraklFrontOperatorAdditionalField;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.request.additionalfield.MiraklGetAdditionalFieldRequest;

@Component
public class MiraklStartupValidator implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger log = LoggerFactory.getLogger(MiraklStartupValidator.class);

    private static String ADYEN_LEGAL_ENTITY_TYPE_NAME = "adyen-legal-entity-type";

    public enum CustomMiraklFields {
        ADYEN_LEGAL_ENTITY_TYPE(ADYEN_LEGAL_ENTITY_TYPE_NAME),
        ADYEN_BUSINESS_HOUSENUMBER("adyen-business-housenumber"),
        ADYEN_BUSINESS_REGISTEREDNAME("adyen-business-registeredname"),
        ADYEN_INDIVIDUAL_HOUSENUMBER("adyen-individual-housenumber"),
        ADYEN_INDIVIDUAL_DOB("adyen-individual-dob"),
        ADYEN_INDIVIDUAL_IDNUMBER("adyen-individual-idnumber");


        private final String name;

        CustomMiraklFields(String s) {
            name = s;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum AdyenLegalEntityType {
        INDIVIDUAL("INDIVIDUAL"),
        BUSINESS("BUSINESS");

        private final String name;

        AdyenLegalEntityType(String s) {
            name = s;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum RequiredCustomMiraklFields {

        ADYEN_LEGAL_ENTITY_TYPE(ADYEN_LEGAL_ENTITY_TYPE_NAME);

        private final String name;

        RequiredCustomMiraklFields(String s) {
            name = s;
        }

        @Override
        public String toString() {
            return this.name;
        }


    }

    @Resource
    private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;
    @Resource
    private ApplicationProperties applicationProperties;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        MiraklGetAdditionalFieldRequest miraklGetAdditionalFieldRequest = new MiraklGetAdditionalFieldRequest();
        final List<MiraklFrontOperatorAdditionalField> additionalFields = miraklMarketplacePlatformOperatorApiClient.getAdditionalFields(miraklGetAdditionalFieldRequest);

        for (RequiredCustomMiraklFields requiredCustomMiraklFields : RequiredCustomMiraklFields.values()) {
            boolean startupSuccess = additionalFields.stream().map(AbstractMiraklAdditionalField::getCode).anyMatch(customFieldName -> requiredCustomMiraklFields.toString().equalsIgnoreCase(customFieldName));
            if (startupSuccess) {
                log.info(String.format("Startup Mirkal validation succeeded, custom field found: [%s]", requiredCustomMiraklFields.toString()));
            } else {
                throw new IllegalStateException(String.format("Startup Mirkal validation failed, unable to find custom field: [%s]", requiredCustomMiraklFields.toString()));
            }
        }
    }

}
