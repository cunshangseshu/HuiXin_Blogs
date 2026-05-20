import request from './request'

export const searchApi = {
  search: (keyword, page = 1, size = 10) => request.get('/search', { params: { keyword, page, size } }),
  getHotKeywords: (limit = 10) => request.get('/search/hot-keywords', { params: { limit } })
}
