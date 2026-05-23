import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const userLoading = ref(false)
  const isLoggedIn = computed(() => !!user.value)
  const isBlogger = computed(() => user.value?.roleType === 1)

  async function fetchUserInfo() {
    if (!localStorage.getItem('accessToken')) {
      user.value = null
      return false
    }
    userLoading.value = true
    try {
      const res = await userApi.getInfo()
      if (res.code === 200) {
        user.value = res.data
        return true
      }
    } catch (e) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      user.value = null
      return false
    } finally {
      userLoading.value = false
    }
    return false
  }

  function setUser(data) { user.value = data }
  function clearUser() { user.value = null }

  return { user, userLoading, isLoggedIn, isBlogger, fetchUserInfo, setUser, clearUser }
})
