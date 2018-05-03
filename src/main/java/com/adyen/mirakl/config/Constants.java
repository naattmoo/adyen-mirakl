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

package com.adyen.mirakl.config;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SYSTEM_ACCOUNT = "system";

    public static final String BANKPROOF = "adyen-bankproof";

    public final class Messages {
        public static final String EMAIL_ACCOUNT_HOLDER_VALIDATION_TITLE = "email.account.holder.validation.title";
        public static final String EMAIL_ACCOUNT_HOLDER_PAYOUT_FAILED_TITLE = "email.account.holder.payout.failed.title";
        public static final String EMAIL_TRANSFER_FUND_FAILED_TITLE = "email.transfer.fund.failed.title";
        public static final String EMAIL_MANUAL_CREDIT_DOCUMENT_FAILED_TITLE = "email.manual.credit.document.failed.title";

        private Messages() {
        }
    }

    private Constants() {
    }
}
