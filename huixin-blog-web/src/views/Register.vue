<template>
  <div class="row justify-content-center">
    <div class="col-md-5 col-lg-4">
      <div class="card p-4">
        <h4 class="text-center mb-3">注册慧芯博客</h4>
        <div v-if="error" class="alert alert-danger py-2">{{ error }}</div>
        <form @submit.prevent="handleRegister">
          <div class="mb-3">
            <label class="form-label">用户名</label>
            <input v-model="form.username" class="form-control" required minlength="4" maxlength="20" placeholder="4-20位字母数字下划线">
          </div>
          <div class="mb-3">
            <label class="form-label">邮箱</label>
            <input v-model="form.email" type="email" class="form-control" required placeholder="请输入邮箱">
          </div>
          <div class="mb-3">
            <label class="form-label">密码</label>
            <input v-model="form.password" type="password" class="form-control" required minlength="8" placeholder="8-20位，需含字母和数字">
          </div>
          <button type="submit" class="btn btn-primary w-100" :disabled="loading">
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>
        <div class="text-center mt-3 small">
          已有账号？<router-link to="/login">去登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
const router = useRouter()
const form = reactive({ username: '', email: '', password: '' })
const loading = ref(false); const error = ref('')
async function handleRegister() {
  loading.value = true; error.value = ''
  try {
    const res = await authApi.register({ ...form })
    if (res.code === 200) {
      alert('注册成功！请登录')
      router.push('/login')
    } else { error.value = res.message || '注册失败' }
  } catch (e) { error.value = e.message || '网络错误' }
  loading.value = false
}
</script>
