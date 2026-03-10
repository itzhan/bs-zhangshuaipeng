<template>
  <div class="layout">
    <header class="nav-bar">
      <div class="container nav-inner">
        <router-link to="/" class="logo">
          <PawPrint :size="22" style="margin-right:6px;vertical-align:-3px" />
          流浪宠物救助
        </router-link>
        <nav class="nav-links">
          <router-link to="/" :class="{ active: route.path === '/' }">
            <Home :size="16" style="margin-right:4px;vertical-align:-2px" />首页
          </router-link>
          <router-link to="/pets" :class="{ active: route.path.startsWith('/pets') }">
            <Cat :size="16" style="margin-right:4px;vertical-align:-2px" />领养宠物
          </router-link>
          <router-link to="/rescue" :class="{ active: route.path === '/rescue' }">
            <Siren :size="16" style="margin-right:4px;vertical-align:-2px" />求助中心
          </router-link>
          <router-link to="/lost-found" :class="{ active: route.path === '/lost-found' }">
            <Search :size="16" style="margin-right:4px;vertical-align:-2px" />寻宠招领
          </router-link>
          <router-link to="/activities" :class="{ active: route.path === '/activities' }">
            <CalendarHeart :size="16" style="margin-right:4px;vertical-align:-2px" />爱心活动
          </router-link>
          <router-link to="/stations" :class="{ active: route.path === '/stations' }">
            <Hospital :size="16" style="margin-right:4px;vertical-align:-2px" />救助站
          </router-link>
        </nav>
        <div class="nav-right">
          <template v-if="userStore.isLogin">
            <router-link to="/user" class="user-btn">
              <UserRound :size="16" style="margin-right:4px;vertical-align:-2px" />
              {{ userStore.userInfo?.nickname || '个人中心' }}
            </router-link>
            <n-button text @click="handleLogout">
              <LogOut :size="16" style="margin-right:4px;vertical-align:-2px" />退出
            </n-button>
          </template>
          <template v-else>
            <router-link to="/login"><n-button type="primary" size="small">登录</n-button></router-link>
          </template>
        </div>
      </div>
    </header>
    <main class="main-content">
      <router-view />
    </main>
    <footer class="footer">
      <div class="container">
        <p><Heart :size="14" style="vertical-align:-2px;color:#e74c3c" /> © 2025 流浪宠物救助系统 — 给每个生命一个温暖的家</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { PawPrint, Home, Cat, Siren, Search, CalendarHeart, Hospital, UserRound, LogOut, Heart } from 'lucide-vue-next'

const route = useRoute()
const userStore = useUserStore()
const router = useRouter()

onMounted(() => { if (userStore.isLogin) userStore.fetchUserInfo() })

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.nav-bar {
  position: sticky; top: 0; z-index: 100;
  background: rgba(255,255,255,0.95); backdrop-filter: blur(10px);
  box-shadow: 0 1px 10px rgba(0,0,0,0.06);
}
.nav-inner { display: flex; align-items: center; height: 60px; gap: 32px; }
.logo { font-size: 1.2rem; font-weight: 700; color: #2d8c5a; white-space: nowrap; display: flex; align-items: center; }
.nav-links { display: flex; gap: 24px; flex: 1; }
.nav-links a { color: #555; font-size: 0.95rem; padding: 4px 0; border-bottom: 2px solid transparent; transition: all 0.2s; display: flex; align-items: center; }
.nav-links a:hover, .nav-links a.active { color: #2d8c5a; border-bottom-color: #2d8c5a; }
.nav-right { display: flex; align-items: center; gap: 12px; white-space: nowrap; }
.user-btn { color: #2d8c5a; font-weight: 500; display: flex; align-items: center; }
.main-content { min-height: calc(100vh - 140px); }
.footer { text-align: center; padding: 24px 20px; color: #999; font-size: 0.85rem; background: #f0f2ee; margin-top: 40px; }
</style>
