<template>
  <nav v-if="pages > 1">
    <ul class="pagination justify-content-center">
      <li class="page-item" :class="{ disabled: current <= 1 }">
        <a class="page-link" href="#" @click.prevent="go(current - 1)">上一页</a>
      </li>
      <li v-for="p in visiblePages" :key="p" class="page-item" :class="{ active: p === current }">
        <a class="page-link" href="#" @click.prevent="go(p)">{{ p }}</a>
      </li>
      <li class="page-item" :class="{ disabled: current >= pages }">
        <a class="page-link" href="#" @click.prevent="go(current + 1)">下一页</a>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ current: Number, pages: Number })
const emit = defineEmits(['change'])
const visiblePages = computed(() => {
  const p = []
  let start = Math.max(1, props.current - 2)
  let end = Math.min(props.pages, start + 4)
  start = Math.max(1, end - 4)
  for (let i = start; i <= end; i++) p.push(i)
  return p
})
function go(p) { if (p >= 1 && p <= props.pages) emit('change', p) }
</script>
