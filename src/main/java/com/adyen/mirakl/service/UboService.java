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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Resource;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.adyen.mirakl.domain.ShareholderMapping;
import com.adyen.mirakl.domain.StreetDetails;
import com.adyen.mirakl.repository.ShareholderMappingRepository;
import com.adyen.mirakl.service.util.IsoUtil;
import com.adyen.mirakl.service.util.MiraklDataExtractionUtil;
import com.adyen.model.Address;
import com.adyen.model.Name;
import com.adyen.model.marketpay.GetAccountHolderResponse;
import com.adyen.model.marketpay.PersonalData;
import com.adyen.model.marketpay.PhoneNumber;
import com.adyen.model.marketpay.ShareholderContact;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;

@Service
public class UboService {

    private static final Logger log = LoggerFactory.getLogger(UboService.class);

    private static final String ADYEN_UBO = "adyen-ubo";

    private static final String CIVILITY = "civility";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String EMAIL = "email";
    private static final String DATE_OF_BIRTH = "dob";
    private static final String NATIONALITY = "nationality";
    private static final String ID_NUMBER = "idnumber";
    private static final String HOUSE_NUMBER_OR_NAME = "housenumber";
    private static final String STREET = "streetname";
    private static final String CITY = "city";
    private static final String POSTAL_CODE = "zip";
    private static final String COUNTRY = "country";
    private static final String PHONE_COUNTRY_CODE = "phonecountry";
    private static final String PHONE_TYPE = "phonetype";
    private static final String PHONE_NUMBER = "phonenumber";
    private static final String STATE_OR_PROVINCE = "stateorprovince";

    final static Map<String, Name.GenderEnum> CIVILITY_TO_GENDER = ImmutableMap.<String, Name.GenderEnum>builder().put("MR", Name.GenderEnum.MALE)
                                                                                                                         .put("MRS", Name.GenderEnum.FEMALE)
                                                                                                                         .put("MISS", Name.GenderEnum.FEMALE)
                                                                                                                         .build();

    @Value("${shopService.maxUbos}")
    private Integer maxUbos = 4;

    @Resource
    private ShareholderMappingRepository shareholderMappingRepository;

    @Resource
    private Map<String, Pattern> houseNumberPatterns;

    @Value("${miraklOperator.miraklTimeZone}")
    private String miraklTimeZone;

    /**
     * Extract shareholder contact data in a adyen format from a mirakl shop
     *
     * @param shop mirakl shop
     * @return share holder contacts to send to adyen
     */
    public List<ShareholderContact> extractUbos(final MiraklShop shop, final GetAccountHolderResponse existingAccountHolder) {
        Map<String, String> extractedKeysFromMirakl = extractKeysFromMirakl(shop);

        ImmutableList.Builder<ShareholderContact> builder = ImmutableList.builder();
        generateMiraklUboKeys(maxUbos).forEach((uboNumber, uboKeys) -> {
            String civility = extractedKeysFromMirakl.getOrDefault(uboKeys.get(CIVILITY), null);
            String firstName = extractedKeysFromMirakl.getOrDefault(uboKeys.get(FIRSTNAME), null);
            String lastName = extractedKeysFromMirakl.getOrDefault(uboKeys.get(LASTNAME), null);
            String email = extractedKeysFromMirakl.getOrDefault(uboKeys.get(EMAIL), null);
            String dateOfBirth = extractedKeysFromMirakl.getOrDefault(uboKeys.get(DATE_OF_BIRTH), null);
            String nationality = extractedKeysFromMirakl.getOrDefault(uboKeys.get(NATIONALITY), null);
            String idNumber = extractedKeysFromMirakl.getOrDefault(uboKeys.get(ID_NUMBER), null);
            String houseNumberOrName = extractedKeysFromMirakl.getOrDefault(uboKeys.get(HOUSE_NUMBER_OR_NAME), null);
            String street = extractedKeysFromMirakl.getOrDefault(uboKeys.get(STREET), null);
            String city = extractedKeysFromMirakl.getOrDefault(uboKeys.get(CITY), null);
            String postalCode = extractedKeysFromMirakl.getOrDefault(uboKeys.get(POSTAL_CODE), null);
            String country = extractedKeysFromMirakl.getOrDefault(uboKeys.get(COUNTRY), null);
            String phoneCountryCode = extractedKeysFromMirakl.getOrDefault(uboKeys.get(PHONE_COUNTRY_CODE), null);
            String phoneType = extractedKeysFromMirakl.getOrDefault(uboKeys.get(PHONE_TYPE), null);
            String phoneNumber = extractedKeysFromMirakl.getOrDefault(uboKeys.get(PHONE_NUMBER), null);
            String stateOrProvince = extractedKeysFromMirakl.getOrDefault(uboKeys.get(STATE_OR_PROVINCE), null);

            //do nothing if mandatory fields are missing
            if (allMandatoryDataIsAvailable(civility, firstName, lastName, email)) {
                ShareholderContact shareholderContact = new ShareholderContact();
                addShareholderCode(shop, uboNumber, shareholderContact, existingAccountHolder);
                addMandatoryData(civility, firstName, lastName, email, shareholderContact);
                addPersonalData(uboNumber, dateOfBirth, nationality, idNumber, shareholderContact);
                String shopCountry = shop.getContactInformation().getCountry();
                addAddressData(uboNumber, houseNumberOrName, street, city, postalCode, country, stateOrProvince, shareholderContact, shopCountry);
                addPhoneData(uboNumber, phoneCountryCode, phoneType, phoneNumber, shareholderContact);
                builder.add(shareholderContact);
            }
        });
        return builder.build();
    }

    private boolean allMandatoryDataIsAvailable(final String civility, final String firstName, final String lastName, final String email) {
        return firstName != null && lastName != null && civility != null && email != null;
    }

    private Map<String, String> extractKeysFromMirakl(final MiraklShop shop) {
        return shop.getAdditionalFieldValues()
                   .stream()
                   .filter(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue.class::isInstance)
                   .map(MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue.class::cast)
                   .collect(Collectors.toMap(MiraklAdditionalFieldValue::getCode, MiraklAdditionalFieldValue.MiraklAbstractAdditionalFieldWithSingleValue::getValue));
    }


    public Set<Integer> extractUboNumbersFromShop(final MiraklShop miraklShop) {
        Map<String, String> extractedKeysFromMirakl = extractKeysFromMirakl(miraklShop);
        final ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();
        generateMiraklUboKeys(maxUbos).forEach((uboNumber, uboKeys) -> {
            String civility = extractedKeysFromMirakl.getOrDefault(uboKeys.get(CIVILITY), null);
            String firstName = extractedKeysFromMirakl.getOrDefault(uboKeys.get(FIRSTNAME), null);
            String lastName = extractedKeysFromMirakl.getOrDefault(uboKeys.get(LASTNAME), null);
            String email = extractedKeysFromMirakl.getOrDefault(uboKeys.get(EMAIL), null);
            if (allMandatoryDataIsAvailable(civility, firstName, lastName, email)) {
                builder.add(uboNumber);
            }
        });
        return builder.build();
    }

    public List<ShareholderContact> extractUbos(final MiraklShop shop) {
        return extractUbos(shop, null);
    }

    private void addShareholderCode(final MiraklShop shop, final Integer uboNumber, final ShareholderContact shareholderContact, final GetAccountHolderResponse existingAccountHolder) {
        final Optional<ShareholderMapping> mapping = shareholderMappingRepository.findOneByMiraklShopIdAndMiraklUboNumber(shop.getId(), uboNumber);
        mapping.ifPresent(shareholderMapping -> shareholderContact.setShareholderCode(shareholderMapping.getAdyenShareholderCode()));
        if (! mapping.isPresent()
                && existingAccountHolder != null
                && existingAccountHolder.getAccountHolderDetails() != null
                && existingAccountHolder.getAccountHolderDetails().getBusinessDetails() != null
                && ! CollectionUtils.isEmpty(existingAccountHolder.getAccountHolderDetails().getBusinessDetails().getShareholders())) {
            final List<ShareholderContact> shareholders = existingAccountHolder.getAccountHolderDetails().getBusinessDetails().getShareholders();
            if (uboNumber - 1 < shareholders.size()) {
                final String shareholderCode = shareholders.get(uboNumber - 1).getShareholderCode();
                if (mappingDoesNotAlreadyExist(shareholderCode)) {
                    final ShareholderMapping shareholderMapping = new ShareholderMapping();
                    shareholderMapping.setAdyenShareholderCode(shareholderCode);
                    shareholderMapping.setMiraklShopId(shop.getId());
                    shareholderMapping.setMiraklUboNumber(uboNumber);
                    shareholderMappingRepository.saveAndFlush(shareholderMapping);
                    shareholderContact.setShareholderCode(shareholderCode);
                }
            }
        }
    }

    private boolean mappingDoesNotAlreadyExist(final String shareholderCode) {
        return ! shareholderMappingRepository.findOneByAdyenShareholderCode(shareholderCode).isPresent();
    }

    private void addMandatoryData(final String civility, final String firstName, final String lastName, final String email, final ShareholderContact shareholderContact) {
        Name name = new Name();
        name.setGender(CIVILITY_TO_GENDER.getOrDefault(civility.toUpperCase(), Name.GenderEnum.UNKNOWN));
        name.setFirstName(firstName);
        name.setLastName(lastName);
        shareholderContact.setName(name);
        shareholderContact.setEmail(email);
    }

    private void addPhoneData(final Integer uboNumber, final String phoneCountryCode, final String phoneType, final String phoneNumber, final ShareholderContact shareholderContact) {
        if (phoneNumber != null || phoneType != null || phoneCountryCode != null) {
            final PhoneNumber phoneNumberWrapper = new PhoneNumber();
            Optional.ofNullable(phoneCountryCode).ifPresent(phoneNumberWrapper::setPhoneCountryCode);
            Optional.ofNullable(phoneNumber).ifPresent(phoneNumberWrapper::setPhoneNumber);
            Optional.ofNullable(phoneType).ifPresent(x -> phoneNumberWrapper.setPhoneType(PhoneNumber.PhoneTypeEnum.valueOf(x.toUpperCase())));
            shareholderContact.setPhoneNumber(phoneNumberWrapper);
        } else {
            log.warn("Unable to populate any phone data for share holder {}", uboNumber);
        }
    }

    private void addAddressData(final Integer uboNumber,
                                final String houseNumberOrName,
                                final String street,
                                final String city,
                                final String postalCode,
                                final String country,
                                final String stateOrProvince,
                                final ShareholderContact shareholderContact,
                                final String contactCountry) {
        if (country != null || street != null || houseNumberOrName != null || city != null || postalCode != null) {
            final Address address = new Address();

            StreetDetails streetDetails = StreetDetails.createStreetDetailsFromSingleLine(houseNumberOrName, street, houseNumberPatterns.get(IsoUtil.getIso2CountryCodeFromIso3(contactCountry)));

            address.setStreet(streetDetails.getStreetName());
            address.setHouseNumberOrName(streetDetails.getHouseNumberOrName());

            Optional.ofNullable(city).ifPresent(address::setCity);
            Optional.ofNullable(postalCode).ifPresent(address::setPostalCode);
            Optional.ofNullable(country).ifPresent(address::setCountry);
            Optional.ofNullable(stateOrProvince).ifPresent(address::setStateOrProvince);
            shareholderContact.setAddress(address);
        } else {
            log.warn("Unable to populate any address data for share holder {}", uboNumber);
        }
    }

    private void addPersonalData(final Integer uboNumber, final String dateOfBirth, final String nationality, final String idNumber, final ShareholderContact shareholderContact) {
        if (dateOfBirth != null || nationality != null || idNumber != null) {
            final PersonalData personalData = new PersonalData();

            if (dateOfBirth != null && ! dateOfBirth.isEmpty()) {
                DateTime dateTime = MiraklDataExtractionUtil.formatCustomDateField(dateOfBirth, miraklTimeZone);
                org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                personalData.setDateOfBirth(dateTime.toString(formatter));
            }

            Optional.ofNullable(nationality).ifPresent(personalData::setNationality);
            Optional.ofNullable(idNumber).ifPresent(personalData::setIdNumber);
            shareholderContact.setPersonalData(personalData);
        } else {
            log.warn("Unable to populate any personal data for share holder {}", uboNumber);
        }
    }

    /**
     * generate mirakl ubo keys
     *
     * @param maxUbos number of ubos in mirakl e.g. 4
     * @return returns ubo numbers linked to their keys
     */
    public Map<Integer, Map<String, String>> generateMiraklUboKeys(Integer maxUbos) {
        return IntStream.rangeClosed(1, maxUbos).mapToObj(i -> {
            final Map<Integer, Map<String, String>> grouped = new HashMap<>();
            grouped.put(i,
                        new ImmutableMap.Builder<String, String>().put(CIVILITY, ADYEN_UBO + String.valueOf(i) + "-civility")
                                                                  .put(FIRSTNAME, ADYEN_UBO + String.valueOf(i) + "-firstname")
                                                                  .put(LASTNAME, ADYEN_UBO + String.valueOf(i) + "-lastname")
                                                                  .put(EMAIL, ADYEN_UBO + String.valueOf(i) + "-email")
                                                                  .put(DATE_OF_BIRTH, ADYEN_UBO + String.valueOf(i) + "-dob")
                                                                  .put(NATIONALITY, ADYEN_UBO + String.valueOf(i) + "-nationality")
                                                                  .put(ID_NUMBER, ADYEN_UBO + String.valueOf(i) + "-idnumber")
                                                                  .put(HOUSE_NUMBER_OR_NAME, ADYEN_UBO + String.valueOf(i) + "-housenumber")
                                                                  .put(STREET, ADYEN_UBO + String.valueOf(i) + "-streetname")
                                                                  .put(CITY, ADYEN_UBO + String.valueOf(i) + "-city")
                                                                  .put(POSTAL_CODE, ADYEN_UBO + String.valueOf(i) + "-zip")
                                                                  .put(COUNTRY, ADYEN_UBO + String.valueOf(i) + "-country")
                                                                  .put(PHONE_COUNTRY_CODE, ADYEN_UBO + String.valueOf(i) + "-phonecountry")
                                                                  .put(PHONE_TYPE, ADYEN_UBO + String.valueOf(i) + "-phonetype")
                                                                  .put(PHONE_NUMBER, ADYEN_UBO + String.valueOf(i) + "-phonenumber")
                                                                  .put(STATE_OR_PROVINCE, ADYEN_UBO + String.valueOf(i) + "-stateorprovince")
                                                                  .build());
            return grouped;
        }).reduce((x, y) -> {
            x.put(y.entrySet().iterator().next().getKey(), y.entrySet().iterator().next().getValue());
            return x;
        }).orElseThrow(() -> new IllegalStateException("UBOs must exist, number found: " + maxUbos));
    }

    public void setMaxUbos(final Integer maxUbos) {
        this.maxUbos = maxUbos;
    }

    public void setHouseNumberPatterns(final Map<String, Pattern> houseNumberPatterns) {
        this.houseNumberPatterns = houseNumberPatterns;
    }
}
