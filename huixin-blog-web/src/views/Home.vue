<template>
  <div class="home-layout row g-3">
    <aside class="col-xl-2 col-lg-3 d-none d-lg-block">
      <div class="sticky-side">
        <TagCloud />
      </div>
    </aside>

    <section class="col-xl-7 col-lg-6">
      <div class="feed-toolbar card p-3 mb-3">
        <div class="d-flex align-items-start justify-content-between gap-3 flex-wrap">
          <div>
            <h1 class="feed-title">{{ pageTitle }}</h1>
            <p class="feed-subtitle mb-0">{{ pageSubtitle }}</p>
          </div>
          <div class="btn-group btn-group-sm sort-group" role="group" aria-label="文章排序">
            <button type="button" class="btn btn-outline-primary" :class="{ active: activeSort === 'latest' }" @click="changeSort('latest')">最新</button>
            <button type="button" class="btn btn-outline-primary" :class="{ active: activeSort === 'hot' }" @click="changeSort('hot')">热门</button>
          </div>
        </div>

        <div class="category-strip mt-3" v-if="categories.length">
          <router-link class="category-chip" :class="{ active: !route.params.id }" :to="{ path: '/', query: cleanQuery }">全部</router-link>
          <router-link
            v-for="category in categories"
            :key="category.id"
            class="category-chip"
            :class="{ active: String(route.params.id || '') === String(category.id) }"
            :to="{ path: `/category/${category.id}`, query: cleanQuery }"
          >
            {{ category.categoryName }}
          </router-link>
        </div>
      </div>

      <template v-if="loading">
        <ArticleSkeleton v-for="i in 4" :key="i" />
      </template>

      <template v-else-if="articles.length">
        <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
        <Pagination v-if="pages > 1" :current="page" :pages="pages" @change="goPage" />
      </template>

      <EmptyState
        v-else
        title="暂无文章"
        :description="emptyDescription"
      >
        <router-link v-if="store.isBlogger" to="/article/new" class="btn btn-primary btn-sm">发布第一篇文章</router-link>
      </EmptyState>
    </section>

    <aside class="col-xl-3 col-lg-3 d-none d-lg-block">
      <div class="sticky-side">
        <HotSidebar />
      </div>
    </aside>

    <section class="col-12 d-lg-none">
      <div class="row g-3">
        <div class="col-md-6"><TagCloud /></div>
        <div class="col-md-6"><HotSidebar /></div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ArticleCard from '@/components/ArticleCard.vue'
import ArticleSkeleton from '@/components/ArticleSkeleton.vue'
import EmptyState from '@/components/EmptyState.vue'
import Pagination from '@/components/Pagination.vue'
import TagCloud from '@/components/TagCloud.vue'
import HotSidebar from '@/components/HotSidebar.vue'
import { articleApi, categoryApi } from '@/api/article'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const articles = ref([])
const categories = ref([])
const page = ref(1)
const pages = ref(1)
const loading = ref(true)
const activeSort = computed(() => route.query.sort || 'latest')
const currentCategory = computed(() => categories.value.find(c => String(c.id) === String(route.params.id || '')))
const cleanQuery = computed(() => {
  const query = { ...route.query }
  delete query.tagId
  return query
})
const pageTitle = computed(() => currentCategory.value ? currentCategory.value.categoryName : '发现文章')
const pageSubtitle = computed(() => {
  if (route.query.tagId) return '按标签筛选内容'
  if (activeSort.value === 'hot') return '按阅读热度排序'
  return '最新发布的博客内容'
})
const emptyDescription = computed(() => route.params.id || route.query.tagId ? '当前筛选条件下没有文章，可以切换分类或标签。' : '数据库中还没有可展示的文章。')

async function loadArticles(p = 1) {
  loading.value = true
  try {
    const params = { page: p, size: 10, sort: activeSort.value }
    if (route.name === 'Category' && route.params.id) params.categoryId = route.params.id
    if (route.query.tagId) params.tagId = route.query.tagId
    const res = await articleApi.list(params)
    if (res.code === 200 && res.data) {
      articles.value = res.data.records || []
      pages.value = Number(res.data.pages || 1)
      page.value = Number(res.data.current || p)
    } else {
      articles.value = []
      pages.value = 1
      page.value = p
    }
  } catch (e) {
    articles.value = []
  } finally {
    loading.value = false
  }
}

function goPage(p) {
  loadArticles(p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function changeSort(sort) {
  router.push({ path: route.path, query: { ...route.query, sort } })
}

onMounted(async () => {
  try {
    const res = await categoryApi.list()
    if (res.code === 200) categories.value = res.data || []
  } catch (e) {}
  loadArticles()
})

watch(() => route.fullPath, () => loadArticles())
</script>

<style scoped>
.sticky-side {
  position: sticky;
  top: 76px;
}
.feed-toolbar {
  border-top: 3px solid var(--huixin-primary);
}
.feed-title {
  margin: 0;
  font-size: 1.35rem;
  line-height: 1.3;
  font-weight: 800;
  color: var(--huixin-text);
}
.feed-subtitle {
  color: var(--huixin-text-light);
  font-size: .9rem;
}
.sort-group .btn.active {
  color: #fff;
  background: var(--huixin-primary);
}
.category-strip {
  display: flex;
  gap: .5rem;
  overflow-x: auto;
  padding-bottom: .1rem;
}
.category-chip {
  flex: 0 0 auto;
  padding: .28rem .75rem;
  border-radius: 999px;
  border: 1px solid var(--huixin-border);
  background: #fff;
  color: var(--huixin-text);
  font-size: .86rem;
  text-decoration: none;
}
.category-chip:hover,
.category-chip.active {
  border-color: var(--huixin-primary);
  background: var(--huixin-bg-soft);
  color: var(--huixin-primary);
}
</style>
