<template>
  <div class="row justify-content-center">
    <div class="col-xl-8 col-lg-9">
      <div class="reading-progress" :style="{ width: progress + '%' }"></div>

      <div v-if="loading" class="card p-5 text-center text-muted">
        <div class="spinner-border spinner-border-sm me-2" style="color:var(--huixin-primary)"></div>
        正在加载文章
      </div>

      <EmptyState v-else-if="!article" title="文章不存在" description="这篇文章可能已被删除或暂未发布。">
        <router-link to="/" class="btn btn-primary btn-sm">返回首页</router-link>
      </EmptyState>

      <template v-else>
        <article class="card article-detail p-4 p-lg-5">
          <img v-if="article.coverImageUrl" :src="article.coverImageUrl" :alt="article.title" class="article-cover mb-4">

          <h1 class="article-title">{{ article.title }}</h1>

          <div class="d-flex align-items-center text-muted small mb-3 flex-wrap gap-2">
            <router-link :to="`/user/${article.authorId}`" class="d-flex align-items-center text-decoration-none author-link">
              <img :src="article.authorAvatar || defaultAvatar" class="rounded-circle me-2" width="28" height="28" alt="">
              <span>{{ article.authorName || '匿名用户' }}</span>
            </router-link>
            <span>发布于 {{ formatDateTime(article.publishTime) }}</span>
            <span>阅读 {{ compactNumber(viewCount) }}</span>
            <span>评论 {{ compactNumber(article.commentCount) }}</span>
            <span v-if="article.isOriginal === 1" class="badge badge-original">原创</span>
            <router-link v-if="canEdit" :to="`/article/${article.id}/edit`" class="btn btn-sm btn-outline-secondary ms-auto">编辑</router-link>
          </div>

          <div class="d-flex align-items-center gap-2 flex-wrap mb-4">
            <span class="category-pill">{{ article.categoryName || '未分类' }}</span>
            <span v-for="tag in (article.tags || [])" :key="tag.id" class="tag-badge" :style="{ backgroundColor: tag.tagColor || '#6B7280' }">
              {{ tag.tagName }}
            </span>
          </div>

          <div id="article-content" ref="articleBody" v-html="renderedContent" class="article-body"></div>

          <div class="article-actions">
            <button class="btn btn-lg" :class="liked ? 'btn-accent' : 'btn-outline-primary'" @click="toggleLike" :disabled="likeLoading">
              {{ liked ? '已点赞' : '点赞' }} · {{ compactNumber(likeCount) }}
            </button>
          </div>
        </article>

        <section class="card p-4 mt-3">
          <CommentSection
            :articleId="article.id"
            :articleAuthorId="article.authorId"
            :comments="comments"
            @refresh="loadComments"
            @delete="deleteComment"
          />
        </section>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'
import { statsApi } from '@/api/stats'
import { useUserStore } from '@/store/user'
import CommentSection from '@/components/CommentSection.vue'
import EmptyState from '@/components/EmptyState.vue'
import { compactNumber, formatDateTime } from '@/utils/format'
import { markdownToHtml } from '@/utils/markdown'

const route = useRoute()
const store = useUserStore()
const article = ref(null)
const comments = ref([])
const liked = ref(false)
const likeCount = ref(0)
const viewCount = ref(0)
const likeLoading = ref(false)
const loading = ref(true)
const progress = ref(0)
const articleBody = ref(null)
import { DEFAULT_AVATAR as defaultAvatar } from '@/utils/constants'

const renderedContent = computed(() => article.value?.contentHtml || markdownToHtml(article.value?.content || ''))
const canEdit = computed(() => store.user?.id && article.value?.authorId === store.user.id)

function onScroll() {
  const el = articleBody.value
  if (!el) return
  const rect = el.getBoundingClientRect()
  const height = Math.max(el.offsetHeight, 1)
  const top = -rect.top + window.innerHeight * .45
  progress.value = Math.min(100, Math.max(0, Math.round((top / height) * 100)))
}

async function loadArticle() {
  loading.value = true
  const id = Number(route.params.id)
  try {
    const res = await articleApi.getDetail(id)
    if (res.code === 200) {
      article.value = res.data
      likeCount.value = Number(res.data.likeCount || 0)
      viewCount.value = Number(res.data.viewCount || 0)
    }
  } catch (e) {
    article.value = null
  } finally {
    loading.value = false
  }

  await Promise.all([loadStats(), loadLikeState(), loadComments()])
}

async function loadStats() {
  if (!article.value) return
  try {
    const res = await statsApi.getStats(article.value.id)
    if (res.code === 200 && res.data) {
      likeCount.value = Number(res.data.likeCount ?? likeCount.value)
      viewCount.value = Number(res.data.viewCount ?? viewCount.value)
    }
  } catch (e) {}
}

async function loadLikeState() {
  if (!article.value || !store.isLoggedIn) return
  try {
    const res = await statsApi.hasLiked(article.value.id)
    if (res.code === 200) liked.value = !!res.data?.liked
  } catch (e) {}
}

async function loadComments() {
  if (!article.value) return
  try {
    const res = await commentApi.getByArticle(article.value.id)
    if (res.code === 200) comments.value = res.data || []
  } catch (e) {
    comments.value = []
  }
}

async function toggleLike() {
  if (!store.isLoggedIn) {
    alert('请先登录')
    return
  }
  if (likeLoading.value || !article.value) return
  likeLoading.value = true
  try {
    const res = await statsApi.toggleLike(article.value.id)
    if (res.code === 200) {
      liked.value = !!res.data.liked
      likeCount.value = Number(res.data.likeCount || 0)
    }
  } catch (e) {
    alert('操作失败')
  } finally {
    likeLoading.value = false
  }
}

async function deleteComment(id) {
  if (!confirm('确定删除这条评论？')) return
  try {
    await commentApi.delete(id)
    await loadComments()
  } catch (e) {
    alert('删除失败')
  }
}

onMounted(() => {
  loadArticle()
  window.addEventListener('scroll', onScroll)
})
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.reading-progress {
  position: fixed;
  top: 56px;
  left: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--huixin-primary), var(--huixin-gold), var(--huixin-accent));
  z-index: 1000;
  transition: width .15s linear;
}
.article-detail {
  overflow: hidden;
}
.article-cover {
  width: 100%;
  max-height: 360px;
  object-fit: cover;
  border-radius: 8px;
  background: var(--huixin-bg-soft);
}
.article-title {
  word-break: break-all;
  margin-bottom: .75rem;
  font-size: clamp(1.65rem, 3vw, 2.35rem);
  line-height: 1.28;
  font-weight: 800;
  color: var(--huixin-text);
}
.author-link img {
  object-fit: cover;
}
.badge-original {
  background: var(--huixin-gold);
}
.category-pill {
  display: inline-flex;
  padding: .22rem .7rem;
  border-radius: 999px;
  background: var(--huixin-bg-soft);
  color: var(--huixin-primary);
  border: 1px solid rgba(47,111,102,.12);
  font-size: .85rem;
}
.article-body {
  line-height: 1.95;
  font-size: 1.05rem;
  word-break: break-word;
}
.article-body :deep(h1),
.article-body :deep(h2),
.article-body :deep(h3) {
  margin: 1.6rem 0 .7rem;
  color: var(--huixin-text);
  font-weight: 800;
}
.article-body :deep(p) {
  margin-bottom: 1rem;
}
.article-body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}
.article-body :deep(pre) {
  background: #f3f6f4;
  padding: 1rem;
  border-radius: 8px;
  overflow-x: auto;
  border: 1px solid var(--huixin-border);
}
.article-body :deep(code) {
  background: #edf3f0;
  padding: .12rem .35rem;
  border-radius: 4px;
}
.article-body :deep(pre code) {
  background: transparent;
  padding: 0;
}
.article-body :deep(blockquote) {
  border-left: 4px solid var(--huixin-primary);
  padding: .25rem 0 .25rem 1rem;
  color: var(--huixin-text-light);
  margin: 1rem 0;
  background: var(--huixin-bg-soft);
}
.article-actions {
  display: flex;
  justify-content: center;
  padding-top: 1.5rem;
  margin-top: 1.5rem;
  border-top: 1px solid var(--huixin-border);
}
</style>
