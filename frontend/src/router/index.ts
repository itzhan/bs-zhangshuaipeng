import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
      { path: 'pets', name: 'PetList', component: () => import('@/views/PetList.vue'), meta: { title: '宠物列表' } },
      { path: 'pets/:id', name: 'PetDetail', component: () => import('@/views/PetDetail.vue'), meta: { title: '宠物详情' } },
      { path: 'rescue', name: 'Rescue', component: () => import('@/views/Rescue.vue'), meta: { title: '求助中心' } },
      { path: 'lost-found', name: 'LostFound', component: () => import('@/views/LostFound.vue'), meta: { title: '寻宠招领' } },
      { path: 'lost-found/:id', name: 'LostFoundDetail', component: () => import('@/views/LostFoundDetail.vue'), meta: { title: '寻宠详情' } },
      { path: 'activities', name: 'Activities', component: () => import('@/views/Activities.vue'), meta: { title: '爱心活动' } },
      { path: 'activities/:id', name: 'ActivityDetail', component: () => import('@/views/ActivityDetail.vue'), meta: { title: '活动详情' } },
      { path: 'stations', name: 'Stations', component: () => import('@/views/Stations.vue'), meta: { title: '救助站' } },
      { path: 'user', name: 'UserCenter', component: () => import('@/views/UserCenter.vue'), meta: { title: '个人中心', requiresAuth: true } },
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { title: '注册' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || ''} - 流浪宠物救助系统`
  if (to.meta.requiresAuth && !localStorage.getItem('token')) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
