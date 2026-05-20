<template>
  <div class="row justify-content-center">
    <div class="col-lg-8">
      <div class="card p-4 mb-3 text-center" v-if="user">
        <img :src="user.avatarUrl || defaultAvatar" class="rounded-circle mx-auto mb-2" width="72" height="72" style="object-fit:cover">
        <h5>{{ user.nickname || user.username }}</h5>
        <small class="text-muted">{{ user.bio || '这个人很懒，什么都没写' }}</small>
        <div class="mt-1">
          <span class="badge" :class="user.roleType === 1 ? 'bg-success' : 'bg-secondary'">
            {{ user.roleType === 1 ? '博主' : '用户' }}
          </span>
        </div>
      </div>
      <h6 class="mb-2">发布的文章</h6>
      <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
      <Pagination v-if="pages > 1" :current="page" :pages="pages" @change="goPage" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { userApi } from '@/api/user'
import { articleApi } from '@/api/article'
import ArticleCard from '@/components/ArticleCard.vue'
import Pagination from '@/components/Pagination.vue'
const route = useRoute()
const user = ref(null); const articles = ref([])
const page = ref(1); const pages = ref(1)
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="72" height="72"><circle cx="36" cy="36" r="36" fill="#8B8682"/></svg>')
async function load(p = 1) {
  const uid = Number(route.params.id)
  try { const r = await userApi.getPublicInfo(uid); if (r.code === 200) user.value = r.data } catch (e) {}
  try {
    const r = await articleApi.listByUser(uid, p)
    if (r.code === 200 && r.data) { articles.value = r.data.records || []; pages.value = r.data.pages || 1 }
  } catch (e) {}
}
function goPage(p) { load(p) }
onMounted(() => load())
</script>
