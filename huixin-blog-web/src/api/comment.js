import request from './request'

export const commentApi = {
  create: (data) => request.post('/comment', data),
  delete: (id) => request.delete(`/comment/${id}`),
  getByArticle: (articleId) => request.get(`/comment/article/${articleId}`)
}
