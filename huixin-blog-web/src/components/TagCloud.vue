<template>
  <div class="card p-3 side-card">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <h6 class="fw-bold mb-0">标签云</h6>
      <small class="text-muted">{{ tags.length }}</small>
    </div>
    <div v-if="loading" class="text-muted small py-2">加载标签中...</div>
    <div v-else-if="tags.length" class="tag-cloud">
      <router-link
        v-for="tag in tags"
        :key="tag.id"
        :to="{ path: '/', query: { tagId: tag.id } }"
        class="tag-badge text-decoration-none"
        :style="{ backgroundColor: tag.tagColor || '#6B7280' }"
        :title="`${tag.tagName} · ${tag.articleCount || 0} 篇`"
      >
        {{ tag.tagName }}
      </router-link>
    </div>
    <div v-else class="text-muted small py-2">暂无标签</div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { tagApi } from '@/api/article'

const tags = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await tagApi.list()
    if (res.code === 200) tags.value = res.data || []
  } catch (e) {
    tags.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.side-card {
  margin-bottom: 1rem;
}
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: .35rem;
}
.tag-cloud .tag-badge {
  margin: 0;
}
</style>
