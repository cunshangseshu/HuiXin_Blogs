<template>
  <div class="row">
    <!-- 左侧：标签云 -->
    <div class="col-lg-2 d-none d-lg-block">
      <TagCloud />
    </div>
    <!-- 中间：信息流 -->
    <div class="col-lg-7">
      <div v-if="loading" class="text-center py-5 text-muted">加载中...</div>
      <template v-else>
        <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
        <Pagination v-if="pages > 1" :current="page" :pages="pages" @change="goPage" />
      </template>
    </div>
    <!-- 右侧：热门 -->
    <div class="col-lg-3 d-none d-lg-block">
      <HotSidebar />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import ArticleCard from '@/components/ArticleCard.vue'
import Pagination from '@/components/Pagination.vue'
import TagCloud from '@/components/TagCloud.vue'
import HotSidebar from '@/components/HotSidebar.vue'
import { articleApi, categoryApi } from '@/api/article'

const route = useRoute()
const articles = ref([])
const page = ref(1)
const pages = ref(1)
const loading = ref(true)

async function loadArticles(p = 1) {
  loading.value = true
  try {
    const params = { page: p, size: 10 }
    if (route.name === 'Category' && route.params.id) params.categoryId = route.params.id
    const res = await articleApi.list(params)
    if (res.code === 200 && res.data) {
      articles.value = res.data.records || []
      pages.value = res.data.pages || 1
      page.value = res.data.current || 1
    }
  } catch (e) {} finally { loading.value = false }
}

function goPage(p) { loadArticles(p); window.scrollTo({ top: 0, behavior: 'smooth' }) }

onMounted(() => loadArticles())
watch(() => route.params.id, () => loadArticles())
</script>
