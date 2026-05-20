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
            <textarea v-model="form.content" class="form-control" rows="15" placeholder="文章正文（支持Markdown）" required></textarea>
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

const route = useRoute(); const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const form = reactive({ title: '', categoryId: '', content: '', summary: '', coverImageUrl: '' })
const categories = ref([]); const allTags = ref([]); const selectedTags = ref([])
const submitting = ref(false)
const availableTags = computed(() => allTags.value.filter(t => !selectedTags.value.find(st => st.id === t.id)))

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
        Object.assign(form, { title: a.title, categoryId: a.categoryId, content: a.content, summary: a.summary || '', coverImageUrl: a.coverImageUrl || '' })
        if (a.tags) selectedTags.value = a.tags
      }
    } catch (e) {}
  }
})
</script>
