<template>
  <div class="row justify-content-center">
    <div class="col-xl-7 col-lg-8">
      <section v-if="user" class="card profile-card p-4 mb-3">
        <img :src="user.avatarUrl || defaultAvatar" class="profile-avatar" alt="">
        <div class="profile-copy">
          <h1>{{ user.nickname || user.username }}</h1>
          <p>{{ user.bio || '还没有填写个人简介' }}</p>
          <span class="role-badge" :class="{ blogger: user.roleType === 1 }">{{ user.roleType === 1 ? '博主' : '用户' }}</span>
        </div>
      </section>

      <EmptyState v-else-if="!loading" title="用户不存在" description="无法找到这个用户的公开资料。">
        <router-link to="/" class="btn btn-primary btn-sm">返回首页</router-link>
      </EmptyState>

      <div v-if="user" class="section-heading mb-2">
        <span>发布的文章</span>
        <small class="text-muted">{{ total }} 篇</small>
      </div>

      <template v-if="loading">
        <ArticleSkeleton v-for="i in 3" :key="i" />
      </template>

      <template v-else-if="articles.length">
        <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
        <Pagination v-if="pages > 1" :current="page" :pages="pages" @change="goPage" />
      </template>

      <EmptyState v-else-if="user" title="暂无公开文章" description="这个用户还没有发布文章。" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { userApi } from '@/api/user'
import { articleApi } from '@/api/article'
import ArticleCard from '@/components/ArticleCard.vue'
import ArticleSkeleton from '@/components/ArticleSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import Pagination from '@/components/Pagination.vue'

const route = useRoute()
const user = ref(null)
const articles = ref([])
const page = ref(1)
const pages = ref(1)
const total = ref(0)
const loading = ref(true)
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="72" height="72"><circle cx="36" cy="36" r="36" fill="#727D78"/></svg>')

async function load(p = 1) {
  loading.value = true
  const uid = Number(route.params.id)
  user.value = null
  articles.value = []
  try {
    const res = await userApi.getPublicInfo(uid)
    if (res.code === 200) user.value = res.data
  } catch (e) {}

  if (user.value) {
    try {
      const res = await articleApi.listByUser(uid, p)
      if (res.code === 200 && res.data) {
        articles.value = res.data.records || []
        pages.value = Number(res.data.pages || 1)
        page.value = Number(res.data.current || p)
        total.value = Number(res.data.total || articles.value.length)
      }
    } catch (e) {}
  }
  loading.value = false
}

function goPage(p) {
  load(p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => load())
watch(() => route.params.id, () => load())
</script>

<style scoped>
.profile-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  border-top: 3px solid var(--huixin-primary);
}
.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--huixin-border);
}
.profile-copy {
  min-width: 0;
}
.profile-copy h1 {
  margin: 0 0 .25rem;
  font-size: 1.35rem;
  font-weight: 800;
}
.profile-copy p {
  margin: 0 0 .5rem;
  color: var(--huixin-text-light);
}
.role-badge {
  display: inline-flex;
  padding: .16rem .55rem;
  border-radius: 999px;
  color: #fff;
  background: #6c757d;
  font-size: .78rem;
}
.role-badge.blogger {
  background: var(--huixin-gold);
}
.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 800;
}
@media (max-width: 575.98px) {
  .profile-card {
    align-items: flex-start;
  }
}
</style>
