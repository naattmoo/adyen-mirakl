@smoke
Feature: Smoke test

    Scenario: Ping test to ensure endpoint is receiving notifications
        Given a shop exists in Mirakl
            | seller       |
            | UpdateShop01 |
        When the Mirakl Shop Details have been changed
        And the connector processes the data and pushes to Adyen
        Then the ACCOUNT_HOLDER_UPDATED will be sent by Adyen

    Scenario Outline: Top up default sourceAccountCode
        When a payment of <amount> <currency> has been authorised
        Then the payment is captured
        Examples:
            | amount | currency |
            | 10000  | EUR      |
            | 10000  | GBP      |
