import request from './request'

export const userApi = {
  getInfo: () => request.get('/user/info'),
  getPublicInfo: (id) => request.get(`/user/${id}`),
  updateInfo: (data) => request.put('/user/info', data),
  changePassword: (data) => request.put('/user/password', data),
  updateAvatar: (avatarUrl) => request.post('/user/avatar', null, { params: { avatarUrl } }),
  applyBlogger: (data) => request.post('/user/blogger/apply', typeof data === 'string' ? { applyReason: data } : data)
}
