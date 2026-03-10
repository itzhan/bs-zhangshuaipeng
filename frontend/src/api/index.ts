import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  res => {
    if (res.data.code === 200) return res.data.data
    return Promise.reject(new Error(res.data.message || '请求失败'))
  },
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default api

// ========== Auth ==========
export const authApi = {
  login: (data: any) => api.post('/auth/login', data),
  register: (data: any) => api.post('/auth/register', data),
  getInfo: () => api.get('/auth/info'),
}

// ========== Pet ==========
export const petApi = {
  list: (params?: any) => api.get('/pets', { params }),
  detail: (id: number) => api.get(`/pets/${id}`),
}

// ========== Adoption ==========
export const adoptionApi = {
  apply: (data: any) => api.post('/adoptions', data),
  myList: (params?: any) => api.get('/adoptions/my', { params }),
  cancel: (id: number) => api.put(`/adoptions/${id}/cancel`),
}

// ========== Rescue ==========
export const rescueApi = {
  list: (params?: any) => api.get('/rescues', { params }),
  create: (data: any) => api.post('/rescues', data),
}

// ========== Lost & Found ==========
export const lostFoundApi = {
  list: (params?: any) => api.get('/lost-found', { params }),
  detail: (id: number) => api.get(`/lost-found/${id}`),
  create: (data: any) => api.post('/lost-found', data),
}

// ========== Activity ==========
export const activityApi = {
  list: (params?: any) => api.get('/activities', { params }),
  detail: (id: number) => api.get(`/activities/${id}`),
  register: (id: number) => api.post(`/activities/${id}/register`),
  cancel: (id: number) => api.post(`/activities/${id}/cancel`),
  myList: (params?: any) => api.get('/activities/my', { params }),
}

// ========== Comment ==========
export const commentApi = {
  list: (params?: any) => api.get('/comments', { params }),
  create: (data: any) => api.post('/comments', data),
  delete: (id: number) => api.delete(`/comments/${id}`),
}

// ========== Favorite ==========
export const favoriteApi = {
  list: (params?: any) => api.get('/favorites', { params }),
  add: (petId: number) => api.post(`/favorites/${petId}`),
  remove: (petId: number) => api.delete(`/favorites/${petId}`),
  check: (petId: number) => api.get(`/favorites/check/${petId}`),
}

// ========== Notification ==========
export const notificationApi = {
  list: (params?: any) => api.get('/notifications', { params }),
  markRead: (id: number) => api.put(`/notifications/${id}/read`),
  markAllRead: () => api.put('/notifications/read-all'),
  unreadCount: () => api.get('/notifications/unread-count'),
}

// ========== Station ==========
export const stationApi = {
  list: (params?: any) => api.get('/stations', { params }),
  all: () => api.get('/stations/all'),
}

// ========== Dashboard ==========
export const dashboardApi = {
  overview: () => api.get('/dashboard/overview'),
  petSpecies: () => api.get('/dashboard/pet-species'),
  petStatus: () => api.get('/dashboard/pet-status'),
  adoptionTrend: () => api.get('/dashboard/adoption-trend'),
  stationCapacity: () => api.get('/dashboard/station-capacity'),
}

// ========== Dict ==========
export const dictApi = {
  byType: (type: string) => api.get(`/dict/${type}`),
}

// ========== Upload ==========
export const uploadFile = (file: File, dir = 'common') => {
  const fd = new FormData()
  fd.append('file', file)
  return api.post('/auth/upload', fd, { params: { dir }, headers: { 'Content-Type': 'multipart/form-data' } })
}
