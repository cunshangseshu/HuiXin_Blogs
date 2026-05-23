<template>
  <div id="huixin-app" class="min-vh-100">
    <Navbar />
    <main class="app-main container-fluid py-3">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <Footer />
    <ScrollToTop />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import Navbar from './components/Navbar.vue'
import Footer from './components/Footer.vue'
import ScrollToTop from './components/ScrollToTop.vue'
import { useUserStore } from '@/store/user'

const store = useUserStore()

onMounted(() => {
  if (localStorage.getItem('accessToken') && !store.user) {
    store.fetchUserInfo()
  }
})
</script>

<style>
.app-main {
  max-width: 1480px;
  min-height: calc(100vh - 56px - 100px);
}
.fade-enter-active, .fade-leave-active { transition: opacity .2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
