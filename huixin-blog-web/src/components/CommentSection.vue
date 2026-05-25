<template>
  <div class="comment-section">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2 class="comment-title">评论</h2>
      <small class="text-muted">{{ totalCount }} 条</small>
    </div>

    <div v-if="store.isLoggedIn" class="comment-editor mb-4">
      <textarea v-model="newComment" class="form-control" rows="3" placeholder="写下你的评论" maxlength="500"></textarea>
      <div class="d-flex justify-content-between align-items-center mt-2">
        <small class="text-muted">{{ newComment.length }}/500</small>
        <button class="btn btn-primary btn-sm" @click="submitComment" :disabled="!newComment.trim() || submitting">
          {{ submitting ? '发布中...' : '发表评论' }}
        </button>
      </div>
    </div>
    <div v-else class="login-tip mb-4">
      <router-link :to="{ name: 'Login', query: { redirect: $route.fullPath } }">登录</router-link>
      后参与评论
    </div>

    <div v-if="comments.length" class="comment-list">
      <article v-for="comment in comments" :key="comment.id" class="comment-item">
        <img :src="comment.avatarUrl || defaultAvatar" class="comment-avatar" alt="">
        <div class="comment-body">
          <div class="d-flex justify-content-between gap-2">
            <div class="comment-meta">
              <strong>{{ comment.username || '匿名用户' }}</strong>
              <span>{{ fromNow(comment.createTime) }}</span>
            </div>
            <button v-if="canDelete(comment)" class="btn btn-sm btn-link text-danger p-0" @click="emit('delete', comment.id)">删除</button>
          </div>
          <p class="comment-content">{{ comment.content }}</p>

          <button v-if="store.isLoggedIn" class="btn btn-sm btn-link p-0 reply-btn" @click="startReply(comment)">
            回复
          </button>

          <div v-if="replyingTo === comment.id" class="reply-editor mt-2">
            <input v-model="replyContent" class="form-control form-control-sm" :placeholder="`回复 ${comment.username || '用户'}`" maxlength="500">
            <div class="d-flex justify-content-between align-items-center mt-1">
<style scoped>
@import "@/styles/responsive.css";
              <div>
                <button class="btn btn-sm btn-outline-secondary me-1" @click="replyingTo = null">取消</button>
                <button class="btn btn-sm btn-outline-primary" @click="submitReply(comment.id, comment.userId)" :disabled="!replyContent.trim() || submitting">发送</button>
              </div>
            </div>
          </div>

          <div v-if="comment.replies?.length" class="reply-list">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <div class="d-flex justify-content-between gap-2">
                <div>
                  <strong>{{ reply.username || '匿名用户' }}</strong>
                  <span v-if="reply.replyToUsername" class="text-muted"> 回复 {{ reply.replyToUsername }}</span>
                  <small class="text-muted ms-2">{{ fromNow(reply.createTime) }}</small>
                </div>
                <button v-if="canDelete(reply)" class="btn btn-sm btn-link text-danger p-0" @click="emit('delete', reply.id)">删除</button>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
            </div>
          </div>
        </div>
      </article>
    </div>

    <div v-else class="text-muted small py-3 text-center">还没有评论</div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useUserStore } from '@/store/user'
import { commentApi } from '@/api/comment'
import { fromNow } from '@/utils/format'

const props = defineProps({
  articleId: { type: Number, required: true },
  articleAuthorId: { type: Number, default: null },
  comments: { type: Array, default: () => [] }
})
const emit = defineEmits(['refresh', 'delete'])
const store = useUserStore()
import { DEFAULT_AVATAR as defaultAvatar } from '@/utils/constants'
const newComment = ref('')
const replyingTo = ref(null)
const replyContent = ref('')
const submitting = ref(false)
const totalCount = computed(() => props.comments.reduce((sum, comment) => sum + 1 + (comment.replies?.length || 0), 0))

function canDelete(comment) {
  return store.user?.id === comment.userId || store.user?.id === props.articleAuthorId
}

function startReply(comment) {
  replyingTo.value = comment.id
  replyContent.value = ''
}

async function submitComment() {
  if (!newComment.value.trim() || submitting.value) return
  submitting.value = true
  try {
    await commentApi.create({ articleId: props.articleId, content: newComment.value.trim() })
    newComment.value = ''
    emit('refresh')
  } catch (e) {
    alert(e.message || '评论失败')
  } finally {
    submitting.value = false
  }
}

async function submitReply(parentId, replyToUserId) {
  if (!replyContent.value.trim() || submitting.value) return
  submitting.value = true
  try {
    await commentApi.create({
      articleId: props.articleId,
      content: replyContent.value.trim(),
      parentId,
      replyToUserId
    })
    replyingTo.value = null
    replyContent.value = ''
    emit('refresh')
  } catch (e) {
    alert(e.message || '回复失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.comment-title {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 800;
}
.comment-editor textarea {
  resize: vertical;
}
.login-tip {
  padding: .8rem 1rem;
  border-radius: 8px;
  background: var(--huixin-bg-soft);
  color: var(--huixin-text-light);
  font-size: .9rem;
}
.comment-list {
  display: grid;
  gap: 1.2rem;
}
.comment-item {
  display: grid;
  grid-template-columns: 36px 1fr;
  gap: .8rem;
  padding-bottom: 1.2rem;
  border-bottom: 1px solid var(--huixin-border);
}
.comment-item:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}
.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}
.comment-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: .45rem;
  font-size: .88rem;
}
.comment-meta span {
  color: var(--huixin-text-light);
}
.comment-content {
  margin: .3rem 0 .2rem;
  white-space: pre-wrap;
  word-break: break-word;
}
.reply-btn {
  color: var(--huixin-primary);
  text-decoration: none;
}
.reply-list {
  display: grid;
  gap: .5rem;
  margin-top: .75rem;
}
.reply-item {
  padding: .65rem .8rem;
  border-radius: 8px;
  background: var(--huixin-bg-soft);
  font-size: .9rem;
}
.reply-content {
  margin-top: .25rem;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
