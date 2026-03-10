<template>
  <div class="container">
    <div class="page-header">
      <h2><CalendarHeart :size="22" style="vertical-align:-4px;margin-right:6px" /> 爱心活动</h2>
      <p>参与志愿活动，传递爱与温暖</p>
    </div>
    <n-spin :show="loading">
      <div class="card-grid">
        <n-card v-for="act in list" :key="act.id" hoverable class="act-card" @click="$router.push(`/activities/${act.id}`)">
          <template #cover>
            <div class="card-cover">
              <img :src="actImage(act)" />
              <n-tag :type="act.status===1?'success':act.status===0?'info':'default'" size="small" class="card-status-tag">
                {{ ['未开始','进行中','已取消','已结束'][act.status] }}
              </n-tag>
            </div>
          </template>
          <h3 class="card-title">{{ act.title }}</h3>
          <p class="card-meta"><MapPin :size="14" style="vertical-align:-2px;margin-right:2px" /> {{ act.location }}</p>
          <p class="card-meta"><CalendarDays :size="14" style="vertical-align:-2px;margin-right:2px" /> {{ formatTime(act.startTime) }}</p>
          <div class="card-footer">
            <span class="card-people"><UsersRound :size="14" style="vertical-align:-2px;margin-right:4px" /> {{ act.currentParticipants || 0 }}/{{ act.maxParticipants }}</span>
            <n-button type="primary" size="tiny" @click.stop="$router.push(`/activities/${act.id}`)">查看详情</n-button>
          </div>
        </n-card>
      </div>
      <n-empty v-if="!loading && !list.length" description="暂无活动" style="margin:40px 0" />
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { activityApi } from '@/api'
import { CalendarHeart, MapPin, Calendar as CalendarDays, Users as UsersRound } from 'lucide-vue-next'

const loading = ref(false)
const list = ref<any[]>([])

const actImages = [
  'https://images.unsplash.com/photo-1587559070757-f72a388edbba?w=400&h=250&fit=crop',
  'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400&h=250&fit=crop',
  'https://images.unsplash.com/photo-1450778869180-41d0601e046e?w=400&h=250&fit=crop',
  'https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=400&h=250&fit=crop',
  'https://images.unsplash.com/photo-1586671267731-da2cf3ceeb80?w=400&h=250&fit=crop',
  'https://images.unsplash.com/photo-1494947665470-20322015e3a8?w=400&h=250&fit=crop'
]

function actImage(act: any) {
  if (act.coverImage && !act.coverImage.includes('/upload/')) return act.coverImage
  return actImages[(act.id || 0) % actImages.length]
}

function formatTime(t: string) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

async function loadData() {
  loading.value = true
  try { const r: any = await activityApi.list({ page: 1, size: 20 }); list.value = r?.records || [] }
  catch {} finally { loading.value = false }
}
onMounted(() => loadData())
</script>

<style scoped>
.act-card { cursor: pointer; transition: all 0.2s; }
.act-card:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.1); }
.card-cover { position: relative; height: 180px; overflow: hidden; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; }
.card-status-tag { position: absolute; top: 8px; right: 8px; }
.card-title { font-size: 1.1rem; font-weight: 600; margin: 0 0 8px; }
.card-meta { color: #888; font-size: 0.85rem; margin-bottom: 4px; }
.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.card-people { color: #666; font-size: 0.85rem; }
</style>
