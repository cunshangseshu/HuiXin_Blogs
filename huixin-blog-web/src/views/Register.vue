<template>
  <div class="row justify-content-center auth-page">
    <div class="col-md-6 col-lg-4">
      <div class="card auth-card p-4">
        <h1 class="auth-title">注册慧芯博客</h1>
        <p class="auth-subtitle">创建账号后即可参与互动</p>

        <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>

        <form @submit.prevent="handleRegister">
          <div class="mb-3">
            <label class="form-label">用户名</label>
            <input v-model.trim="form.username" class="form-control" required minlength="4" maxlength="20" placeholder="4-20位字母数字下划线">
          </div>
          <div class="mb-3">
            <label class="form-label">邮箱</label>
            <input v-model.trim="form.email" type="email" class="form-control" required placeholder="请输入邮箱">
          </div>
          <div class="mb-3">
            <label class="form-label">密码</label>
            <input v-model="form.password" type="password" class="form-control" required minlength="8" maxlength="20" placeholder="8-20位，含字母和数字">
          </div>
          <div class="mb-3">
            <label class="form-label">确认密码</label>
            <input v-model="form.confirmPassword" type="password" class="form-control" required placeholder="请再次输入密码">
            <small v-if="form.confirmPassword && form.password !== form.confirmPassword" class="text-danger">两次密码不一致</small>
          </div>
          <button type="submit" class="btn btn-primary w-100" :disabled="loading || !canSubmit">
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>

        <div class="text-center mt-3 small">
          已有账号？<router-link :to="{ name: 'Login', query: route.query.redirect ? { redirect: route.query.redirect } : {} }">去登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authApi } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const form = reactive({ username: '', email: '', password: '', confirmPassword: '' })
const loading = ref(false)
const error = ref('')
const canSubmit = computed(() => form.username && form.email && form.password && form.confirmPassword && form.password === form.confirmPassword)

async function handleRegister() {
  if (!canSubmit.value || loading.value) return
  loading.value = true
  error.value = ''
  if (!/^[a-zA-Z0-9_]{4,20}$/.test(form.username)) {
    error.value = '用户名只能包含4-20位字母、数字和下划线'
    loading.value = false
    return
  }
  if (!/^(?=\S+$)(?=.*[a-zA-Z])(?=.*\d).{8,20}$/.test(form.password)) {
    error.value = '密码需为8-20位，包含字母和数字，且不能包含空格'
    loading.value = false
    return
  }
  try {
    const res = await authApi.register({
      username: form.username,
      email: form.email,
      password: form.password
    })
    if (res.code === 200) {
      alert('注册成功，请登录')
      router.push({ name: 'Login', query: route.query.redirect ? { redirect: route.query.redirect } : {} })
    } else {
      error.value = res.message || '注册失败'
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
