Feature: Money Transfer
    Scenario: Successful money transfer
        Given that the user with account id 1 has to transfer 200 Euro to another user having account id 2
        When the transfer is requested
        Then amount is deducted from sender's account
        And receiver receives the amount