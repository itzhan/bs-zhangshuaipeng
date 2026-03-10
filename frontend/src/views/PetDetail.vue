<template>
  <div class="container detail-page">
    <n-spin :show="loading">
      <template v-if="pet">
        <!-- 顶部大图 + 名称 -->
        <div class="detail-hero">
          <img :src="petImage" class="main-img" />
          <div class="hero-overlay">
            <h1>{{ pet.name }}</h1>
            <div class="hero-tags">
              <n-tag type="success" round>{{ pet.species }}</n-tag>
              <n-tag v-if="pet.breed" round>{{ pet.breed }}</n-tag>
              <n-tag v-if="pet.status === 2" type="warning" round>待领养</n-tag>
              <n-tag v-else-if="pet.status === 3" type="success" round>已领养</n-tag>
              <n-tag v-else-if="pet.status === 1" type="info" round>救助中</n-tag>
              <n-tag v-else round>待救助</n-tag>
            </div>
          </div>
        </div>

        <!-- 属性卡片网格 -->
        <div class="attr-grid">
          <div class="attr-card" v-for="attr in attributes" :key="attr.label">
            <component :is="attr.icon" :size="22" class="attr-icon" />
            <div class="attr-label">{{ attr.label }}</div>
            <div class="attr-value">{{ attr.value }}</div>
          </div>
        </div>

        <!-- 宠物介绍 -->
        <n-card class="intro-card">
          <template #header>
            <FileText :size="18" style="vertical-align:-3px;margin-right:6px" />
            宠物介绍
          </template>
          <p class="desc-text">{{ pet.description || '暂无详细介绍' }}</p>
          <div class="location-row" v-if="pet.location">
            <MapPin :size="16" style="vertical-align:-3px;margin-right:4px;color:#2d8c5a" />
            <span>发现地点：{{ pet.location }}</span>
          </div>
          <div class="location-row" v-if="pet.rescueDate">
            <Calendar :size="16" style="vertical-align:-3px;margin-right:4px;color:#2d8c5a" />
            <span>救助日期：{{ pet.rescueDate }}</span>
          </div>
        </n-card>

        <!-- 操作按钮 -->
        <div class="action-bar" v-if="pet.status === 2">
          <n-button type="primary" size="large" @click="showAdopt = true">
            <HeartHandshake :size="18" style="margin-right:6px" /> 我要领养
          </n-button>
          <n-button :type="isFav ? 'error' : 'default'" size="large" @click="toggleFav">
            <component :is="isFav ? HeartIcon : HeartIcon" :size="18" style="margin-right:6px" />
            {{ isFav ? '已收藏' : '收藏' }}
          </n-button>
        </div>
        <n-tag v-else :type="pet.status === 3 ? 'success' : 'warning'" size="large" style="margin-top:16px">
          {{ pet.status === 3 ? '已被领养' : pet.status === 1 ? '救助中' : '待救助' }}
        </n-tag>
      </template>
    </n-spin>

    <n-modal v-model:show="showAdopt" title="提交领养申请" preset="card" style="width:500px">
      <n-form :model="adoptForm" label-placement="left" label-width="80px">
        <n-form-item label="真实姓名"><n-input v-model:value="adoptForm.realName" /></n-form-item>
        <n-form-item label="联系电话"><n-input v-model:value="adoptForm.phone" /></n-form-item>
        <n-form-item label="居住条件"><n-input v-model:value="adoptForm.livingCondition" placeholder="如：独居公寓/别墅/有院子" /></n-form-item>
        <n-form-item label="养宠经验"><n-input v-model:value="adoptForm.experience" placeholder="是否养过宠物" /></n-form-item>
        <n-form-item label="申请理由"><n-input v-model:value="adoptForm.reason" type="textarea" :rows="3" /></n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showAdopt = false">取消</n-button>
          <n-button type="primary" @click="submitAdopt">提交申请</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { petApi, adoptionApi, favoriteApi } from '@/api'
import { useUserStore } from '@/store/user'
import { getPetImage } from '@/utils/petImage'
import {
  Cat, Dog, Rabbit, Weight, Palette, HeartPulse, Scissors, Syringe,
  FileText, MapPin, Calendar, HeartHandshake, Heart as HeartIcon, Clock
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const msg = useMessage()
const userStore = useUserStore()
const loading = ref(true)
const pet = ref<any>(null)
const isFav = ref(false)
const showAdopt = ref(false)
const adoptForm = reactive({ realName: '', phone: '', livingCondition: '', experience: '', reason: '', petId: 0 })

const petImage = computed(() => {
  if (!pet.value) return ''
  return getPetImage(pet.value)
})

const speciesIcon = computed(() => {
  const sp = (pet.value?.species || '').toLowerCase()
  if (sp.includes('猫') || sp.includes('cat')) return Cat
  if (sp.includes('狗') || sp.includes('dog')) return Dog
  if (sp.includes('兔') || sp.includes('rabbit')) return Rabbit
  return Cat
})

const attributes = computed(() => {
  if (!pet.value) return []
  return [
    { label: '种类', value: pet.value.species, icon: speciesIcon.value },
    { label: '品种', value: pet.value.breed || '未知', icon: speciesIcon.value },
    { label: '年龄', value: pet.value.age || '未知', icon: Clock },
    { label: '性别', value: pet.value.gender === 1 ? '公' : '母', icon: speciesIcon.value },
    { label: '体重', value: pet.value.weight ? pet.value.weight + 'kg' : '未知', icon: Weight },
    { label: '毛色', value: pet.value.color || '未知', icon: Palette },
    { label: '健康', value: pet.value.healthStatus || '未知', icon: HeartPulse },
    { label: '绝育', value: pet.value.isSterilized ? '已绝育' : '未绝育', icon: Scissors },
    { label: '疫苗', value: pet.value.isVaccinated ? '已接种' : '未接种', icon: Syringe },
  ]
})

onMounted(async () => {
  const id = Number(route.params.id)
  adoptForm.petId = id
  try {
    pet.value = await petApi.detail(id)
    if (userStore.isLogin) {
      try { isFav.value = !!(await favoriteApi.check(id)) } catch {}
    }
  } catch { msg.error('加载失败') }
  loading.value = false
})

async function toggleFav() {
  if (!userStore.isLogin) { router.push('/login'); return }
  try {
    if (isFav.value) { await favoriteApi.remove(pet.value.id); isFav.value = false; msg.success('取消收藏') }
    else { await favoriteApi.add(pet.value.id); isFav.value = true; msg.success('收藏成功') }
  } catch (e: any) { msg.error(e.message || '操作失败') }
}

async function submitAdopt() {
  if (!userStore.isLogin) { router.push('/login'); return }
  if (!adoptForm.realName || !adoptForm.phone) { msg.warning('请填写姓名和电话'); return }
  try { await adoptionApi.apply(adoptForm); msg.success('申请提交成功，请等待审核'); showAdopt.value = false }
  catch (e: any) { msg.error(e.message || '提交失败') }
}
</script>

<style scoped>
.detail-hero { position: relative; border-radius: 16px; overflow: hidden; margin-bottom: 24px; }
.main-img { width: 100%; height: 400px; object-fit: cover; display: block; }
.hero-overlay {
  position: absolute; bottom: 0; left: 0; right: 0;
  padding: 32px 24px 20px;
  background: linear-gradient(transparent, rgba(0,0,0,0.65));
  color: #fff;
}
.hero-overlay h1 { font-size: 2rem; margin: 0 0 10px; text-shadow: 0 2px 8px rgba(0,0,0,0.3); }
.hero-tags { display: flex; gap: 8px; flex-wrap: wrap; }

.attr-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
  margin-bottom: 24px;
}
.attr-card {
  background: #f8faf6;
  border: 1px solid #e8efe4;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  transition: all 0.2s;
}
.attr-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(45,140,90,0.1); border-color: #2d8c5a; }
.attr-icon { color: #2d8c5a; margin-bottom: 6px; }
.attr-label { font-size: 0.8rem; color: #999; margin-bottom: 4px; }
.attr-value { font-size: 1rem; font-weight: 600; color: #333; }

.intro-card { margin-bottom: 24px; border-radius: 12px; }
.desc-text { font-size: 1rem; line-height: 1.8; color: #444; }
.location-row { margin-top: 12px; color: #666; font-size: 0.9rem; }

.action-bar { display: flex; gap: 12px; margin-top: 20px; }
</style>
