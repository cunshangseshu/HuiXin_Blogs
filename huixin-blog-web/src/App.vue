<template>
  <div id="huixin-app" class="min-vh-100">
    <Navbar />
    <main class="container-fluid py-3">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
</script>
