<template>
  <div class="container detail-page">
    <n-spin :show="loading">
      <template v-if="item">
        <!-- 顶部图片 -->
        <div class="detail-hero" v-if="itemPhoto">
          <img :src="itemPhoto" class="main-img" />
          <div class="hero-overlay">
            <n-tag :type="item.type === 0 ? 'warning' : 'info'" size="large" round>{{ item.type === 0 ? '寻宠启事' : '招领启事' }}</n-tag>
          </div>
        </div>
        <div v-else class="no-photo-banner">
          <ImageOff :size="48" style="color:#ccc" />
          <p>暂无照片</p>
        </div>

        <!-- 标题和状态 -->
        <div class="title-row">
          <h1>{{ item.petName || (item.type === 0 ? '寻找宠物' : '招领宠物') }}</h1>
          <n-tag :type="item.status === 1 ? 'success' : 'warning'" size="small">{{ item.status === 1 ? '已找到' : '寻找中' }}</n-tag>
        </div>

        <!-- 信息卡片 -->
        <div class="info-grid">
          <div class="info-card">
            <Cat :size="20" class="info-icon" />
            <div class="info-label">种类</div>
            <div class="info-value">{{ item.species || '未知' }}</div>
          </div>
          <div class="info-card">
            <Palette :size="20" class="info-icon" />
            <div class="info-label">颜色</div>
            <div class="info-value">{{ item.color || '未知' }}</div>
          </div>
          <div class="info-card">
            <MapPin :size="20" class="info-icon" />
            <div class="info-label">{{ item.type === 0 ? '走失地点' : '发现地点' }}</div>
            <div class="info-value">{{ item.location || '未知' }}</div>
          </div>
          <div class="info-card">
            <CalendarIcon :size="20" class="info-icon" />
            <div class="info-label">{{ item.type === 0 ? '走失日期' : '发现日期' }}</div>
            <div class="info-value">{{ item.lostDate || item.foundDate || '未知' }}</div>
          </div>
        </div>

        <!-- 详细描述 -->
        <n-card class="desc-card">
          <template #header>
            <FileText :size="18" style="vertical-align:-3px;margin-right:6px" /> 详细描述
          </template>
          <p class="desc-text">{{ item.description || '暂无描述' }}</p>
        </n-card>

        <!-- 联系方式 -->
        <n-card class="contact-card">
          <template #header>
            <Phone :size="18" style="vertical-align:-3px;margin-right:6px" /> 联系方式
          </template>
          <div class="contact-row">
            <div class="contact-item">
              <UserRound :size="16" style="margin-right:6px;vertical-align:-2px;color:#2d8c5a" />
              <span>发布人 ID：{{ item.userId }}</span>
            </div>
            <div class="contact-item" v-if="item.contactPhone">
              <Phone :size="16" style="margin-right:6px;vertical-align:-2px;color:#2d8c5a" />
              <span>联系电话：<a :href="'tel:' + item.contactPhone" class="phone-link">{{ item.contactPhone }}</a></span>
            </div>
          </div>
          <n-button v-if="item.contactPhone" type="primary" style="margin-top:16px" @click="copyPhone">
            <Phone :size="16" style="margin-right:6px" /> 复制电话号码
          </n-button>
        </n-card>

        <div style="margin-top:20px">
          <n-button @click="$router.back()">
            <ArrowLeft :size="16" style="margin-right:4px" /> 返回列表
          </n-button>
        </div>
      </template>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { lostFoundApi } from '@/api'
import {
  Cat, Palette, MapPin, Calendar as CalendarIcon, FileText,
  Phone, UserRound, ArrowLeft, ImageOff
} from 'lucide-vue-next'
import { getLostFoundImage } from '@/utils/lostFoundImage'

const route = useRoute()
const msg = useMessage()
const loading = ref(true)
const item = ref<any>(null)

const itemPhoto = computed(() => {
  if (!item.value) return ''
  return getLostFoundImage(item.value)
})

onMounted(async () => {
  const id = Number(route.params.id)
  try {
    item.value = await lostFoundApi.detail(id)
  } catch { msg.error('加载失败') }
  loading.value = false
})

function copyPhone() {
  if (item.value?.contactPhone) {
    navigator.clipboard.writeText(item.value.contactPhone)
    msg.success('电话号码已复制')
  }
}
</script>

<style scoped>
.detail-hero { position: relative; border-radius: 16px; overflow: hidden; margin-bottom: 24px; }
.main-img { width: 100%; height: 360px; object-fit: cover; display: block; }
.hero-overlay {
  position: absolute; top: 16px; left: 16px;
}
.no-photo-banner {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  height: 200px; background: #f5f7f3; border-radius: 16px; margin-bottom: 24px; color: #ccc;
}
.title-row { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.title-row h1 { font-size: 1.8rem; margin: 0; }

.info-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px; margin-bottom: 24px;
}
.info-card {
  background: #f8faf6; border: 1px solid #e8efe4; border-radius: 12px;
  padding: 16px; text-align: center; transition: all 0.2s;
}
.info-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(45,140,90,0.1); border-color: #2d8c5a; }
.info-icon { color: #2d8c5a; margin-bottom: 6px; }
.info-label { font-size: 0.8rem; color: #999; margin-bottom: 4px; }
.info-value { font-size: 1rem; font-weight: 600; color: #333; }

.desc-card, .contact-card { margin-bottom: 20px; border-radius: 12px; }
.desc-text { font-size: 1rem; line-height: 1.8; color: #444; }
.contact-row { display: flex; flex-direction: column; gap: 12px; }
.contact-item { font-size: 0.95rem; color: #555; }
.phone-link { color: #2d8c5a; font-weight: 600; text-decoration: none; }
.phone-link:hover { text-decoration: underline; }
</style>
