<template>
  <div class="card mb-3 p-3 article-card">
    <div class="d-flex align-items-start">
      <router-link :to="`/user/${article.authorId}`" class="me-3 flex-shrink-0">
        <img :src="article.authorAvatar || defaultAvatar" class="rounded-circle border border-2" width="44" height="44" style="object-fit:cover; border-color: var(--huixin-border) !important;">
      </router-link>
      <div class="flex-grow-1 min-w-0">
        <div class="d-flex align-items-center mb-1">
          <router-link :to="`/user/${article.authorId}`" class="fw-bold text-dark text-decoration-none small">
            {{ article.authorName || '匿名墨客' }}
          </router-link>
          <span class="text-muted mx-1">·</span>
          <small class="text-muted">{{ formatTime(article.publishTime) }}</small>
          <span v-if="article.isTop" class="badge ms-2" style="background:var(--huixin-accent); font-size:.65rem">&#x1F4CC; 置顶</span>
          <span v-if="article.isOriginal" class="badge ms-1" style="background:var(--huixin-gold); color:#fff; font-size:.65rem">&#x2728; 原创</span>
        </div>
        <router-link :to="`/article/${article.id}`" class="text-decoration-none">
          <h6 class="mb-1 fw-bold article-title">{{ article.title }}</h6>
        </router-link>
        <p class="text-muted mb-2 small article-summary">{{ article.summary || article.title }}</p>
        <div class="d-flex align-items-center text-muted small gap-2 flex-wrap">
          <span class="badge border" style="background:var(--huixin-bg); color:var(--huixin-primary); font-weight:normal">
            &#x1F4C2; {{ article.categoryName || '未分类' }}
          </span>
          <span v-for="tag in (article.tags || [])" :key="tag.id" class="tag-badge" :style="{ backgroundColor: tag.tagColor || '#6B7280' }">
            {{ tag.tagName }}
          </span>
          <span class="ms-auto d-flex align-items-center gap-3">
            <span class="stat-item">&#x1F441; {{ article.viewCount || 0 }}</span>
            <span class="stat-item">&#x2764; {{ article.likeCount || 0 }}</span>
            <span class="stat-item">&#x1F4AC; {{ article.commentCount || 0 }}</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ article: { type: Object, required: true } })
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="44" height="44"><circle cx="22" cy="22" r="22" fill="#8B8682"/></svg>')
function formatTime(t) { if (!t) return ''; const d = new Date(t); return d.toLocaleDateString('zh-CN') }
</script>

<style scoped>
.article-card {
  border-left: 3px solid var(--huixin-primary);
  transition: all .2s;
}
.article-card:hover {
  border-left-color: var(--huixin-accent);
  background: linear-gradient(90deg, rgba(74,107,93,0.02) 0%, transparent 10%);
}
.article-title {
  color: var(--huixin-text);
  transition: color .2s;
}
.article-card:hover .article-title { color: var(--huixin-primary); }
.article-summary {
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.stat-item { transition: color .15s; }
.stat-item:hover { color: var(--huixin-accent); }
</style>
