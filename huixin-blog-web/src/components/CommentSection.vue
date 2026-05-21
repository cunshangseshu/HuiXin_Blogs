<template>
  <div class="mt-4">
    <h6 class="mb-3">评论 ({{ comments.length }})</h6>
    <!-- 评论输入 -->
    <div v-if="store.isLoggedIn" class="mb-3">
      <textarea v-model="newComment" class="form-control" rows="3" placeholder="写下你的评论..." maxlength="500"></textarea>
      <button class="btn btn-primary btn-sm mt-2" @click="submitComment" :disabled="!newComment.trim()">发表评论</button>
    </div>
    <div v-else class="mb-3 text-muted small">
      <router-link to="/login">登录</router-link> 后参与评论
    </div>
    <!-- 评论列表 -->
    <div v-for="c in comments" :key="c.id" class="border-bottom pb-2 mb-2">
      <div class="d-flex align-items-start">
        <img :src="c.avatarUrl || defaultAvatar" class="rounded-circle me-2" width="32" height="32" style="object-fit:cover">
        <div class="flex-grow-1">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <strong class="small">{{ c.username }}</strong>
              <small class="text-muted ms-2">{{ formatTime(c.createTime) }}</small>
            </div>
            <button v-if="store.user?.id === c.userId" class="btn btn-sm text-danger" @click="$emit('delete', c.id)">&times;</button>
          </div>
          <p class="mb-1 small">{{ c.content }}</p>
          <button v-if="store.isLoggedIn" class="btn btn-sm text-muted p-0" @click="replyingTo = c.id; replyContent = ''">回复</button>
          <!-- 回复输入 -->
          <div v-if="replyingTo === c.id" class="mt-1">
            <input v-model="replyContent" class="form-control form-control-sm" placeholder="回复..." maxlength="500">
            <button class="btn btn-sm btn-outline-primary mt-1" @click="submitReply(c.id, c.userId)">发送</button>
            <button class="btn btn-sm text-muted mt-1" @click="replyingTo = null">取消</button>
          </div>
          <!-- 二级回复 -->
          <div v-for="r in (c.replies || [])" :key="r.id" class="ms-4 mt-1 p-2 bg-light rounded small">
            <strong>{{ r.username }}</strong>
            <span v-if="r.replyToUsername" class="text-muted"> 回复 {{ r.replyToUsername }}</span>
            ：{{ r.content }}
            <button v-if="store.user?.id === r.userId" class="btn btn-sm text-danger p-0 ms-1" @click="$emit('delete', r.id)">&times;</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { commentApi } from '@/api/comment'
const props = defineProps({ articleId: { type: Number, required: true }, comments: { type: Array, default: () => [] } })
const emit = defineEmits(['refresh'])
const store = useUserStore()
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32"><circle cx="16" cy="16" r="16" fill="#8B8682"/></svg>')
const newComment = ref('')
const replyingTo = ref(null)
const replyContent = ref('')
const submitting = ref(false)
function formatTime(t) { if (!t) return ''; return new Date(t).toLocaleDateString('zh-CN') }
async function submitComment() {
  if (!newComment.value.trim() || submitting.value) return
  submitting.value = true
  try { await commentApi.create({ articleId: props.articleId, content: newComment.value }); newComment.value = ''; emit('refresh') } catch (e) { alert(e.message || '评论失败') }
  finally { submitting.value = false }
}
async function submitReply(parentId, replyToUserId) {
  if (!replyContent.value.trim() || submitting.value) return
  submitting.value = true
  try { await commentApi.create({ articleId: props.articleId, content: replyContent.value, parentId, replyToUserId }); replyingTo.value = null; replyContent.value = ''; emit('refresh') } catch (e) { alert(e.message || '回复失败') }
  finally { submitting.value = false }
}
</script>
