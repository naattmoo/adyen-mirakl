Feature: Account Holder Updated notification upon Mirakl shop changes

    @ADY-11 @ADY-71 @ADY-83
    Scenario: Updating Mirakl existing shop with contact details and verifying Adyen Account Holder Details are updated
        Given a seller creates a new shop as an Individual
            | lastName |
            | TestData |
        And the connector processes the data and pushes to Adyen
        And an AccountHolder will be created in Adyen with status Active
        When the Mirakl Shop Details have been updated
            | firstName | lastName | postCode | city       |
            | John      | Smith    | SE1 9GB  | Manchester |
        And the connector processes the data and pushes to Adyen
        Then a notification will be sent pertaining to ACCOUNT_HOLDER_UPDATED
        And the shop data is correctly mapped to the Adyen Account

    @ADY-11 @bug @ADY-149
    Scenario: ACCOUNT_HOLDER_UPDATED will not be invoked if no data has been changed
        Given a shop exists in Mirakl with the following fields
            | seller       | lastName | city       |
            | UpdateShop01 | Smith    | Manchester |
        When the Mirakl Shop Details have been updated as the same as before
            | lastName |
            | Smith    |
        And the connector processes the data and pushes to Adyen
        Then a notification of ACCOUNT_HOLDER_UPDATED will not be sent

    @PW-513
    Scenario: ACCOUNT_HOLDER_VERIFICATION notification including a new BankAccountDetail is sent by Adyen upon providing Bank Account Details and editing Account Number.
        Given a shop has been created in Mirakl for a Business with US Bank Information
            | city   | bank name | bankAccountNumber    | routingNumber |bankOwnerName | lastName | maxUbos | currency  | street                | state | zip   |
            | PASSED | testBank  | 123456789            | 121000358     |TestData      | TestData | 4       | USD       | 420 Montgomery Street |   CA  | 94104 |
        When the connector processes the data and pushes to Adyen
        Then the ACCOUNT_HOLDER_VERIFICATION notification is sent by Adyen comprising of BANK_ACCOUNT_VERIFICATION and PASSED
        And a new US bankAccountDetail will be created for the existing Account Holder
            | eventType              | bankAccountNumber| routingNumber |
            | ACCOUNT_HOLDER_CREATED | 123456789        | 121000358     |
        When the Bank Account Number has been modified in Mirakl
            | bankAccountNumber |
            | 987654321         |
        And the connector processes the data and pushes to Adyen
        And a new bankAccountDetail will be created for the existing Account Holder
            | eventType              | bankAccountNumber    |
            | ACCOUNT_HOLDER_UPDATED | 987654321            |
        And the previous BankAccountDetail will be removed
