<template>
  <div class="row justify-content-center">
    <div class="col-lg-8">
      <!-- 用户信息卡片 -->
      <div class="card p-4 mb-3 animate-float">
        <div class="d-flex align-items-start flex-wrap">
          <img :src="store.user?.avatarUrl || defaultAvatar" class="rounded-circle border border-2 me-3" width="64" height="64" style="object-fit:cover; border-color:var(--huixin-border) !important;">
          <div class="flex-grow-1">
            <h5 class="mb-1" style="font-family:var(--font-calligraphy); letter-spacing:1px;">{{ store.user?.nickname || store.user?.username }}</h5>
            <span class="badge me-1" :class="store.isBlogger ? 'badge-gold' : 'bg-secondary'">{{ store.isBlogger ? '博主' : '普通用户' }}</span>
            <small class="text-muted ms-2">邮箱：{{ store.user?.email || '未设置' }}</small>
          </div>
          <button class="btn btn-sm btn-outline-secondary mt-2 mt-sm-0" data-bs-toggle="collapse" data-bs-target="#editProfile">编辑资料</button>
        </div>
        <!-- 编辑资料折叠区 -->
        <div class="collapse mt-3" id="editProfile">
          <div class="card bg-light p-3">
            <div class="row g-2">
              <div class="col-md-6"><input v-model="profile.nickname" class="form-control form-control-sm" placeholder="昵称"></div>
              <div class="col-md-6"><input v-model="profile.bio" class="form-control form-control-sm" placeholder="个人简介"></div>
            </div>
            <button class="btn btn-primary btn-sm mt-2" @click="updateProfile">保存</button>
          </div>
        </div>
      </div>

      <!-- 博主专属：我的文章 -->
      <div class="card p-4 mb-3" v-if="store.isBlogger">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h6 class="mb-0" style="font-family:var(--font-calligraphy); letter-spacing:1px;">&#x270F; 我的文章（{{ myArticles.length }}篇）</h6>
          <router-link to="/article/new" class="btn btn-primary btn-sm">&#x2795; 写文章</router-link>
        </div>
        <div v-if="!myArticles.length" class="text-center py-4 text-muted small">
          &#x1F4DD; 还没有写过文章，点击右上角开始创作吧
        </div>
        <div v-for="a in myArticles" :key="a.id" class="d-flex align-items-center border-bottom py-2 px-2 my-article-row">
          <div class="flex-grow-1 min-w-0">
            <router-link :to="`/article/${a.id}`" class="text-dark text-decoration-none fw-bold small d-block text-truncate">
              {{ a.title }}
            </router-link>
            <small class="text-muted">
              {{ formatTime(a.publishTime) }} · &#x1F441; {{ a.viewCount || 0 }} · &#x2764; {{ a.likeCount || 0 }} · &#x1F4AC; {{ a.commentCount || 0 }}
            </small>
          </div>
          <div class="flex-shrink-0 ms-2">
            <router-link :to="`/article/${a.id}/edit`" class="btn btn-sm btn-outline-secondary me-1">编辑</router-link>
            <button class="btn btn-sm btn-outline-danger" @click="deleteMyArticle(a.id)">删除</button>
          </div>
        </div>
      </div>

      <!-- 修改密码 -->
      <div class="card p-4 mb-3">
        <h6 class="mb-3" style="font-family:var(--font-calligraphy); letter-spacing:1px;">&#x1F512; 修改密码</h6>
        <div class="row g-2">
          <div class="col-md-4"><input v-model="pwd.oldPassword" type="password" class="form-control form-control-sm" placeholder="旧密码"></div>
          <div class="col-md-4"><input v-model="pwd.newPassword" type="password" class="form-control form-control-sm" placeholder="新密码（8-20位）"></div>
          <div class="col-md-4"><button class="btn btn-primary btn-sm w-100" @click="changePassword">修改密码</button></div>
        </div>
      </div>

      <!-- 申请博主 -->
      <div class="card p-4" v-if="!store.isBlogger">
        <h6 class="mb-3" style="font-family:var(--font-calligraphy); letter-spacing:1px;">&#x1F3C5; 申请成为博主</h6>
        <textarea v-model="applyReason" class="form-control form-control-sm mb-2" rows="3" placeholder="请说明申请理由..."></textarea>
        <button class="btn btn-accent btn-sm" @click="applyBlogger">提交申请</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { articleApi } from '@/api/article'
import { userApi } from '@/api/user'
const store = useUserStore()
const myArticles = ref([])
const profile = reactive({ nickname: '', bio: '' })
const pwd = reactive({ oldPassword: '', newPassword: '' })
const applyReason = ref('')
const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="64" height="64"><circle cx="32" cy="32" r="32" fill="#8B8682"/></svg>')
function formatTime(t) { if (!t) return ''; return new Date(t).toLocaleDateString('zh-CN') }

async function loadMyArticles() {
  if (!store.isBlogger) return
  try { const r = await articleApi.listByUser(store.user.id); if (r.code === 200) myArticles.value = r.data.records || [] } catch (e) {}
}
async function deleteMyArticle(id) {
  if (!confirm('确定删除这篇文章？')) return
  try { await articleApi.delete(id); loadMyArticles() } catch (e) { alert('删除失败') }
}
async function updateProfile() {
  try { await userApi.updateInfo(profile); store.fetchUserInfo(); alert('更新成功') } catch (e) { alert('更新失败') }
}
async function changePassword() {
  if (!pwd.oldPassword || !pwd.newPassword) return alert('请填写完整')
  if (pwd.newPassword.length < 8) return alert('新密码至少8位')
  try { await userApi.changePassword(pwd); alert('密码修改成功，请重新登录') } catch (e) { alert(e.message || '修改失败') }
}
async function applyBlogger() {
  if (!applyReason.value.trim()) return alert('请填写申请理由')
  try { await userApi.applyBlogger(applyReason.value); alert('申请已提交') } catch (e) { alert('申请失败') }
}
onMounted(async () => { await store.fetchUserInfo(); loadMyArticles() })
</script>

<style scoped>
.badge-gold { background: var(--huixin-gold); color: #fff; font-weight: normal; }
.my-article-row:hover { background: rgba(74,107,93,.03); border-radius: 6px; }
</style>
