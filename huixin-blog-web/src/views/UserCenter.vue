<template>
  <div class="row justify-content-center">
    <div class="col-lg-8">
      <div class="card p-4 mb-3">
        <div class="d-flex align-items-center mb-3">
          <img :src="store.user?.avatarUrl || defaultAvatar" class="rounded-circle me-3" width="56" height="56" style="object-fit:cover">
          <div>
            <h5 class="mb-1">{{ store.user?.nickname || store.user?.username }}</h5>
            <small class="text-muted">{{ store.isBlogger ? '博主' : '普通用户' }}</small>
          </div>
        </div>
        <div class="row g-2 small">
          <div class="col-4"><strong>昵称：</strong>{{ store.user?.nickname || '-' }}</div>
          <div class="col-4"><strong>邮箱：</strong>{{ store.user?.email || '-' }}</div>
          <div class="col-4"><strong>注册时间：</strong>{{ formatTime(store.user?.createTime) }}</div>
        </div>
      </div>

      <div class="card p-4 mb-3" v-if="store.isBlogger">
        <h6 class="mb-3">我的文章</h6>
        <ArticleCard v-for="a in myArticles" :key="a.id" :article="a" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { articleApi } from '@/api/article'
import ArticleCard from '@/components/ArticleCard.vue'
const store = useUserStore()
const myArticles = ref([])
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="56" height="56"><circle cx="28" cy="28" r="28" fill="#8B8682"/></svg>')
function formatTime(t) { if (!t) return ''; return new Date(t).toLocaleDateString('zh-CN') }
onMounted(async () => {
  await store.fetchUserInfo()
  if (store.isBlogger) {
    try { const r = await articleApi.listByUser(store.user.id); if (r.code === 200) myArticles.value = r.data.records || [] } catch (e) {}
  }
})
</script>
