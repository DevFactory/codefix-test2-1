import { Auth0DecodedHash } from 'auth0-js';
import { User } from '@app/core/models/user';

export const AUTH_TOKEN: Auth0DecodedHash = {
  accessToken: 'a token',
  idToken: 'an id token',
  expiresIn: 78000,
  idTokenPayload: {
    name : 'user',
    nickname: 'nickname',
    picture: 'picture',
    email: 'user@email.com'
  }
};

export const AUTH_USER: User = new User(
    'user',
    'nickname',
    'picture',
    'user@email.com'
);
