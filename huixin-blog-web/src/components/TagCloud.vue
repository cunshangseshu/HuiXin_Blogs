<template>
  <div class="card p-3">
    <h6 class="fw-bold mb-2">标签云</h6>
    <div v-if="tags.length">
      <router-link v-for="t in tags" :key="t.id" :to="`/?tagId=${t.id}`"
        class="tag-badge text-decoration-none" :style="{ backgroundColor: t.tagColor || '#6B7280' }">
        {{ t.tagName }}
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { tagApi } from '@/api/article'
const tags = ref([])
onMounted(async () => {
  try { const res = await tagApi.list(); if (res.code === 200) tags.value = res.data || [] } catch (e) {}
})
</script>
