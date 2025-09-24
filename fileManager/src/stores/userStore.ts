import type { ILoginInfo, IUser ,RoleType} from '@/types/interface';
import { defineStore } from 'pinia';

import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const user = ref<string>('');

  const role = ref<RoleType>(null);

  //模拟用户登录
  const loginInfo = {
    admin: 'admin',
    adminPassword: 'admin123',
    user: 'test',
    userPassword: 'test123',
  };

  const login = (loginData: ILoginInfo) => {
    //对数据进行处理
    loginData.account = loginData.account.trim();
    loginData.password = loginData.password.trim();

    const result: IUser = loginValidated(loginData);

    if (result.role != null) {
      user.value = result.user;
      role.value = result.role;
      return true;
    }
    return false;
  };

  const loginValidated = (loginData: ILoginInfo) => {
    const user: IUser = {
      user: 'user',
      role: 'user',
    };

    const admin: IUser = {
      user: 'admin',
      role: 'admin',
    };

    const errorUser: IUser = {
      user: 'error',
      role: null,
    };

    //模拟登录逻辑,假设发送请求，并进行对比
    if (loginData.account == loginInfo.admin && loginData.password == loginInfo.adminPassword) {
      return admin;
    }

    if (loginData.account == loginInfo.user && loginData.password == loginInfo.userPassword) {
      return user;
    }

    return errorUser;
  };

  return {
    login,
  };
});
