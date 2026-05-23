import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoryApi } from '@/api/article'

export const useCategoryStore = defineStore('category', () => {
  const categories = ref([])
  const isLoaded = ref(false)

  async function fetchCategories() {
    if (isLoaded.value) return categories.value
    try {
      const res = await categoryApi.list()
      if (res.code === 200) {
        categories.value = res.data || []
        isLoaded.value = true
      }
    } catch (e) {
      console.error('Failed to load categories', e)
    }
    return categories.value
  }

  return { categories, isLoaded, fetchCategories }
})
