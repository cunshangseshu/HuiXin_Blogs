<template>
  <div class="row justify-content-center">
    <div class="col-lg-8">
      <div v-if="loading" class="text-center py-5 text-muted">加载中...</div>
      <template v-else-if="article">
        <div class="card p-4">
          <h3 class="fw-bold mb-2">{{ article.title }}</h3>
          <div class="d-flex align-items-center text-muted small mb-3">
            <router-link :to="`/user/${article.authorId}`">{{ article.authorName }}</router-link>
            <span class="mx-2">·</span>
            <span>{{ formatTime(article.publishTime) }}</span>
            <span class="mx-2">·</span>
            <span>&#x1F441; {{ article.viewCount || 0 }}</span>
          </div>
          <div class="mb-2">
            <span class="badge bg-light text-dark me-1">{{ article.categoryName }}</span>
            <span v-for="t in (article.tags || [])" :key="t.id" class="tag-badge me-1" :style="{backgroundColor:t.tagColor}">{{ t.tagName }}</span>
          </div>
          <hr>
          <div v-html="article.contentHtml || article.content" class="article-body" style="line-height:1.8;font-size:1.05rem"></div>
          <hr>
          <!-- 点赞 -->
          <div class="d-flex justify-content-center mb-3">
            <button class="btn" :class="liked ? 'btn-danger' : 'btn-outline-danger'" @click="toggleLike">
              &#x2764; {{ likeCount }} {{ liked ? '已点赞' : '点赞' }}
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
import { ref, onMounted } from 'vue'
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
const likeLoading = ref(false)
const loading = ref(true)
function formatTime(t) { if (!t) return ''; return new Date(t).toLocaleString('zh-CN') }

async function loadArticle() {
  const id = Number(route.params.id)
  try {
    const res = await articleApi.getDetail(id)
    if (res.code === 200) { article.value = res.data; likeCount.value = res.data.likeCount || 0 }
  } catch (e) {} finally { loading.value = false }
  // 记录阅读
  try { await statsApi.recordView(id) } catch (e) {}
  // 检查点赞
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
onMounted(loadArticle)
</script>
