<template>
  <div class="row justify-content-center">
    <div class="col-xl-9 col-lg-10">
      <section class="profile-header card p-4 mb-3">
        <img :src="store.user?.avatarUrl || defaultAvatar" class="profile-avatar" alt="">
        <div class="profile-copy">
          <h1>{{ store.user?.nickname || store.user?.username || '用户中心' }}</h1>
          <div class="d-flex flex-wrap align-items-center gap-2">
            <span class="role-badge" :class="{ blogger: store.isBlogger }">{{ store.isBlogger ? '博主' : '普通用户' }}</span>
            <small class="text-muted">{{ store.user?.email || '未设置邮箱' }}</small>
          </div>
          <p class="mb-0 text-muted">{{ store.user?.bio || '还没有填写个人简介' }}</p>
        </div>
        <router-link v-if="store.user?.id" :to="`/user/${store.user.id}`" class="btn btn-outline-secondary btn-sm ms-auto">查看主页</router-link>
      </section>

      <div class="center-tabs mb-3">
        <button v-for="tab in tabs" :key="tab.key" class="tab-btn" :class="{ active: activeTab === tab.key }" @click="setTab(tab.key)">
          {{ tab.label }}
        </button>
      </div>

      <section v-if="activeTab === 'profile'" class="card p-4">
        <h2 class="section-title">资料设置</h2>
        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label">昵称</label>
            <input v-model.trim="profile.nickname" class="form-control" maxlength="20" placeholder="1-20 个字符">
          </div>
          <div class="col-md-6">
            <label class="form-label">头像 URL</label>
            <input v-model.trim="avatarUrl" class="form-control" placeholder="https:// 或 data:image/">
          </div>
          <div class="col-12">
            <label class="form-label">个人简介</label>
            <textarea v-model.trim="profile.bio" class="form-control" rows="3" maxlength="200" placeholder="介绍一下自己"></textarea>
            <div class="form-hint">{{ profile.bio.length }}/200</div>
          </div>
        </div>
        <div v-if="avatarUrl" class="avatar-preview mt-3">
          <img :src="avatarUrl" alt="头像预览">
        </div>
        <div class="mt-3 d-flex gap-2">
          <button class="btn btn-primary" @click="updateProfile" :disabled="savingProfile">保存资料</button>
          <button class="btn btn-outline-primary" @click="updateAvatar" :disabled="savingProfile || !avatarUrl">更新头像</button>
        </div>
      </section>

      <section v-else-if="activeTab === 'articles'" class="card p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h2 class="section-title mb-0">我的文章</h2>
          <router-link v-if="store.isBlogger" to="/article/new" class="btn btn-primary btn-sm">写文章</router-link>
        </div>

        <template v-if="store.isBlogger">
          <div v-if="articleLoading" class="text-muted small py-3">加载文章中...</div>
          <template v-else-if="myArticles.length">
            <div v-for="article in myArticles" :key="article.id" class="article-row">
              <div class="min-w-0">
                <router-link :to="`/article/${article.id}`" class="article-row-title">{{ article.title }}</router-link>
                <small class="text-muted">
                  {{ formatDate(article.publishTime) }} · 阅读 {{ compactNumber(article.viewCount) }} · 赞 {{ compactNumber(article.likeCount) }} · 评论 {{ compactNumber(article.commentCount) }}
                </small>
              </div>
              <div class="article-row-actions">
                <router-link :to="`/article/${article.id}/edit`" class="btn btn-sm btn-outline-secondary">编辑</router-link>
                <button class="btn btn-sm btn-outline-danger" @click="deleteMyArticle(article.id)">删除</button>
              </div>
            </div>
            <Pagination v-if="articlePages > 1" :current="articlePage" :pages="articlePages" @change="loadMyArticles" />
          </template>
          <EmptyState v-else title="还没有文章" description="发布文章后会在这里管理。">
            <router-link to="/article/new" class="btn btn-primary btn-sm">开始写作</router-link>
          </EmptyState>
        </template>

        <EmptyState v-else title="暂未开通写作权限" description="申请成为博主后可以发布和管理文章。">
          <button class="btn btn-primary btn-sm" @click="setTab('apply')">申请博主</button>
        </EmptyState>
      </section>

      <section v-else-if="activeTab === 'security'" class="card p-4">
        <h2 class="section-title">账号安全</h2>
        <div class="row g-3">
          <div class="col-md-4">
            <label class="form-label">旧密码</label>
            <input v-model="pwd.oldPassword" type="password" class="form-control" autocomplete="current-password">
          </div>
          <div class="col-md-4">
            <label class="form-label">新密码</label>
            <input v-model="pwd.newPassword" type="password" class="form-control" autocomplete="new-password" placeholder="8-20位，含字母和数字">
          </div>
          <div class="col-md-4">
            <label class="form-label">确认新密码</label>
            <input v-model="pwd.confirmPassword" type="password" class="form-control" autocomplete="new-password">
          </div>
        </div>
        <button class="btn btn-primary mt-3" @click="changePassword" :disabled="passwordSaving">修改密码</button>
      </section>

      <section v-else class="card p-4">
        <h2 class="section-title">申请成为博主</h2>
        <template v-if="store.isBlogger">
          <EmptyState title="你已经是博主" description="现在可以发布文章并管理自己的内容。">
            <router-link to="/article/new" class="btn btn-primary btn-sm">写文章</router-link>
          </EmptyState>
        </template>
        <template v-else>
          <textarea v-model.trim="applyReason" class="form-control" rows="5" maxlength="500" placeholder="请说明申请理由，至少 10 个字符"></textarea>
          <div class="form-hint">{{ applyReason.length }}/500</div>
          <button class="btn btn-accent mt-3" @click="applyBlogger" :disabled="applySaving || applyReason.length < 10">
            {{ applySaving ? '提交中...' : '提交申请' }}
          </button>
        </template>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { articleApi } from '@/api/article'
import { userApi } from '@/api/user'
import EmptyState from '@/components/EmptyState.vue'
import Pagination from '@/components/Pagination.vue'
import { compactNumber, formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const tabs = [
  { key: 'profile', label: '资料' },
  { key: 'articles', label: '文章' },
  { key: 'security', label: '安全' },
  { key: 'apply', label: '博主申请' }
]
const activeTab = computed(() => route.query.tab || 'profile')
const myArticles = ref([])
const articlePage = ref(1)
const articlePages = ref(1)
const articleLoading = ref(false)
const profile = reactive({ nickname: '', bio: '' })
const avatarUrl = ref('')
const pwd = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const applyReason = ref('')
const savingProfile = ref(false)
const passwordSaving = ref(false)
const applySaving = ref(false)
import { DEFAULT_AVATAR as defaultAvatar } from '@/utils/constants'

function setTab(tab) {
  router.replace({ query: { ...route.query, tab } })
}

function syncProfile() {
  profile.nickname = store.user?.nickname || ''
  profile.bio = store.user?.bio || ''
  avatarUrl.value = store.user?.avatarUrl || ''
}

async function loadMyArticles(p = 1) {
  if (!store.isBlogger || !store.user?.id) return
  articleLoading.value = true
  try {
    const res = await articleApi.listByUser(store.user.id, p, 8)
    if (res.code === 200 && res.data) {
      myArticles.value = res.data.records || []
      articlePage.value = Number(res.data.current || p)
      articlePages.value = Number(res.data.pages || 1)
    }
  } catch (e) {
    myArticles.value = []
  } finally {
    articleLoading.value = false
  }
}

async function deleteMyArticle(id) {
  if (!confirm('确定删除这篇文章？')) return
  try {
    await articleApi.delete(id)
    await loadMyArticles(articlePage.value)
  } catch (e) {
    alert('删除失败')
  }
}

async function updateProfile() {
  if (profile.nickname && profile.nickname.length > 20) return alert('昵称不能超过20个字符')
  savingProfile.value = true
  try {
    await userApi.updateInfo({ nickname: profile.nickname || null, bio: profile.bio || null })
    await store.fetchUserInfo()
    syncProfile()
    alert('资料已保存')
  } catch (e) {
    alert(e.message || '更新失败')
  } finally {
    savingProfile.value = false
  }
}

async function updateAvatar() {
  if (!/^(https?:\/\/|data:image\/).+/.test(avatarUrl.value)) return alert('头像 URL 格式不正确')
  savingProfile.value = true
  try {
    await userApi.updateAvatar(avatarUrl.value)
    await store.fetchUserInfo()
    syncProfile()
    alert('头像已更新')
  } catch (e) {
    alert(e.message || '头像更新失败')
  } finally {
    savingProfile.value = false
  }
}

async function changePassword() {
  if (!pwd.oldPassword || !pwd.newPassword || !pwd.confirmPassword) return alert('请填写完整')
  if (pwd.newPassword !== pwd.confirmPassword) return alert('两次新密码不一致')
  if (!/^(?=\S+$)(?=.*[a-zA-Z])(?=.*\d).{8,20}$/.test(pwd.newPassword)) return alert('新密码需为8-20位，包含字母和数字，且不能包含空格')
  passwordSaving.value = true
  try {
    await userApi.changePassword({ oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    store.clearUser()
    alert('密码修改成功，请重新登录')
    router.push('/login')
  } catch (e) {
    alert(e.message || '修改失败')
  } finally {
    passwordSaving.value = false
  }
}

async function applyBlogger() {
  if (applyReason.value.length < 10) return alert('申请理由至少10个字符')
  applySaving.value = true
  try {
    await userApi.applyBlogger({ applyReason: applyReason.value })
    applyReason.value = ''
    alert('申请已提交，请等待审核')
  } catch (e) {
    alert(e.message || '申请失败')
  } finally {
    applySaving.value = false
  }
}

watch(() => store.user, () => syncProfile())
watch(activeTab, tab => {
  if (tab === 'articles') loadMyArticles()
})

onMounted(async () => {
  await store.fetchUserInfo()
  syncProfile()
  if (activeTab.value === 'articles') loadMyArticles()
})
</script>

<style scoped>
.profile-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  border-top: 3px solid var(--huixin-primary);
}
.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--huixin-border);
}
.profile-copy {
  min-width: 0;
}
.profile-copy h1 {
  margin: 0 0 .3rem;
  font-size: 1.35rem;
  font-weight: 800;
}
.role-badge {
  padding: .16rem .55rem;
  border-radius: 999px;
  background: #6c757d;
  color: #fff;
  font-size: .78rem;
}
.role-badge.blogger {
  background: var(--huixin-gold);
}
.center-tabs {
  display: flex;
  gap: .5rem;
  overflow-x: auto;
}
.tab-btn {
  flex: 0 0 auto;
  border: 1px solid var(--huixin-border);
  border-radius: 999px;
  background: #fff;
  color: var(--huixin-text);
  padding: .38rem 1rem;
}
.tab-btn.active,
.tab-btn:hover {
  border-color: var(--huixin-primary);
  color: var(--huixin-primary);
  background: var(--huixin-bg-soft);
}
.section-title {
  margin: 0 0 1rem;
  font-size: 1.1rem;
  font-weight: 800;
}
.form-hint {
  text-align: right;
  color: var(--huixin-text-light);
  font-size: .8rem;
  margin-top: .25rem;
}
.avatar-preview {
  width: 92px;
  height: 92px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--huixin-bg-soft);
  border: 1px solid var(--huixin-border);
}
.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.article-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 1rem;
  align-items: center;
  padding: .8rem 0;
  border-bottom: 1px solid var(--huixin-border);
}
.article-row:last-child {
  border-bottom: 0;
}
.article-row-title {
  display: block;
  color: var(--huixin-text);
  font-weight: 700;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.article-row-title:hover {
  color: var(--huixin-primary);
}
.article-row-actions {
  display: flex;
  gap: .4rem;
}
@media (max-width: 575.98px) {
  .profile-header,
  .article-row {
    grid-template-columns: 1fr;
    display: grid;
  }
  .profile-header .btn {
    margin-left: 0 !important;
  }
  .article-row-actions {
    justify-content: flex-start;
  }
}
</style>
