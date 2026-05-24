<template>
  <nav class="navbar navbar-expand-lg navbar-huixin sticky-top">
    <div class="container-fluid">
      <router-link to="/" class="navbar-brand d-flex align-items-center">
        <span class="brand-mark me-2">慧</span>
        <span>慧芯博客</span>
      </router-link>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain" aria-label="切换导航">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link to="/" class="nav-link">首页</router-link>
          </li>
          <li class="nav-item dropdown" v-if="categories.length">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">分类</a>
            <ul class="dropdown-menu category-menu">
              <li>
                <router-link class="dropdown-item" to="/">全部文章</router-link>
              </li>
              <li v-for="category in categories" :key="category.id">
                <router-link class="dropdown-item d-flex justify-content-between align-items-center" :to="`/category/${category.id}`">
                  <span>{{ category.categoryName }}</span>
                  <small class="text-muted">{{ category.articleCount || 0 }}</small>
                </router-link>
              </li>
            </ul>
          </li>
          <li class="nav-item">
            <router-link to="/search" class="nav-link">搜索</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/about" class="nav-link">关于</router-link>
          </li>
          <li class="nav-item" v-if="store.isBlogger">
            <router-link to="/article/new" class="nav-link">写文章</router-link>
          </li>
        </ul>

        <form class="nav-search d-flex me-lg-3 my-2 my-lg-0" @submit.prevent="doSearch">
          <input
            class="form-control form-control-sm"
            type="search"
            v-model.trim="keyword"
            placeholder="搜索文章标题、摘要"
            aria-label="搜索文章"
          >
          <button class="btn btn-sm btn-light ms-2" type="submit" :disabled="!keyword">搜索</button>
        </form>

        <ul class="navbar-nav">
          <template v-if="store.isLoggedIn">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" data-bs-toggle="dropdown">
                <img :src="store.user?.avatarUrl || defaultAvatar" class="rounded-circle me-2 nav-avatar" alt="">
                <span class="nav-user-name">{{ store.user?.nickname || store.user?.username }}</span>
              </a>
              <ul class="dropdown-menu dropdown-menu-end">
                <li><router-link to="/user/center" class="dropdown-item">个人中心</router-link></li>
                <li><router-link :to="`/user/${store.user?.id}`" class="dropdown-item">我的主页</router-link></li>
                <li v-if="store.isBlogger"><router-link to="/article/new" class="dropdown-item">发布文章</router-link></li>
                <li v-else><router-link to="/user/center?tab=apply" class="dropdown-item">申请博主</router-link></li>
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
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { authApi } from '@/api/auth'
import { categoryApi } from '@/api/article'

const router = useRouter()
const route = useRoute()
const store = useUserStore()
const keyword = ref(route.query.keyword || '')
const categories = ref([])
import { DEFAULT_AVATAR as defaultAvatar } from '@/utils/constants'

watch(() => route.query.keyword, value => {
  keyword.value = value || ''
})

function doSearch() {
  const value = keyword.value.trim()
  if (value) {
    router.push({ name: 'Search', query: { keyword: value } })
  } else {
    router.push({ name: 'Search' })
  }
}

async function logout() {
  try {
    await authApi.logout()
  } catch (e) {
    // 本地退出优先保证前端状态一致。
  }
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  store.clearUser()
  router.push('/')
}

onMounted(async () => {
  try {
    const res = await categoryApi.list()
    if (res.code === 200) categories.value = res.data || []
  } catch (e) {}
})
</script>

<style scoped>
.brand-mark {
  width: 28px;
  height: 28px;
  border-radius: 7px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,.14);
  border: 1px solid rgba(255,255,255,.22);
  color: #fff;
  font-family: var(--font-calligraphy);
  font-size: 1rem;
  letter-spacing: 0;
}
.nav-search input {
  width: min(260px, 42vw);
  border: 0;
}
.nav-search .btn {
  color: var(--huixin-primary-dark);
  border: 0;
}
.category-menu {
  min-width: 220px;
}
.nav-avatar {
  width: 28px;
  height: 28px;
  object-fit: cover;
}
.nav-user-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
@media (max-width: 991.98px) {
  .nav-search {
    width: 100%;
  }
  .nav-search input {
    width: 100%;
  }
}
</style>
