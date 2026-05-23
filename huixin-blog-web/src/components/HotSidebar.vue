<template>
  <div class="card p-3 side-card">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h6 class="fw-bold mb-0">热门文章</h6>
      <small class="text-muted">Top {{ hotItems.length }}</small>
    </div>

    <div v-if="loading" class="text-muted small py-2">加载排行中...</div>

    <div v-else-if="hotItems.length" class="hot-list">
      <router-link
        v-for="(item, index) in hotItems"
        :key="item.id"
        :to="`/article/${item.id}`"
        class="hot-item text-decoration-none"
      >
        <span class="hot-rank">{{ index + 1 }}</span>
        <span class="hot-title">{{ item.title }}</span>
        <small class="hot-score">{{ item.hotScore || item.viewCount || 0 }}</small>
      </router-link>
    </div>

    <div v-else class="text-muted small py-2">暂无热门排行</div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { statsApi } from '@/api/stats'
import { articleApi } from '@/api/article'

const hotItems = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await statsApi.getHot(6)
    const list = res.code === 200 ? (res.data || []) : []
    const items = await Promise.all(list.map(async item => {
      try {
        const detail = await articleApi.getDetail(item.articleId)
        if (detail.code === 200 && detail.data) {
          return { ...detail.data, hotScore: item.hotScore }
        }
      } catch (e) {}
      return null
    }))
    hotItems.value = items.filter(Boolean)
  } catch (e) {
    hotItems.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.side-card {
  margin-bottom: 1rem;
}
.hot-list {
  display: grid;
  gap: .65rem;
}
.hot-item {
  display: grid;
  grid-template-columns: 24px 1fr auto;
  align-items: center;
  gap: .55rem;
  color: var(--huixin-text);
}
.hot-item:hover {
  color: var(--huixin-primary);
}
.hot-rank {
  width: 24px;
  height: 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 7px;
  background: var(--huixin-bg-soft);
  color: var(--huixin-primary);
  font-weight: 700;
  font-size: .8rem;
}
.hot-title {
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: .9rem;
}
.hot-score {
  color: var(--huixin-text-light);
}
</style>
