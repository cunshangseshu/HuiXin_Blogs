import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const isLoggedIn = computed(() => !!user.value)
  const isBlogger = computed(() => user.value?.roleType === 1)

  async function fetchUserInfo() {
    try {
      const res = await userApi.getInfo()
      if (res.code === 200) {
        user.value = res.data
      }
    } catch (e) { /* 未登录或Token过期 */ }
  }

  function setUser(data) { user.value = data }
  function clearUser() { user.value = null }

  return { user, isLoggedIn, isBlogger, fetchUserInfo, setUser, clearUser }
})
