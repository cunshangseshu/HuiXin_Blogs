<template>
  <div class="row justify-content-center">
    <div class="col-xl-9 col-lg-10">
      <div class="card editor-shell p-3 p-lg-4">
        <div class="d-flex align-items-start justify-content-between gap-3 flex-wrap mb-3">
          <div>
            <h1 class="editor-title">{{ isEdit ? '编辑文章' : '写文章' }}</h1>
            <p class="text-muted small mb-0">标题、分类和正文为必填项</p>
          </div>
          <div class="btn-group btn-group-sm" role="group" aria-label="编辑模式">
            <button type="button" class="btn btn-outline-secondary" :class="{ active: tab === 'write' }" @click="tab = 'write'">编辑</button>
            <button type="button" class="btn btn-outline-secondary" :class="{ active: tab === 'preview' }" @click="tab = 'preview'">预览</button>
          </div>
        </div>

        <form @submit.prevent="handleSubmit">
          <div class="mb-3">
            <input v-model.trim="form.title" class="form-control form-control-lg title-input" placeholder="文章标题" required maxlength="200">
            <div class="form-hint">{{ form.title.length }}/200</div>
          </div>

          <div class="row g-3 mb-3">
            <div class="col-md-4">
              <label class="form-label">分类</label>
              <select v-model="form.categoryId" class="form-select" required>
                <option value="">选择分类</option>
                <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.categoryName }}</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">原创声明</label>
              <select v-model.number="form.isOriginal" class="form-select">
                <option :value="1">原创</option>
                <option :value="0">转载</option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">封面图片 URL</label>
              <input v-model.trim="form.coverImageUrl" class="form-control" placeholder="https:// 或 data:image/">
            </div>
          </div>

          <div v-if="form.coverImageUrl" class="cover-preview mb-3">
            <img :src="form.coverImageUrl" alt="封面预览">
          </div>

          <div class="mb-3">
            <label class="form-label">标签</label>
            <div class="tag-picker">
              <span v-for="tag in selectedTags" :key="tag.id" class="tag-badge" :style="{ backgroundColor: tag.tagColor || '#6B7280' }">
                {{ tag.tagName }}
                <button type="button" class="tag-remove" @click="removeTag(tag.id)" aria-label="移除标签">×</button>
              </span>
              <div class="dropdown" v-if="availableTags.length">
                <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">添加标签</button>
                <ul class="dropdown-menu tag-menu">
                  <li v-for="tag in availableTags" :key="tag.id">
                    <a class="dropdown-item" href="#" @click.prevent="addTag(tag)">{{ tag.tagName }}</a>
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">摘要</label>
            <textarea v-model.trim="form.summary" class="form-control" rows="2" placeholder="留空时根据正文自动生成" maxlength="500"></textarea>
            <div class="form-hint">{{ form.summary.length }}/500</div>
          </div>

          <div class="mb-3">
            <div class="d-flex justify-content-between align-items-center mb-1">
              <label class="form-label mb-0">正文</label>
              <small class="text-muted">{{ form.content.length }} 字</small>
            </div>
            <textarea
              v-show="tab === 'write'"
              v-model="form.content"
              class="form-control font-monospace editor-textarea"
              rows="18"
              placeholder="# 标题&#10;&#10;在这里开始写作..."
              required
            ></textarea>
            <div v-show="tab === 'preview'" class="preview-pane article-body" v-html="previewHtml"></div>
          </div>

          <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
            <router-link to="/user/center" class="btn btn-outline-secondary">取消</router-link>
            <button type="submit" class="btn btn-primary" :disabled="submitting || !canSubmit">
              {{ submitting ? '保存中...' : (isEdit ? '更新文章' : '发布文章') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi, categoryApi, tagApi } from '@/api/article'
import { useUserStore } from '@/store/user'
import { clampText } from '@/utils/format'
import { markdownToHtml } from '@/utils/markdown'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const isEdit = computed(() => !!route.params.id)
const tab = ref('write')
const form = reactive({
  title: '',
  categoryId: '',
  content: '',
  summary: '',
  coverImageUrl: '',
  isOriginal: 1
})
const categories = ref([])
const allTags = ref([])
const selectedTags = ref([])
const submitting = ref(false)
const availableTags = computed(() => allTags.value.filter(tag => !selectedTags.value.find(selected => selected.id === tag.id)))
const previewHtml = computed(() => markdownToHtml(form.content))
const canSubmit = computed(() => form.title && form.categoryId && form.content.trim())

function stripMarkdown(value) {
  return String(value || '')
    .replace(/```[\s\S]*?```/g, ' ')
    .replace(/!\[[^\]]*]\([^)]*\)/g, ' ')
    .replace(/\[[^\]]+]\(([^)]*)\)/g, ' ')
    .replace(/[#>*_`-]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

function addTag(tag) {
  selectedTags.value.push(tag)
}

function removeTag(id) {
  selectedTags.value = selectedTags.value.filter(tag => tag.id !== id)
}

async function handleSubmit() {
  if (!canSubmit.value || submitting.value) return
  submitting.value = true
  const data = {
    title: form.title,
    categoryId: Number(form.categoryId),
    content: form.content,
    contentHtml: previewHtml.value,
    summary: form.summary || clampText(stripMarkdown(form.content), 180),
    coverImageUrl: form.coverImageUrl,
    tagIds: selectedTags.value.map(tag => tag.id),
    isOriginal: form.isOriginal
  }
  try {
    if (isEdit.value) {
      await articleApi.update(Number(route.params.id), data)
      router.push(`/article/${route.params.id}`)
    } else {
      const res = await articleApi.create(data)
      const articleId = res.data?.articleId
      router.push(articleId ? `/article/${articleId}` : '/user/center')
    }
  } catch (e) {
    alert(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  if (!store.user && localStorage.getItem('accessToken')) await store.fetchUserInfo()
  if (!store.isBlogger) {
    alert('只有博主可以发布文章')
    router.push('/user/center?tab=apply')
    return
  }

  try {
    const res = await categoryApi.list()
    if (res.code === 200) categories.value = res.data || []
  } catch (e) {}

  try {
    const res = await tagApi.list()
    if (res.code === 200) allTags.value = res.data || []
  } catch (e) {}

  if (isEdit.value) {
    try {
      const res = await articleApi.getDetail(Number(route.params.id))
      if (res.code === 200) {
        const article = res.data
        if (article.authorId !== store.user?.id) {
          alert('无权编辑此文章')
          router.push('/')
          return
        }
        Object.assign(form, {
          title: article.title || '',
          categoryId: article.categoryId || '',
          content: article.content || '',
          summary: article.summary || '',
          coverImageUrl: article.coverImageUrl || '',
          isOriginal: article.isOriginal ?? 1
        })
        selectedTags.value = article.tags || []
      }
    } catch (e) {
      alert('文章加载失败')
      router.push('/user/center')
    }
  }
})
</script>

<style scoped>
.editor-shell {
  border-top: 3px solid var(--huixin-primary);
}
.editor-title {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 800;
}
.title-input {
  font-weight: 700;
}
.form-hint {
  margin-top: .25rem;
  color: var(--huixin-text-light);
  font-size: .8rem;
  text-align: right;
}
.cover-preview {
  width: 100%;
  max-height: 260px;
  overflow: hidden;
  border-radius: 8px;
  background: var(--huixin-bg-soft);
}
.cover-preview img {
  width: 100%;
  height: 100%;
  max-height: 260px;
  object-fit: cover;
}
.tag-picker {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: .45rem;
}
.tag-badge {
  margin: 0;
}
.tag-remove {
  margin-left: .25rem;
  border: 0;
  background: transparent;
  color: #fff;
  font-size: .95rem;
  line-height: 1;
}
.tag-menu {
  max-height: 240px;
  overflow-y: auto;
}
.editor-textarea,
.preview-pane {
  min-height: 440px;
}
.preview-pane {
  padding: 1rem;
  border: 1px solid var(--huixin-border);
  border-radius: 8px;
  background: #fff;
  overflow-y: auto;
}
.article-body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}
.article-body :deep(pre) {
  padding: 1rem;
  border-radius: 8px;
  background: #f3f6f4;
  overflow-x: auto;
}
.article-body :deep(blockquote) {
  padding-left: 1rem;
  border-left: 4px solid var(--huixin-primary);
  color: var(--huixin-text-light);
}
</style>
