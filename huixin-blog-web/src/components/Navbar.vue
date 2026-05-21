<template>
  <nav class="navbar navbar-expand-lg navbar-huixin sticky-top">
    <div class="container-fluid">
      <router-link to="/" class="navbar-brand d-flex align-items-center">
        <span class="me-2">&#x2728;</span>慧芯博客
      </router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto">
          <li class="nav-item"><router-link to="/" class="nav-link">首页</router-link></li>
          <li class="nav-item" v-if="store.isBlogger">
            <router-link to="/article/new" class="nav-link">写文章</router-link>
          </li>
        </ul>
        <form class="d-flex me-3" @submit.prevent="doSearch">
          <input class="form-control form-control-sm" type="search" v-model="keyword" placeholder="搜索文章..." style="width:200px">
        </form>
        <ul class="navbar-nav">
          <template v-if="store.isLoggedIn">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                <img :src="store.user?.avatarUrl || defaultAvatar" class="rounded-circle me-1" width="24" height="24" style="object-fit:cover">
                {{ store.user?.nickname || store.user?.username }}
              </a>
              <ul class="dropdown-menu dropdown-menu-end">
                <li><router-link to="/user/center" class="dropdown-item">个人中心</router-link></li>
                <li v-if="!store.isBlogger">
                  <router-link to="/user/center?tab=apply" class="dropdown-item">申请博主</router-link>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#" @click.prevent="logout">退出登录</a></li>
              </ul>
            </li>
          </template>
          <template v-else>
            <li class="nav-item"><router-link to="/login" class="nav-link">登录</router-link></li>
            <li class="nav-item"><router-link to="/register" class="nav-link">注册</router-link></li>
          </template>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const store = useUserStore()
const keyword = ref('')
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><circle cx="12" cy="12" r="12" fill="#8B8682"/></svg>')

function doSearch() {
  if (keyword.value.trim()) {
    router.push({ name: 'Search', query: { keyword: keyword.value.trim() } })
  }
}

async function logout() {
  const userId = store.user?.id
  if (userId) {
    try { await import('@/api/auth').then(m => m.authApi.logout(userId)) } catch (e) {}
  }
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  store.clearUser()
  router.push('/')
}
</script>
