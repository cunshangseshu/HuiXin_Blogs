import request from './request'

export const authApi = {
  register: (data) => request.post('/auth/register', data),
  login: (data) => request.post('/auth/login', data),
  refreshToken: (refreshToken) => request.post('/auth/refresh', null, { headers: { 'X-Refresh-Token': refreshToken } }),
  logout: (userId) => request.post('/auth/logout', null, { headers: { 'X-User-Id': userId } })
}
