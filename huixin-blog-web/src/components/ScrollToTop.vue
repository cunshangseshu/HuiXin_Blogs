<template>
  <button v-show="visible" class="scroll-top-btn" @click="scrollToTop" :title="tip">
    &#x2191;
  </button>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
const visible = ref(false)
const tip = '回到顶部'

function onScroll() { visible.value = window.scrollY > 400 }
function scrollToTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }

onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.scroll-top-btn {
  position: fixed; bottom: 30px; right: 30px; z-index: 999;
  width: 42px; height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--huixin-primary), var(--huixin-primary-dark));
  color: #fff; font-size: 1.3rem; border: none;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(74,107,93,0.3);
  transition: all .25s;
  display: flex; align-items: center; justify-content: center;
  animation: floatUp .3s ease-out;
}
.scroll-top-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(74,107,93,0.4);
}
@media (max-width: 768px) {
  .scroll-top-btn { bottom: 16px; right: 16px; width: 36px; height: 36px; font-size: 1.1rem; }
}
</style>
