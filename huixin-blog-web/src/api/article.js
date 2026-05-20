import request from './request'

export const articleApi = {
  create: (data) => request.post('/article', data),
  update: (id, data) => request.put(`/article/${id}`, data),
  delete: (id) => request.delete(`/article/${id}`),
  getDetail: (id) => request.get(`/article/${id}`),
  list: (params) => request.get('/article/list', { params }),
  listByUser: (userId, page = 1, size = 10) => request.get(`/article/user/${userId}`, { params: { page, size } })
}

export const categoryApi = {
  list: () => request.get('/category/list')
}

export const tagApi = {
  list: () => request.get('/tag/list')
}
