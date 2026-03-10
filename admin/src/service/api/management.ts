import { request } from '../request';

// ==================== 宠物管理 ====================
export function fetchPetList(params: any) {
  return request<any>({ url: '/pets', params });
}
export function fetchPetDetail(id: number) {
  return request<any>({ url: `/pets/${id}` });
}
export function createPet(data: any) {
  return request<any>({ url: '/pets', method: 'post', data });
}
export function updatePet(id: number, data: any) {
  return request<any>({ url: `/pets/${id}`, method: 'put', data });
}
export function deletePet(id: number) {
  return request<any>({ url: `/pets/${id}`, method: 'delete' });
}
export function updatePetStatus(id: number, status: number) {
  return request<any>({ url: `/pets/${id}/status`, method: 'put', data: { status } });
}

// ==================== 用户管理 ====================
export function fetchUserList(params: any) {
  return request<any>({ url: '/users', params });
}
export function updateUser(id: number, data: any) {
  return request<any>({ url: `/users/${id}`, method: 'put', data });
}
export function updateUserStatus(id: number, status: number) {
  return request<any>({ url: `/users/${id}/status`, method: 'put', data: { status } });
}
export function deleteUser(id: number) {
  return request<any>({ url: `/users/${id}`, method: 'delete' });
}

// ==================== 领养管理 ====================
export function fetchAdoptionList(params: any) {
  return request<any>({ url: '/adoptions', params });
}
export function reviewAdoption(id: number, data: any) {
  return request<any>({ url: `/adoptions/${id}/review`, method: 'put', data });
}
export function deleteAdoption(id: number) {
  return request<any>({ url: `/adoptions/${id}`, method: 'delete' });
}

// ==================== 救助记录 ====================
export function fetchRescueList(params: any) {
  return request<any>({ url: '/rescues', params });
}
export function createRescue(data: any) {
  return request<any>({ url: '/rescues', method: 'post', data });
}
export function updateRescue(id: number, data: any) {
  return request<any>({ url: `/rescues/${id}`, method: 'put', data });
}
export function updateRescueStatus(id: number, status: number) {
  return request<any>({ url: `/rescues/${id}/status`, method: 'put', data: { status } });
}
export function deleteRescue(id: number) {
  return request<any>({ url: `/rescues/${id}`, method: 'delete' });
}

// ==================== 救助站 ====================
export function fetchStationList(params: any) {
  return request<any>({ url: '/stations', params });
}
export function fetchAllStations() {
  return request<any>({ url: '/stations/all' });
}
export function createStation(data: any) {
  return request<any>({ url: '/stations', method: 'post', data });
}
export function updateStation(id: number, data: any) {
  return request<any>({ url: `/stations/${id}`, method: 'put', data });
}
export function deleteStation(id: number) {
  return request<any>({ url: `/stations/${id}`, method: 'delete' });
}

// ==================== 物资管理 ====================
export function fetchSupplyList(params: any) {
  return request<any>({ url: '/supplies', params });
}
export function createSupply(data: any) {
  return request<any>({ url: '/supplies', method: 'post', data });
}
export function updateSupply(id: number, data: any) {
  return request<any>({ url: `/supplies/${id}`, method: 'put', data });
}
export function deleteSupply(id: number) {
  return request<any>({ url: `/supplies/${id}`, method: 'delete' });
}
export function fetchLowStockSupplies() {
  return request<any>({ url: '/supplies/low-stock' });
}

// ==================== 活动管理 ====================
export function fetchActivityList(params: any) {
  return request<any>({ url: '/activities', params });
}
export function createActivity(data: any) {
  return request<any>({ url: '/activities', method: 'post', data });
}
export function updateActivity(id: number, data: any) {
  return request<any>({ url: `/activities/${id}`, method: 'put', data });
}
export function deleteActivity(id: number) {
  return request<any>({ url: `/activities/${id}`, method: 'delete' });
}
export function fetchActivityRegistrations(id: number, params: any) {
  return request<any>({ url: `/activities/${id}/registrations`, params });
}

// ==================== 失宠招领 ====================
export function fetchLostFoundList(params: any) {
  return request<any>({ url: '/lost-found', params });
}
export function deleteLostFound(id: number) {
  return request<any>({ url: `/lost-found/${id}`, method: 'delete' });
}

// ==================== 志愿排班 ====================
export function fetchVolunteerList(params: any) {
  return request<any>({ url: '/volunteers', params });
}
export function createVolunteer(data: any) {
  return request<any>({ url: '/volunteers', method: 'post', data });
}
export function updateVolunteer(id: number, data: any) {
  return request<any>({ url: `/volunteers/${id}`, method: 'put', data });
}
export function deleteVolunteer(id: number) {
  return request<any>({ url: `/volunteers/${id}`, method: 'delete' });
}

// ==================== 数据字典 ====================
export function fetchDictList(params: any) {
  return request<any>({ url: '/dict', params });
}
export function fetchDictByType(type: string) {
  return request<any>({ url: `/dict/${type}` });
}
export function createDict(data: any) {
  return request<any>({ url: '/dict', method: 'post', data });
}
export function updateDict(id: number, data: any) {
  return request<any>({ url: `/dict/${id}`, method: 'put', data });
}
export function deleteDict(id: number) {
  return request<any>({ url: `/dict/${id}`, method: 'delete' });
}

// ==================== 数据看板 ====================
export function fetchDashboardOverview() {
  return request<any>({ url: '/dashboard/overview' });
}
export function fetchPetSpeciesStats() {
  return request<any>({ url: '/dashboard/pet-species' });
}
export function fetchPetStatusStats() {
  return request<any>({ url: '/dashboard/pet-status' });
}
export function fetchAdoptionTrend() {
  return request<any>({ url: '/dashboard/adoption-trend' });
}
export function fetchStationCapacity() {
  return request<any>({ url: '/dashboard/station-capacity' });
}

// ==================== 评论管理 ====================
export function fetchCommentList(params: any) {
  return request<any>({ url: '/comments', params });
}
export function deleteComment(id: number) {
  return request<any>({ url: `/comments/${id}/admin`, method: 'delete' });
}

// ==================== 通知 ====================
export function fetchNotificationList(params: any) {
  return request<any>({ url: '/notifications', params });
}

// ==================== 文件上传 ====================
export function uploadFile(file: File, dir = 'common') {
  const formData = new FormData();
  formData.append('file', file);
  return request<string>({
    url: '/auth/upload',
    method: 'post',
    data: formData,
    params: { dir },
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}
