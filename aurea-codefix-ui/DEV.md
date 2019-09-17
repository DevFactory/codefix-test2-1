# Development detail guide

## Running system against environments

| Environment               | Commands           |
|---------------------------|--------------------|
| dev                       | npm run startDev   |
| qa                        | npm run startQa    |
| prod                      | npm run startProd  |
| local (in 8586 port)      | npm run startLocal |

## Mock data

Mock data based app have been configured for fast prototyping and ui issue detection, using [json server](https://github.com/typicode/json-server).
To run system and json server `npm run mockServer:startMock` can be used.

## Unit test

### How to debug unit test

Use command `npm run testDebug` which will trigger unit test in browser and debug code using browser tools.

### Execute single test

The test framework used in this application [Jasmine](https://jasmine.github.io/2.0/introduction.html) provides `fdescribe` and `fit` 
alternatives to `describe` and `it` in order to execute single suite or single test.
