# Adyen Mirakl Connector

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/Adyen/adyen-mirakl.svg?branch=develop)](https://travis-ci.org/Adyen/adyen-mirakl)


The Adyen Mirakl Connector integrates a Mirakl Marketplace Platform Instance with an Adyen MarketPay Merchant Account.

* The Connector manages the transfer of Shop data from Mirakl to create and update Account Holders in the MarketPay for purpose of Financial Onboarding and KYC Checks.
* The Connector manages the Payout of Sellers through MarketPay when Mirakl Posts the Payout Voucher to the Connector.



## Documentation
We have a [Wiki](https://github.com/e2y/adyen-mirakl/wiki) for more detailed information.


## Development
This application was generated using JHipster 4.14.0, you can find documentation and help at [http://www.jhipster.tech/documentation-archive/v4.14.0](http://www.jhipster.tech/documentation-archive/v4.14.0).

Please add environment variables for `MIRAKL_SDK_USER`, `MIRAKL_SDK_PASSWORD`, `MIRAKL_ENV_URL`, `MIRAKL_API_OPERATOR_KEY` and `MIRAKL_OPERATOR_EMAIL` e.g.
update `~/.bashrc` with:
```
export MIRAKL_SDK_USER=<user>
export MIRAKL_SDK_PASSWORD=<pass>
export MIRAKL_ENV_URL=<miraklEnvUrl>
export MIRAKL_API_OPERATOR_KEY=<miraklApiOperatorKey>
export MIRAKL_OPERATOR_EMAIL=<miraklOperatorEmail>
```

Same goes for Adyen: `ADYEN_USER_NAME`, `ADYEN_PASS`, `ADYEN_ENV`, `ADYEN_NOTIFY_URL` AND `ADYEN_LIABLE_ACCOUNT_CODE`. 
```
export ADYEN_USER_NAME=<user>
export ADYEN_PASS=<pass>
export ADYEN_ENV=<TEST|LIVE>
export ADYEN_NOTIFY_URL=<e.g. http://adyen-mirakl-connector.domain.com/api/adyen-notifications>
export ADYEN_LIABLE_ACCOUNT_CODE=<accountCode>
```

And settings for receiving notifications from Adyen and Mirakl: `NOTIFY_USERNAME` and `NOTIFY_PASSWORD`
```
export NOTIFY_USERNAME=<notifyUsername>
export NOTIFY_PASSWORD=<notifyPassword>
```

We use heroku mailtrap for development, please add the user and password in application.yml
```
export MAIL_HOST=<host>
export MAIL_PORT=<port>
export MAIL_USER=<user>
export MAIL_PASS=<pass>
```

(Optional) For running integration tests:
````
export MIRAKL_API_FRONT_KEY=<miraklApiFrontKey>
export ADYEN_PAL_USERNAME=<ws@company.merchantaccount>
export ADYEN_PAL_PASSWORD=<pass>
export ADYEN_PAL_MERCHANT_ACCOUNT=<merchant account>
````

And run `source ~/.bashrc`



To start your application in the dev profile, simply run:

    ./gradlew


For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].



## Building for production

To optimize the AdyenMiraklConnector application for production, run:

    ./gradlew -Pprod clean bootRepackage

To ensure everything worked, run:

    java -jar build/libs/*.war


Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./gradlew test

For more information, refer to the [Running tests page][].

## Heroku 
The connector can be deployed to Heroku for testing purposes. We only allow deployment to Heroku to connect to our test platform.

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)


## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[JHipster Homepage and latest documentation]: http://www.jhipster.tech
[JHipster 4.14.0 archive]: http://www.jhipster.tech/documentation-archive/v4.14.0

[Using JHipster in development]: http://www.jhipster.tech/documentation-archive/v4.14.0/development/
[Using Docker and Docker-Compose]: http://www.jhipster.tech/documentation-archive/v4.14.0/docker-compose
[Using JHipster in production]: http://www.jhipster.tech/documentation-archive/v4.14.0/production/
[Running tests page]: http://www.jhipster.tech/documentation-archive/v4.14.0/running-tests/
[Setting up Continuous Integration]: http://www.jhipster.tech/documentation-archive/v4.14.0/setting-up-ci/

## Local mail testing

To use a local mailcatcher (on systems that "nc" is installed):
    
```
export MAIL_HOST=localhost
export MAIL_PORT=8025
export MAIL_USER=any
export MAIL_PASS=any
```

and run:

    src/test/resources/scripts/smtp_nc.sh
    
## License
This repository is open source and available under the MIT license. See the LICENSE file for more info.
