<template>
  <div class="row justify-content-center">
    <div class="col-lg-8">
      <div class="card p-4">
        <h4 class="mb-3">{{ isEdit ? '编辑文章' : '写文章' }}</h4>
        <form @submit.prevent="handleSubmit">
          <div class="mb-3">
            <input v-model="form.title" class="form-control form-control-lg" placeholder="文章标题" required maxlength="200">
          </div>
          <div class="row mb-3">
            <div class="col-md-4">
              <select v-model="form.categoryId" class="form-select" required>
                <option value="">选择分类</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.categoryName }}</option>
              </select>
            </div>
            <div class="col-md-8">
              <div class="d-flex flex-wrap align-items-center">
                <span v-for="t in selectedTags" :key="t.id" class="tag-badge me-1 mb-1" :style="{backgroundColor:t.tagColor}">
                  {{ t.tagName }} <button type="button" class="btn-close btn-close-white ms-1" style="font-size:0.5rem" @click="removeTag(t.id)"></button>
                </span>
                <div class="dropdown" v-if="availableTags.length">
                  <button class="btn btn-sm btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown">+ 添加标签</button>
                  <ul class="dropdown-menu" style="max-height:200px;overflow-y:auto">
                    <li v-for="t in availableTags" :key="t.id"><a class="dropdown-item" href="#" @click.prevent="addTag(t)">{{ t.tagName }}</a></li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
          <div class="mb-3">
            <div class="d-flex justify-content-between align-items-center mb-1">
              <label class="form-label mb-0">文章正文（Markdown）</label>
              <div class="btn-group btn-group-sm" role="group">
                <button type="button" class="btn btn-outline-secondary" :class="{ active: tab==='write' }" @click="tab='write'">&#x270F; 编辑</button>
                <button type="button" class="btn btn-outline-secondary" :class="{ active: tab==='preview' }" @click="tab='preview'">&#x1F441; 预览</button>
              </div>
            </div>
            <textarea v-show="tab==='write'" v-model="form.content" class="form-control font-monospace" rows="16" placeholder="支持 Markdown 语法：&#10;# 一级标题&#10;## 二级标题&#10;**加粗** *斜体* &#10;- 列表项&#10;[链接](url)&#10;![图片](url)&#10;> 引用" required></textarea>
            <div v-show="tab==='preview'" class="border rounded p-3 bg-white" style="min-height:300px; max-height:500px; overflow-y:auto;" v-html="previewHtml"></div>
          </div>
          <div class="mb-3">
            <input v-model="form.coverImageUrl" class="form-control" placeholder="封面图片URL（可选）">
          </div>
          <div class="mb-3">
            <input v-model="form.summary" class="form-control" placeholder="文章摘要（可选，留空自动截取）" maxlength="500">
          </div>
          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? '保存中...' : (isEdit ? '更新' : '发布') }}
            </button>
            <router-link to="/user/center" class="btn btn-outline-secondary">取消</router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi, categoryApi, tagApi } from '@/api/article'
import { useUserStore } from '@/store/user'

const route = useRoute(); const router = useRouter()
const store = useUserStore()
const isEdit = computed(() => !!route.params.id)
const tab = ref('write')
const form = reactive({ title: '', categoryId: '', content: '', summary: '', coverImageUrl: '' })
const categories = ref([]); const allTags = ref([]); const selectedTags = ref([])
const submitting = ref(false)
const availableTags = computed(() => allTags.value.filter(t => !selectedTags.value.find(st => st.id === t.id)))

// 简单的 Markdown → HTML 转换（预览用）
const previewHtml = computed(() => {
  let html = form.content || ''
  html = html.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  html = html.replace(/^### (.+)$/gm, '<h6>$1</h6>')
  html = html.replace(/^## (.+)$/gm, '<h5>$1</h5>')
  html = html.replace(/^# (.+)$/gm, '<h4>$1</h4>')
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  html = html.replace(/\*(.+?)\*/g, '<em>$1</em>')
  html = html.replace(/`(.+?)`/g, '<code style="background:#f0ede8;padding:1px 4px;border-radius:3px;">$1</code>')
  html = html.replace(/!\[(.*?)\]\((.+?)\)/g, '<img src="$2" alt="$1" style="max-width:100%;border-radius:6px;">')
  html = html.replace(/\[(.+?)\]\((.+?)\)/g, '<a href="$2" target="_blank">$1</a>')
  html = html.replace(/^> (.+)$/gm, '<blockquote style="border-left:3px solid var(--huixin-primary);padding-left:.8rem;color:var(--huixin-text-light);">$1</blockquote>')
  html = html.replace(/^- (.+)$/gm, '<li>$1</li>')
  html = html.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>')
  html = html.replace(/\n\n/g, '</p><p>')
  html = '<p>' + html + '</p>'
  return html
})

function addTag(t) { selectedTags.value.push(t) }
function removeTag(id) { selectedTags.value = selectedTags.value.filter(t => t.id !== id) }

async function handleSubmit() {
  submitting.value = true
  const data = { ...form, tagIds: selectedTags.value.map(t => t.id), isOriginal: 1 }
  try {
    if (isEdit.value) {
      await articleApi.update(Number(route.params.id), data)
    } else {
      await articleApi.create(data)
    }
    router.push('/user/center')
  } catch (e) { alert(e.message || '操作失败') }
  submitting.value = false
}

onMounted(async () => {
  try { const r = await categoryApi.list(); if (r.code === 200) categories.value = r.data || [] } catch (e) {}
  try { const r = await tagApi.list(); if (r.code === 200) allTags.value = r.data || [] } catch (e) {}
  if (isEdit.value) {
    try {
      const r = await articleApi.getDetail(Number(route.params.id))
      if (r.code === 200) {
        const a = r.data
        // 前端所有权检查：非作者无权编辑
        if (a.authorId !== store.user?.id) {
          alert('无权编辑此文章')
          router.push('/')
          return
        }
        Object.assign(form, { title: a.title, categoryId: a.categoryId, content: a.content, summary: a.summary || '', coverImageUrl: a.coverImageUrl || '' })
        if (a.tags) selectedTags.value = a.tags
      }
    } catch (e) {}
  }
})
</script>
