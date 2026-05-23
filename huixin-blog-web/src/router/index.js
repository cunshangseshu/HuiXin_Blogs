import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  { path: '/', name: 'Home', component: () => import('@/views/Home.vue') },
  { path: '/about', name: 'About', component: () => import('@/views/About.vue') },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { guestOnly: true } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { guestOnly: true } },
  { path: '/article/:id', name: 'ArticleDetail', component: () => import('@/views/ArticleDetail.vue') },
  { path: '/article/new', name: 'ArticleNew', component: () => import('@/views/ArticleEdit.vue'), meta: { requiresAuth: true, requiresBlogger: true } },
  { path: '/article/:id/edit', name: 'ArticleEdit', component: () => import('@/views/ArticleEdit.vue'), meta: { requiresAuth: true, requiresBlogger: true } },
  { path: '/category/:id', name: 'Category', component: () => import('@/views/Home.vue') },
  { path: '/search', name: 'Search', component: () => import('@/views/Search.vue') },
  { path: '/user/center', name: 'UserCenter', component: () => import('@/views/UserCenter.vue'), meta: { requiresAuth: true } },
  { path: '/user/:id', name: 'UserProfile', component: () => import('@/views/UserProfile.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('accessToken')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  const store = useUserStore()
  if (token && !store.user) {
    await store.fetchUserInfo()
  }

  if (to.meta.guestOnly && store.isLoggedIn) {
    next({ name: 'Home' })
    return
  }

  if (to.meta.requiresBlogger && !store.isBlogger) {
    next({ name: 'UserCenter', query: { tab: 'apply' } })
    return
  }

  next()
})

export default router
