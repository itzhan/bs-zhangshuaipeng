import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<any>(null)
  const isLogin = computed(() => !!token.value)

  async function login(data: any) {
    const res: any = await authApi.login(data)
    token.value = res.token
    userInfo.value = res.userInfo
    localStorage.setItem('token', res.token)
    return res
  }

  async function register(data: any) {
    return await authApi.register(data)
  }

  async function fetchUserInfo() {
    try {
      const res: any = await authApi.getInfo()
      userInfo.value = res
    } catch { logout() }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, isLogin, login, register, fetchUserInfo, logout }
})
