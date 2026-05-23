<template>
  <div class="row justify-content-center auth-page">
    <div class="col-md-6 col-lg-4">
      <div class="card auth-card p-4">
        <h1 class="auth-title">登录慧芯博客</h1>
        <p class="auth-subtitle">继续阅读、评论和写作</p>

        <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>

        <form @submit.prevent="handleLogin">
          <div class="mb-3">
            <label class="form-label">用户名 / 邮箱</label>
            <input v-model.trim="form.account" class="form-control" required autocomplete="username" placeholder="请输入用户名或邮箱">
          </div>
          <div class="mb-3">
            <label class="form-label">密码</label>
            <input v-model="form.password" type="password" class="form-control" required autocomplete="current-password" placeholder="请输入密码">
          </div>
          <button type="submit" class="btn btn-primary w-100" :disabled="loading || !form.account || !form.password">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>

        <div class="text-center mt-3 small">
          还没有账号？<router-link :to="{ name: 'Register', query: route.query.redirect ? { redirect: route.query.redirect } : {} }">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { authApi } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const store = useUserStore()
const form = reactive({ account: '', password: '' })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  if (loading.value) return
  loading.value = true
  error.value = ''
  try {
    const res = await authApi.login({ account: form.account, password: form.password })
    if (res.code === 200 && res.data) {
      localStorage.setItem('accessToken', res.data.accessToken)
      localStorage.setItem('refreshToken', res.data.refreshToken)
      await store.fetchUserInfo()
      router.push(route.query.redirect || '/')
    } else {
      error.value = res.message || '登录失败'
    }
  } catch (e) {
    error.value = e.message || '网络错误'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  padding-top: 4vh;
}
.auth-card {
  border-top: 3px solid var(--huixin-primary);
}
.auth-title {
  margin: 0;
  text-align: center;
  font-size: 1.45rem;
  font-weight: 800;
}
.auth-subtitle {
  text-align: center;
  color: var(--huixin-text-light);
  font-size: .9rem;
  margin: .35rem 0 1.25rem;
}
</style>
