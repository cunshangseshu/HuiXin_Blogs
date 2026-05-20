<template>
  <div class="row justify-content-center">
    <div class="col-lg-7">
      <h5 class="mb-3">搜索：{{ keyword }}</h5>
      <div v-if="loading" class="text-center py-5 text-muted">搜索中...</div>
      <template v-else>
        <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
        <Pagination v-if="pages > 1" :current="page" :pages="pages" @change="goPage" />
        <div v-if="!articles.length" class="text-center text-muted py-5">未找到相关文章</div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { searchApi } from '@/api/search'
import ArticleCard from '@/components/ArticleCard.vue'
import Pagination from '@/components/Pagination.vue'
const route = useRoute()
const keyword = ref(''); const articles = ref([])
const page = ref(1); const pages = ref(1); const loading = ref(true)
async function doSearch(p = 1) {
  keyword.value = route.query.keyword || ''
  loading.value = true
  try {
    const r = await searchApi.search(keyword.value, p)
    if (r.code === 200 && r.data) { articles.value = r.data.records || []; pages.value = r.data.pages || 1; page.value = r.data.current || 1 }
  } catch (e) {} finally { loading.value = false }
}
function goPage(p) { doSearch(p) }
onMounted(() => doSearch())
watch(() => route.query.keyword, () => doSearch())
</script>
