<template>
  <div class="row justify-content-center">
    <div class="col-lg-8">
      <!-- 阅读进度条 -->
      <div class="reading-progress" :style="{ width: progress + '%' }"></div>
      <div v-if="loading" class="text-center py-5 text-muted">
        <div class="spinner-border spinner-border-sm me-2" style="color:var(--huixin-primary)"></div>墨香氤氲，加载中...
      </div>
      <template v-else-if="article">
        <div class="card p-4 p-lg-5">
          <!-- 标题 -->
          <h3 class="fw-bold mb-2" style="font-family:var(--font-calligraphy); letter-spacing:1px;">{{ article.title }}</h3>
          <!-- 作者栏 -->
          <div class="d-flex align-items-center text-muted small mb-3 flex-wrap">
            <router-link :to="`/user/${article.authorId}`" class="d-flex align-items-center text-decoration-none me-2">
              <img :src="article.authorAvatar || defaultAvatar" class="rounded-circle me-1" width="22" height="22" style="object-fit:cover">
              <span>{{ article.authorName || '匿名墨客' }}</span>
            </router-link>
            <span class="mx-1">·</span>
            <span>{{ formatTime(article.publishTime) }}</span>
            <span class="mx-1">·</span>
            <span>&#x1F441; {{ article.viewCount || 0 }}</span>
            <span v-if="article.isOriginal" class="badge ms-2" style="background:var(--huixin-gold);color:#fff;font-size:.65rem">&#x2728; 原创</span>
            <span v-if="store.user?.id === article.authorId" class="ms-auto">
              <router-link :to="`/article/${article.id}/edit`" class="btn btn-sm btn-outline-secondary">编辑</router-link>
            </span>
          </div>
          <!-- 分类标签 -->
          <div class="mb-3">
            <span class="badge border me-1" style="background:var(--huixin-bg);color:var(--huixin-primary)">&#x1F4C2; {{ article.categoryName }}</span>
            <span v-for="t in (article.tags || [])" :key="t.id" class="tag-badge me-1" :style="{backgroundColor:t.tagColor}">{{ t.tagName }}</span>
          </div>
          <hr>
          <!-- 正文 -->
          <div id="article-content" ref="articleBody" v-html="article.contentHtml || article.content" class="article-body"></div>
          <hr>
          <!-- 点赞 -->
          <div class="d-flex justify-content-center mb-2">
            <button class="btn btn-lg" :class="liked ? 'btn-accent' : 'btn-outline-primary'" @click="toggleLike" :disabled="likeLoading">
              &#x2764; {{ likeCount }} {{ liked ? '已点赞' : '点个赞' }}
            </button>
          </div>
        </div>
        <!-- 评论区 -->
        <div class="card p-4 mt-3">
          <CommentSection :articleId="article.id" :comments="comments" @refresh="loadComments" @delete="deleteComment" />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'
import { statsApi } from '@/api/stats'
import { useUserStore } from '@/store/user'
import CommentSection from '@/components/CommentSection.vue'
const route = useRoute()
const store = useUserStore()
const article = ref(null); const comments = ref([])
const liked = ref(false); const likeCount = ref(0)
const likeLoading = ref(false); const loading = ref(true)
const progress = ref(0)
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22"><circle cx="11" cy="11" r="11" fill="#8B8682"/></svg>')
function formatTime(t) { if (!t) return ''; return new Date(t).toLocaleString('zh-CN') }

function onScroll() {
  const el = document.getElementById('article-content')
  if (!el) return
  const rect = el.getBoundingClientRect()
  const h = el.offsetHeight
  const top = -rect.top + window.innerHeight * .5
  progress.value = Math.min(100, Math.max(0, Math.round((top / h) * 100)))
}

async function loadArticle() {
  const id = Number(route.params.id)
  try {
    const res = await articleApi.getDetail(id)
    if (res.code === 200) { article.value = res.data; likeCount.value = res.data.likeCount || 0 }
  } catch (e) {} finally { loading.value = false }
  try { await statsApi.recordView(id) } catch (e) {}
  if (store.isLoggedIn) {
    try {
      const lres = await statsApi.hasLiked(id)
      if (lres.code === 200) liked.value = lres.data?.liked
    } catch (e) {}
  }
  loadComments()
}
async function loadComments() {
  try {
    const res = await commentApi.getByArticle(Number(route.params.id))
    if (res.code === 200) comments.value = res.data || []
  } catch (e) {}
}
async function toggleLike() {
  if (!store.isLoggedIn) return alert('请先登录')
  if (likeLoading.value) return
  likeLoading.value = true
  try {
    const res = await statsApi.toggleLike(Number(route.params.id))
    if (res.code === 200) { liked.value = res.data.liked; likeCount.value = res.data.likeCount }
  } catch (e) { alert('操作失败') }
  finally { likeLoading.value = false }
}
async function deleteComment(id) {
  if (!confirm('确定删除？')) return
  try { await commentApi.delete(id); loadComments() } catch (e) { alert('删除失败') }
}
onMounted(() => { loadArticle(); window.addEventListener('scroll', onScroll) })
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.reading-progress {
  position: fixed; top: 56px; left: 0; height: 3px;
  background: linear-gradient(90deg, var(--huixin-primary), var(--huixin-gold), var(--huixin-accent));
  z-index: 1000; transition: width .15s linear;
}
.article-body { line-height: 1.9; font-size: 1.05rem; word-break: break-word; }
.article-body :deep(img) { max-width: 100%; border-radius: 6px; }
.article-body :deep(pre) { background: #f8f5f0; padding: 1rem; border-radius: 8px; overflow-x: auto; }
.article-body :deep(blockquote) { border-left: 4px solid var(--huixin-primary); padding-left: 1rem; color: var(--huixin-text-light); margin: 1rem 0; }
</style>
