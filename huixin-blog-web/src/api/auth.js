import request from './request'

export const authApi = {
  register: (data) => request.post('/auth/register', data),
  login: (data) => request.post('/auth/login', data),
  refreshToken: (refreshToken) => request.post('/auth/refresh', null, { headers: { 'X-Refresh-Token': refreshToken } }),
  // 登出由 Gateway 鉴权后注入 X-User-Id，前端不再手动传递
  logout: () => request.post('/auth/logout')
}
