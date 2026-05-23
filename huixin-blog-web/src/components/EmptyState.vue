<template>
  <div class="empty-state text-center">
    <div class="empty-state__mark">{{ mark }}</div>
    <h6 class="empty-state__title">{{ title }}</h6>
    <p v-if="description" class="empty-state__desc">{{ description }}</p>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  mark: { type: String, default: '-' }
})
</script>

<style scoped>
.empty-state {
  padding: 3rem 1.5rem;
  color: var(--huixin-text-light);
  border: 1px dashed var(--huixin-border);
  border-radius: 8px;
  background: rgba(255,255,255,.62);
}
.empty-state__mark {
  width: 46px;
  height: 46px;
  margin: 0 auto .8rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--huixin-bg-soft);
  color: var(--huixin-primary);
  font-weight: 700;
}
.empty-state__title {
  margin-bottom: .35rem;
  color: var(--huixin-text);
}
.empty-state__desc {
  max-width: 360px;
  margin: 0 auto 1rem;
  font-size: .9rem;
}
</style>
