# Code Fix UI
Client application which allow the repositories issues administration.

## Technical information
This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 7.0.6 and based on using [codefix-df-prototype](https://github.com/trilogy-group/dfproto-codefix).

### Getting started
1. Install node js, _recommended version latest v9.x, and install use nvm in linux/mac os environments_.
1. Install devfactory ui repo `npm install --@devfactory:registry=http://nexus-rapid-proto.devfactory.com/repository/npm-proto/` 
1. Install node angular cli by executing `npm install -g @angular/cli`.
1. Install other dependencies `npm install`.

### Development Commands
| Description             | Commands        |
|-------------------------|-----------------|
| Run application locally | ng serve        |
| Run unit test           | ng test         |
| Run checks              | npm run checks  |

_Less frequent but useful commands available at [development detail guide](/DEV.md)_

## Generals practices
1. Include unit test for functional classes as services and utils and components logic. 
1. No UI based testing as selenium. 
1. Avoid component with state or global properties or variables use ONLY stores to maintain application state.
1. Place in *Core* application core functionalities as security and navigation.
1. Place in *Shared* module your reusable components, pipes and directives in the shared module, so they can be used by all feature-modules.
1. If adding new data driven ui component please include example payload in json-server to be able to easily test ui. 

