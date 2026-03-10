<template>
  <div class="container">
    <div class="page-header"><h2><Hospital :size="22" style="vertical-align:-4px;margin-right:6px" /> 救助站</h2><p>查看各地流浪动物救助站信息</p></div>
    <n-spin :show="loading">
      <div class="card-grid">
        <n-card v-for="s in list" :key="s.id" :title="s.name" hoverable>
          <p><MapPin :size="14" style="vertical-align:-2px;margin-right:2px" /> {{ s.address }}</p>
          <p><UserRound :size="14" style="vertical-align:-2px;margin-right:2px" /> 负责人：{{ s.manager }}</p>
          <p><Phone :size="14" style="vertical-align:-2px;margin-right:2px" /> {{ s.contactPhone }}</p>
          <p><Building :size="14" style="vertical-align:-2px;margin-right:2px" /> 容量：{{ s.currentCount }}/{{ s.capacity }}</p>
          <n-progress :percentage="s.capacity > 0 ? Math.round(s.currentCount * 100 / s.capacity) : 0"
            :status="s.currentCount / s.capacity > 0.8 ? 'error' : 'success'" style="margin-top:8px" />
          <p v-if="s.description" style="margin-top:8px;color:#888">{{ s.description }}</p>
        </n-card>
      </div>
      <n-empty v-if="!loading && !list.length" description="暂无救助站信息" style="margin:40px 0" />
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { stationApi } from '@/api'
import { Hospital, MapPin, UserRound, Phone, Building } from 'lucide-vue-next'

const loading = ref(false); const list = ref<any[]>([])
async function load() { loading.value = true; try { const r: any = await stationApi.list({ page: 1, size: 50 }); list.value = r?.records || [] } catch {} finally { loading.value = false } }
onMounted(() => load())
</script>
