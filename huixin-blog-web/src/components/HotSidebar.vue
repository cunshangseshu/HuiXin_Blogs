<template>
  <div class="card p-3 mb-3" v-if="hotItems.length">
    <h6 class="fw-bold mb-3">&#x1F525; 热门文章</h6>
    <div v-for="(item, idx) in hotItems" :key="item.id" class="d-flex align-items-center mb-2">
      <span class="fw-bold text-muted me-2" style="width:20px">{{ idx + 1 }}</span>
      <router-link :to="`/article/${item.id}`" class="text-truncate small text-decoration-none">
        {{ item.title }}
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statsApi } from '@/api/stats'
import { articleApi } from '@/api/article'
const hotItems = ref([])
onMounted(async () => {
  try {
    const res = await statsApi.getHot(5)
    if (res.code === 200 && res.data?.length) {
      const ids = res.data.slice(0, 5).map(h => h.articleId)
      for (const id of ids) {
        try {
          const detail = await articleApi.getDetail(id)
          if (detail.code === 200) hotItems.value.push(detail.data)
        } catch (e) {}
      }
    }
  } catch (e) {}
})
</script>
