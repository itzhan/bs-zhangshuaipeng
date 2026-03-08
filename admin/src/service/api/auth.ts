import { request } from '../request';

/** Login response from our backend: {token: string, userInfo: {...}} */
interface LoginResponse {
  token: string;
  userInfo: Api.Auth.UserInfo;
}

/**
 * Login
 *
 * @param username User name
 * @param password Password
 */
export function fetchLogin(username: string, password: string) {
  return request<LoginResponse>({
    url: '/auth/login',
    method: 'post',
    data: {
      username,
      password
    }
  });
}

/** Get user info */
export function fetchGetUserInfo() {
  return request<Api.Auth.UserInfo>({ url: '/auth/info' });
}

/**
 * Refresh token — our backend doesn't support refresh token,
 * so this just returns null to trigger logout
 */
export function fetchRefreshToken(_refreshToken: string) {
  return request<Api.Auth.LoginToken>({
    url: '/auth/info',
    method: 'get'
  });
}

/**
 * return custom backend error
 */
export function fetchCustomBackendError(code: string, msg: string) {
  return request({ url: '/auth/error', params: { code, msg } });
}
