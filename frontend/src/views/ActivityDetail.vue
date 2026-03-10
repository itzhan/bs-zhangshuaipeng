<template>
  <div class="container detail-page">
    <n-spin :show="loading">
      <template v-if="act">
        <!-- 顶部大图 -->
        <div class="detail-hero">
          <img :src="actImage" class="main-img" />
          <div class="hero-overlay">
            <n-tag :type="statusType" size="large" round>{{ statusText }}</n-tag>
          </div>
        </div>

        <!-- 标题 + 报名信息 -->
        <div class="title-section">
          <h1>{{ act.title }}</h1>
          <div class="progress-wrap">
            <n-progress
              type="line"
              :percentage="Math.round((act.currentParticipants || 0) / (act.maxParticipants || 1) * 100)"
              :status="(act.currentParticipants || 0) >= (act.maxParticipants || 1) ? 'error' : 'success'"
              :show-indicator="false"
              style="flex:1"
            />
            <span class="progress-text">
              <UsersRound :size="14" style="vertical-align:-2px;margin-right:4px" />
              {{ act.currentParticipants || 0 }}/{{ act.maxParticipants }} 人
            </span>
          </div>
        </div>

        <!-- 信息卡片 -->
        <div class="info-grid">
          <div class="info-card">
            <MapPin :size="22" class="info-icon" />
            <div class="info-label">活动地点</div>
            <div class="info-value">{{ act.location }}</div>
          </div>
          <div class="info-card">
            <CalendarIcon :size="22" class="info-icon" />
            <div class="info-label">开始时间</div>
            <div class="info-value">{{ formatTime(act.startTime) }}</div>
          </div>
          <div class="info-card">
            <Clock :size="22" class="info-icon" />
            <div class="info-label">结束时间</div>
            <div class="info-value">{{ formatTime(act.endTime) }}</div>
          </div>
          <div class="info-card">
            <UsersRound :size="22" class="info-icon" />
            <div class="info-label">人数上限</div>
            <div class="info-value">{{ act.maxParticipants }} 人</div>
          </div>
        </div>

        <!-- 活动介绍 -->
        <n-card class="desc-card">
          <template #header>
            <FileText :size="18" style="vertical-align:-3px;margin-right:6px" /> 活动介绍
          </template>
          <p class="desc-text">{{ act.description || '暂无介绍' }}</p>
        </n-card>

        <!-- 操作按钮 -->
        <div class="action-bar">
          <n-button
            v-if="act.status <= 1 && (act.currentParticipants || 0) < (act.maxParticipants || 0)"
            type="primary" size="large" @click="handleRegister"
          >
            <CalendarHeart :size="18" style="margin-right:6px" /> 我要报名
          </n-button>
          <n-button v-else-if="act.status > 1" disabled size="large">活动已结束</n-button>
          <n-button v-else disabled size="large">名额已满</n-button>
          <n-button @click="$router.back()" size="large">
            <ArrowLeft :size="16" style="margin-right:4px" /> 返回列表
          </n-button>
        </div>
      </template>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { activityApi } from '@/api'
import { useUserStore } from '@/store/user'
import {
  MapPin, Calendar as CalendarIcon, Clock, UsersRound,
  FileText, CalendarHeart, ArrowLeft
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const msg = useMessage()
const userStore = useUserStore()
const loading = ref(true)
const act = ref<any>(null)

const actImages = [
  'https://images.unsplash.com/photo-1587559070757-f72a388edbba?w=800&h=400&fit=crop',
  'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=800&h=400&fit=crop',
  'https://images.unsplash.com/photo-1450778869180-41d0601e046e?w=800&h=400&fit=crop',
  'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=800&h=400&fit=crop',
  'https://images.unsplash.com/photo-1586671267731-da2cf3ceeb80?w=800&h=400&fit=crop',
  'https://images.unsplash.com/photo-1494947665470-20322015e3a8?w=800&h=400&fit=crop'
]

const actImage = computed(() => {
  if (!act.value) return ''
  if (act.value.coverImage && !act.value.coverImage.includes('/upload/')) return act.value.coverImage
  return actImages[(act.value.id || 0) % actImages.length]
})

const statusText = computed(() => ['未开始', '进行中', '已取消', '已结束'][act.value?.status] || '未知')
const statusType = computed(() => {
  const s = act.value?.status
  if (s === 1) return 'success'
  if (s === 0) return 'info'
  return 'default'
})

function formatTime(t: string) {
  if (!t) return '未知'
  return t.replace('T', ' ').substring(0, 16)
}

onMounted(async () => {
  const id = Number(route.params.id)
  try { act.value = await activityApi.detail(id) }
  catch { msg.error('加载失败') }
  loading.value = false
})

async function handleRegister() {
  if (!userStore.isLogin) { router.push('/login'); return }
  try {
    await activityApi.register(act.value.id)
    msg.success('报名成功！')
    // 刷新数据
    act.value = await activityApi.detail(act.value.id)
  } catch (e: any) { msg.error(e.message || '报名失败') }
}
</script>

<style scoped>
.detail-hero { position: relative; border-radius: 16px; overflow: hidden; margin-bottom: 24px; }
.main-img { width: 100%; height: 380px; object-fit: cover; display: block; }
.hero-overlay { position: absolute; top: 16px; left: 16px; }

.title-section { margin-bottom: 24px; }
.title-section h1 { font-size: 1.8rem; margin: 0 0 12px; }
.progress-wrap { display: flex; align-items: center; gap: 12px; }
.progress-text { white-space: nowrap; color: #666; font-size: 0.9rem; }

.info-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(170px, 1fr)); gap: 12px; margin-bottom: 24px; }
.info-card {
  background: #f8faf6; border: 1px solid #e8efe4; border-radius: 12px;
  padding: 16px; text-align: center; transition: all 0.2s;
}
.info-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(45,140,90,0.1); border-color: #2d8c5a; }
.info-icon { color: #2d8c5a; margin-bottom: 6px; }
.info-label { font-size: 0.8rem; color: #999; margin-bottom: 4px; }
.info-value { font-size: 0.95rem; font-weight: 600; color: #333; }

.desc-card { margin-bottom: 24px; border-radius: 12px; }
.desc-text { font-size: 1rem; line-height: 1.8; color: #444; }

.action-bar { display: flex; gap: 12px; margin-top: 8px; }
</style>
