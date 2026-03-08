declare namespace Api {
  /**
   * namespace Auth
   *
   * backend api module: "auth"
   */
  namespace Auth {
    interface LoginToken {
      token: string;
      refreshToken: string;
    }

    interface UserInfo {
      userId: string;
      userName: string;
      nickname: string;
      avatar: string;
      role: string;
      roles: string[];
      buttons: string[];
    }
  }
}
