<template>
  <article class="card mb-3 p-3 article-card">
    <div class="d-flex gap-3">
      <router-link :to="`/user/${article.authorId}`" class="author-avatar flex-shrink-0">
        <img :src="article.authorAvatar || defaultAvatar" class="rounded-circle border" width="44" height="44" alt="">
      </router-link>

      <div class="flex-grow-1 min-w-0">
        <div class="d-flex align-items-center mb-1 article-meta">
          <router-link :to="`/user/${article.authorId}`" class="fw-semibold text-dark text-decoration-none text-truncate">
            {{ article.authorName || '匿名用户' }}
          </router-link>
          <span class="text-muted mx-1">·</span>
          <time class="text-muted flex-shrink-0" :title="formatDateTime(article.publishTime)">{{ fromNow(article.publishTime) }}</time>
          <span v-if="article.isTop === 1" class="badge badge-top ms-2">置顶</span>
        </div>

        <div class="article-main">
          <div class="article-copy">
            <router-link :to="`/article/${article.id}`" class="text-decoration-none">
              <h2 class="article-title">{{ article.title }}</h2>
            </router-link>
            <p class="text-muted mb-2 small article-summary">{{ summary }}</p>
          </div>

          <router-link v-if="article.coverImageUrl" :to="`/article/${article.id}`" class="article-cover">
            <img :src="article.coverImageUrl" :alt="article.title">
          </router-link>
        </div>

        <div class="d-flex align-items-center text-muted small gap-2 flex-wrap article-footer">
          <span class="category-pill">{{ article.categoryName || '未分类' }}</span>
          <span v-for="tag in (article.tags || [])" :key="tag.id" class="tag-badge" :style="{ backgroundColor: tag.tagColor || '#6B7280' }">
            {{ tag.tagName }}
          </span>
          <span class="ms-auto d-flex align-items-center gap-3 article-stats">
            <span title="阅读量">阅读 {{ compactNumber(article.viewCount) }}</span>
            <span title="点赞数">赞 {{ compactNumber(article.likeCount) }}</span>
            <span title="评论数">评 {{ compactNumber(article.commentCount) }}</span>
          </span>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { clampText, compactNumber, formatDateTime, fromNow } from '@/utils/format'

const props = defineProps({ article: { type: Object, required: true } })
import { DEFAULT_AVATAR as defaultAvatar } from '@/utils/constants'
const summary = computed(() => clampText(props.article.summary || props.article.title, 150))
</script>

<style scoped>
.article-card {
  border-left: 3px solid var(--huixin-primary);
}
.article-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--huixin-shadow-md);
  border-left-color: var(--huixin-accent);
  background: linear-gradient(90deg, rgba(47,111,102,0.025) 0%, rgba(255,255,255,.95) 18%);
}
.author-avatar img {
  object-fit: cover;
  border-color: var(--huixin-border) !important;
}
.article-meta {
  min-width: 0;
  font-size: .84rem;
}
.badge-top {
  background: var(--huixin-accent);
  font-weight: 500;
}
.article-main {
  display: flex;
  align-items: stretch;
  gap: 1rem;
}
.article-copy {
  min-width: 0;
  flex: 1 1 auto;
}
.article-title {
  word-break: break-all;
  margin: 0 0 .35rem;
  color: var(--huixin-text);
  font-size: 1.05rem;
  line-height: 1.45;
  font-weight: 700;
  transition: color .2s;
}
.article-card:hover .article-title {
  word-break: break-all;
  color: var(--huixin-primary);
}
.article-summary {
  line-height: 1.58;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.article-cover {
  width: 150px;
  aspect-ratio: 16 / 10;
  flex: 0 0 auto;
  overflow: hidden;
  border-radius: 8px;
  background: var(--huixin-bg-soft);
}
.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform .25s ease;
}
.article-card:hover .article-cover img {
  transform: scale(1.04);
}
.category-pill {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 2px 10px;
  border-radius: 999px;
  background: var(--huixin-bg-soft);
  color: var(--huixin-primary);
  border: 1px solid rgba(47,111,102,.12);
}
.article-stats {
  white-space: nowrap;
}
@media (max-width: 575.98px) {
  .article-main {
    flex-direction: column;
    gap: .5rem;
  }
  .article-cover {
    width: 100%;
  }
  .article-stats {
    width: 100%;
    justify-content: space-between;
    margin-left: 0 !important;
    padding-top: .35rem;
  }
}
</style>
