<template>
  <div class="row justify-content-center">
    <div class="col-xl-7 col-lg-8">
      <div class="card search-panel p-3 p-lg-4 mb-3">
        <h1 class="search-title">搜索文章</h1>
        <form class="search-form mt-3" @submit.prevent="submitSearch">
          <input v-model.trim="inputKeyword" class="form-control form-control-lg" placeholder="输入关键词，例如 Spring Boot、Vue、Redis">
          <button class="btn btn-primary" type="submit" :disabled="!inputKeyword">搜索</button>
        </form>

        <div class="keyword-groups mt-3" v-if="!keyword">
          <div v-if="hotKeywords.length">
            <div class="keyword-label">热门搜索</div>
            <button v-for="item in hotKeywords" :key="item" class="keyword-chip" @click="useKeyword(item)">{{ item }}</button>
          </div>
          <div v-if="historyKeywords.length" class="mt-3">
            <div class="keyword-label">最近搜索</div>
            <button v-for="item in historyKeywords" :key="item" class="keyword-chip" @click="useKeyword(item)">{{ item }}</button>
          </div>
        </div>
      </div>

      <div v-if="keyword" class="result-heading mb-2">
        <span>搜索：{{ keyword }}</span>
        <small class="text-muted">共 {{ total }} 条结果</small>
      </div>

      <template v-if="loading">
        <ArticleSkeleton v-for="i in 3" :key="i" />
      </template>

      <template v-else-if="articles.length">
        <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
        <Pagination v-if="pages > 1" :current="page" :pages="pages" @change="goPage" />
      </template>

      <EmptyState
        v-else-if="keyword"
        title="没有找到相关文章"
        description="换一个更短或更具体的关键词试试。"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchApi } from '@/api/search'
import ArticleCard from '@/components/ArticleCard.vue'
import ArticleSkeleton from '@/components/ArticleSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import Pagination from '@/components/Pagination.vue'

const HISTORY_KEY = 'huixin_search_history'
const route = useRoute()
const router = useRouter()
const keyword = ref(route.query.keyword || '')
const inputKeyword = ref(keyword.value)
const articles = ref([])
const hotKeywords = ref([])
const historyKeywords = ref([])
const page = ref(1)
const pages = ref(1)
const total = ref(0)
const loading = ref(false)

function readHistory() {
  try {
    historyKeywords.value = JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]')
  } catch (e) {
    historyKeywords.value = []
  }
}

function rememberKeyword(value) {
  const next = [value, ...historyKeywords.value.filter(item => item !== value)].slice(0, 8)
  historyKeywords.value = next
  localStorage.setItem(HISTORY_KEY, JSON.stringify(next))
}

function submitSearch() {
  if (!inputKeyword.value) return
  router.push({ name: 'Search', query: { keyword: inputKeyword.value } })
}

function useKeyword(value) {
  inputKeyword.value = value
  router.push({ name: 'Search', query: { keyword: value } })
}

async function doSearch(p = 1) {
  keyword.value = route.query.keyword || ''
  inputKeyword.value = keyword.value
  articles.value = []
  total.value = 0
  if (!keyword.value) {
    loading.value = false
    return
  }
  loading.value = true
  try {
    rememberKeyword(keyword.value)
    const res = await searchApi.search(keyword.value, p)
    if (res.code === 200 && res.data) {
      articles.value = res.data.records || []
      pages.value = Number(res.data.pages || 1)
      page.value = Number(res.data.current || p)
      total.value = Number(res.data.total || articles.value.length)
    }
  } catch (e) {
    articles.value = []
  } finally {
    loading.value = false
  }
}

function goPage(p) {
  doSearch(p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(async () => {
  readHistory()
  try {
    const res = await searchApi.getHotKeywords(8)
    if (res.code === 200) hotKeywords.value = res.data || []
  } catch (e) {}
  doSearch()
})

watch(() => route.query.keyword, () => doSearch())
</script>

<style scoped>
.search-panel {
  border-top: 3px solid var(--huixin-primary);
}
.search-title {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 800;
}
.search-form {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: .75rem;
}
.keyword-label {
  margin-bottom: .45rem;
  color: var(--huixin-text-light);
  font-size: .82rem;
}
.keyword-chip {
  margin: 0 .45rem .45rem 0;
  padding: .28rem .75rem;
  border-radius: 999px;
  border: 1px solid var(--huixin-border);
  background: #fff;
  color: var(--huixin-text);
  font-size: .86rem;
}
.keyword-chip:hover {
  color: var(--huixin-primary);
  border-color: var(--huixin-primary);
  background: var(--huixin-bg-soft);
}
.result-heading {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
  font-weight: 700;
}
@media (max-width: 575.98px) {
  .search-form {
    grid-template-columns: 1fr;
  }
}
</style>
