# Adyen Mirakl Connector

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/Adyen/adyen-mirakl.svg?branch=develop)](https://travis-ci.org/Adyen/adyen-mirakl)


The Adyen Mirakl Connector integrates a Mirakl Marketplace Platform Instance with an Adyen MarketPay Merchant Account.

* The Connector manages the transfer of Shop data from Mirakl to create and update Account Holders in the MarketPay for purpose of Financial Onboarding and KYC Checks.
* The Connector manages the Payout of Sellers through MarketPay when Mirakl Posts the Payout Voucher to the Connector.


## Documentation
We have a [Wiki](https://github.com/e2y/adyen-mirakl/wiki) for more detailed information.


## Installation

### Download the connector

You can clone this repository or download a released version from here:
https://github.com/Adyen/adyen-mirakl/releases

### Configure your credentials and environment settings

Please add environment variables for `MIRAKL_SDK_USER`, `MIRAKL_SDK_PASSWORD`, `MIRAKL_ENV_URL`, `MIRAKL_API_OPERATOR_KEY`, `MIRAKL_OPERATOR_EMAIL` and `MIRAKL_TIMEZONE` e.g.
update `~/.bashrc` with:
```
export MIRAKL_SDK_USER=<user>
export MIRAKL_SDK_PASSWORD=<pass>
export MIRAKL_ENV_URL=<miraklEnvUrl>
export MIRAKL_API_OPERATOR_KEY=<miraklApiOperatorKey>
export MIRAKL_OPERATOR_EMAIL=<miraklOperatorEmail>
export MIRAKL_TIMEZONE=<e.g. Europe/Amsterdam>
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

And settings for mail server (SMTP)
```
export MAIL_HOST=<host>
export MAIL_PORT=<port>
export MAIL_USER=<user>
export MAIL_PASS=<pass>
```


### Setup database connection

This is optional when in development.

It requires that you are running a database server eg. MySQL.
By default the connector will attempt to connect to localhost MySql server and use database name called adyenmiraklconnector with root user

You can set-up a database connection by exporting a jdbc query string e.g.:
SPRING_DATASOURCE_URL=jdbc:mysql://adyenmiraklconnector-mysql:3306/adyenmiraklconnector?useUnicode=true&characterEncoding=utf8&useSSL=false
or modifying the yml configuration file: https://github.com/Adyen/adyen-mirakl/blob/1.0.3/src/main/resources/config/application-prod.yml#L28



## Run in Development

This application was generated using JHipster 4.14.0, you can find documentation and help at [http://www.jhipster.tech/documentation-archive/v4.14.0](http://www.jhipster.tech/documentation-archive/v4.14.0).

For running integration tests you will need to configure:
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



## Run in production


To optimize the AdyenMiraklConnector application for production, run:

    ./gradlew -Pprod clean bootRepackage

To ensure everything worked, run:

    java -jar build/libs/*.war


Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./gradlew test

For more information, refer to the [Running tests page][].

## Logging

You can read more about default logging here: https://www.jhipster.tech/monitoring/

You can configure a log file in application yml configuration file. The one for dev environment is located here:
https://github.com/Adyen/adyen-mirakl/blob/1.0.3/src/main/resources/config/application-dev.yml#L16

The yml format would be like the following:

    logging:
      file: “FILE_LOCATION_HERE”
      level: ..

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
