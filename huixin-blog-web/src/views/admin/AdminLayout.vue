<template>
  <div class="admin-container">
    <!-- 后台左侧导航菜单 -->
    <aside class="admin-sidebar shadow-sm">
      <div class="admin-brand">
        <span class="brand-mark me-2">慧</span>
        <span>慧芯管理中台</span>
      </div>
      <nav class="admin-nav">
        <router-link to="/admin/dashboard" class="admin-nav-item" active-class="active">
          📊 数据仪表盘
        </router-link>
        <router-link to="/admin/approvals" class="admin-nav-item" active-class="active">
          📝 博主申请审批
        </router-link>
        <router-link to="/admin/content" class="admin-nav-item" active-class="active">
          🛡️ 违规内容监管
        </router-link>
      </nav>
      <div class="admin-footer">
        <router-link to="/" class="btn btn-sm btn-outline-secondary w-100">返回博客前台</router-link>
      </div>
    </aside>

    <!-- 后台右侧内容呈现区 -->
    <main class="admin-main bg-light">
      <header class="admin-topbar bg-white shadow-sm px-4">
        <h5 class="mb-0 fw-bold">{{ currentRouteName }}</h5>
        <div class="d-flex align-items-center gap-3">
          <span class="badge bg-danger">超级管理员组</span>
          <img :src="store.user?.avatarUrl || defaultAvatar" width="36" height="36" class="rounded-circle border" alt="">
        </div>
      </header>
      
      <!-- 这里将按路由动态渲染子页面 (如审批页、仪表盘页) -->
      <div class="admin-content p-4">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { DEFAULT_AVATAR as defaultAvatar } from '@/utils/constants'

const route = useRoute()
const store = useUserStore()

// 映射给头部面包屑
const routeNames = {
  'AdminDashboard': '全站高并发 QPS 仪表盘',
  'AdminApprovals': '创作者入驻审批中心',
  'AdminContent': '全领域内容封杀管控'
}
const currentRouteName = computed(() => routeNames[route.name] || '后台管理')
</script>

<style scoped>
/* 延续现代中国风青黛色主色调，排版采用非常干净的 Notion 类似布局 */
.admin-container {
  display: flex;
  min-height: 100vh;
  /* 屏蔽掉前台继承的可能边距 */
  margin: -1.5rem calc(-50vw + 50%);
}
.admin-sidebar {
  width: 260px;
  background: #ffffff;
  border-right: 1px solid var(--huixin-border);
  display: flex;
  flex-direction: column;
}
.admin-brand {
  height: 70px;
  display: flex;
  align-items: center;
  padding: 0 1.5rem;
  font-size: 1.2rem;
  font-weight: 800;
  color: var(--huixin-primary);
  border-bottom: 1px solid var(--huixin-border);
}
.brand-mark {
  width: 28px;
  height: 28px;
  border-radius: 7px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--huixin-primary);
  color: #fff;
  font-family: var(--font-calligraphy);
  font-size: 1rem;
}
.admin-nav {
  padding: 1.5rem 1rem;
  flex: 1;
}
.admin-nav-item {
  display: block;
  padding: 0.8rem 1rem;
  color: var(--huixin-text-light);
  text-decoration: none;
  font-weight: 500;
  border-radius: 8px;
  margin-bottom: 0.5rem;
  transition: all 0.2s;
}
.admin-nav-item:hover {
  background: var(--huixin-bg-soft);
  color: var(--huixin-primary);
}
.admin-nav-item.active {
  background: var(--huixin-primary);
  color: #fff;
  box-shadow: 0 4px 12px rgba(47, 111, 102, 0.2);
}
.admin-footer {
  padding: 1.5rem;
  border-top: 1px solid var(--huixin-border);
}
.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.admin-topbar {
  height: 70px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.admin-content {
  flex: 1;
  overflow-y: auto;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
