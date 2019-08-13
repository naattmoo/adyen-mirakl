Feature: Bank Account Verification

    @ADY-8 @ADY-77 @ADY-84 @ADY-102 @ADY-13 @ADY-100 @ADY-15 @ADY-89 @PW-439
    Scenario: ACCOUNT_HOLDER_VERIFICATION notification including a new BankAccountDetail is sent by Adyen upon providing Bank Account Details and editing IBAN.
    Seller uploads Bank Statement Mirakl to fulfil BANK_ACCOUNT_VERIFICATION in Adyen
        Given a shop has been created in Mirakl for an Individual with Bank Information
            | city   | bank name | iban                   | bankOwnerName | lastName |
            | PASSED | RBS       | GB26TEST40051512347366 | TestData      | TestData |
        When the connector processes the data and pushes to Adyen
        Then the ACCOUNT_HOLDER_VERIFICATION notification is sent by Adyen comprising of BANK_ACCOUNT_VERIFICATION and DATA_PROVIDED
        And a new bankAccountDetail will be created for the existing Account Holder
            | eventType              | iban                   |
            | ACCOUNT_HOLDER_CREATED | GB26TEST40051512347366 |
        When the IBAN has been modified in Mirakl
            | iban                   |
            | GB26TEST40051512393150 |
        And the connector processes the data and pushes to Adyen
        Then a new bankAccountDetail will be created for the existing Account Holder
            | eventType              | iban                   |
            | ACCOUNT_HOLDER_UPDATED | GB26TEST40051512393150 |
        And the previous BankAccountDetail will be removed
        When the seller uploads a Bank Statement in Mirakl
        And the connector processes the document data and push to Adyen
        And the document is successfully uploaded to Adyen
            | documentType   | filename          |
            | BANK_STATEMENT | BankStatement.png |
        When the ACCOUNT_HOLDER_VERIFICATION notification is sent by Adyen comprising of BANK_ACCOUNT_VERIFICATION and DATA_PROVIDED
        And the ACCOUNT_HOLDER_VERIFICATION notification is sent to the Connector

    @ADY-8 @ADY-71 @ADY-84 @ADY-104
    Scenario: New BankAccountDetail is created for Account Holder upon new IBAN entry in Mirakl for an existing Adyen accountHolder
        Given a seller creates a shop as a Individual without entering a bank account
            | lastName |
            | TestData |
        And the connector processes the data and pushes to Adyen
        And a new IBAN has been provided by the seller in Mirakl and the mandatory IBAN fields have been provided
        When the connector processes the data and pushes to Adyen
        Then a new bankAccountDetail will be created for the existing Account Holder
            | eventType              |
            | ACCOUNT_HOLDER_UPDATED |

    @PW-1534
    Scenario: New BankAccountDetail is created for Account Holder upon new Swedish IBAN and SEK currency entry in Mirakl for an existing Adyen accountHolder
        Given a shop has been created in Mirakl for an Business with Swedish Bank Information
            | city   | bank name | iban                     | bic      | bankOwnerName | lastName | maxUbos | currency  |
            | PASSED | testBank  | SE4550000000058398257466 | NDEASESE | TestData      | TestData | 4       | SEK       |
        When the connector processes the data and pushes to Adyen
        Then the ACCOUNT_HOLDER_VERIFICATION notification is sent by Adyen comprising of BANK_ACCOUNT_VERIFICATION and AWAITING_DATA
        And a new bankAccountDetail will be created for the existing Account Holder
            | eventType              | iban                     |
            | ACCOUNT_HOLDER_CREATED | SE4550000000058398257466 |
        When the IBAN has been modified in Mirakl
            | iban                     |
            | SE5293156958935141133616 |
        And the connector processes the data and pushes to Adyen
        Then a new bankAccountDetail will be created for the existing Account Holder
            | eventType              | iban                     |
            | ACCOUNT_HOLDER_UPDATED | SE5293156958935141133616 |
        And the previous BankAccountDetail will be removed
        When the seller uploads a Bank Statement in Mirakl
        And the connector processes the document data and push to Adyen
        And the document is successfully uploaded to Adyen
            | documentType   | filename          |
            | BANK_STATEMENT | BankStatement.png |
        When the ACCOUNT_HOLDER_VERIFICATION notification is sent by Adyen comprising of BANK_ACCOUNT_VERIFICATION and PASSED
        And the ACCOUNT_HOLDER_VERIFICATION notification is sent to the Connector

