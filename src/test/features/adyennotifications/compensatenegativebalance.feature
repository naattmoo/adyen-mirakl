Feature: Compensate negative balance for account

    @ADY-28
    Scenario: Successful manual invoice document created in mirakl after balance compensation in adyen
        Given a shop has been created in Mirakl for an Individual with mandatory KYC data
            | city   | bank name | iban                   | bankOwnerName | lastName |
            | PASSED | testBank  | GB26TEST40051512347366 | TestData      | TestData |
        And the connector processes the data and pushes to Adyen
        When a compensate negative balance notification is sent to the Connector
            | amount | currency |
            | -100   | EUR      |
        Then mirakl will create a manual credit document
