import request from './request'

export const statsApi = {
  recordView: (articleId) => request.post(`/stats/article/${articleId}/view`),
  toggleLike: (articleId) => request.post(`/stats/article/${articleId}/like`),
  hasLiked: (articleId) => request.get(`/stats/article/${articleId}/liked`),
  getStats: (articleId) => request.get(`/stats/article/${articleId}`),
  getHot: (limit = 10) => request.get('/stats/hot', { params: { limit } })
}
