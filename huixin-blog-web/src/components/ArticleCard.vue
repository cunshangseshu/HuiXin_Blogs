<template>
  <div class="card mb-3 p-3">
    <div class="d-flex align-items-start">
      <!-- 作者头像 -->
      <router-link :to="`/user/${article.authorId}`" class="me-3 flex-shrink-0">
        <img :src="article.authorAvatar || defaultAvatar" class="rounded-circle" width="40" height="40" style="object-fit:cover">
      </router-link>
      <div class="flex-grow-1 min-w-0">
        <!-- 作者 + 时间 -->
        <div class="d-flex align-items-center mb-1">
          <router-link :to="`/user/${article.authorId}`" class="fw-bold text-dark text-decoration-none small">
            {{ article.authorName || '未知用户' }}
          </router-link>
          <span class="text-muted mx-1">·</span>
          <small class="text-muted">{{ formatTime(article.publishTime) }}</small>
          <span v-if="article.isTop" class="badge bg-warning text-dark ms-2" style="font-size:0.65rem">置顶</span>
        </div>
        <!-- 标题 -->
        <router-link :to="`/article/${article.id}`" class="text-decoration-none">
          <h6 class="mb-1 text-dark fw-bold">{{ article.title }}</h6>
        </router-link>
        <!-- 摘要 -->
        <p class="text-muted mb-2 small" style="line-height:1.4">{{ article.summary || article.title }}</p>
        <!-- 底部操作栏 -->
        <div class="d-flex align-items-center text-muted small gap-3">
          <span class="badge bg-light text-dark">{{ article.categoryName || '未分类' }}</span>
          <span v-for="tag in (article.tags || [])" :key="tag.id" class="tag-badge" :style="{ backgroundColor: tag.tagColor || '#6B7280' }">
            {{ tag.tagName }}
          </span>
          <span class="ms-auto d-flex align-items-center gap-3">
            <span>&#x1F441; {{ article.viewCount || 0 }}</span>
            <span>&#x2764; {{ article.likeCount || 0 }}</span>
            <span>&#x1F4AC; {{ article.commentCount || 0 }}</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ article: { type: Object, required: true } })
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="40" height="40"><circle cx="20" cy="20" r="20" fill="#8B8682"/></svg>')
function formatTime(t) { if (!t) return ''; const d = new Date(t); return d.toLocaleDateString('zh-CN') }
</script>
