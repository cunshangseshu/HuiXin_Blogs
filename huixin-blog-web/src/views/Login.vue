<template>
  <div class="row justify-content-center">
    <div class="col-md-5 col-lg-4">
      <div class="card p-4">
        <h4 class="text-center mb-3">登录慧芯博客</h4>
        <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>
        <form @submit.prevent="handleLogin">
          <div class="mb-3">
            <label class="form-label">用户名 / 邮箱</label>
            <input v-model="form.account" class="form-control" required placeholder="请输入用户名或邮箱">
          </div>
          <div class="mb-3">
            <label class="form-label">密码</label>
            <input v-model="form.password" type="password" class="form-control" required placeholder="请输入密码">
          </div>
          <button type="submit" class="btn btn-primary w-100" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>
        <div class="text-center mt-3 small">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { authApi } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const store = useUserStore()
const form = reactive({ account: '', password: '' })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true; error.value = ''
  try {
    const res = await authApi.login({ account: form.account, password: form.password })
    if (res.code === 200 && res.data) {
      localStorage.setItem('accessToken', res.data.accessToken)
      localStorage.setItem('refreshToken', res.data.refreshToken)
      // 调用后端接口获取已验证的用户信息（替代不安全的客户端 JWT 解码）
      await store.fetchUserInfo()
      router.push(route.query.redirect || '/')
    } else { error.value = res.message || '登录失败' }
  } catch (e) { error.value = e.message || '网络错误' }
  loading.value = false
}
</script>
