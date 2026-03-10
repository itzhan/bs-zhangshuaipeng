<template>
  <div>
    <section class="hero">
      <h1><PawPrint :size="36" style="vertical-align:-6px;margin-right:8px" /> 让爱回家</h1>
      <p>每一个流浪的小生命都值得被温柔以待。加入我们，给它们一个温暖的家。</p>
      <n-space justify="center">
        <n-button type="primary" size="large" @click="$router.push('/pets')">浏览待领养宠物</n-button>
        <n-button size="large" ghost @click="$router.push('/rescue')">我要求助</n-button>
      </n-space>
    </section>
    <div class="container">
      <div class="stat-bar">
        <div class="stat-item" v-for="s in stats" :key="s.label">
          <component :is="s.icon" :size="22" style="color:#2d8c5a;margin-bottom:4px" />
          <div class="num">{{ s.value }}</div>
          <div class="label">{{ s.label }}</div>
        </div>
      </div>
      <h2 class="section-title">待领养宠物</h2>
      <div class="card-grid">
        <div class="pet-card" v-for="pet in pets" :key="pet.id" @click="$router.push(`/pets/${pet.id}`)">
          <img :src="petImage(pet)" :alt="pet.name" />
          <div class="info">
            <h3>{{ pet.name }}</h3>
            <p>{{ pet.breed }} · {{ pet.age }} · {{ pet.gender === 1 ? '公' : '母' }}</p>
            <div class="tags">
              <span class="tag">{{ pet.species }}</span>
              <span class="tag" v-if="pet.isSterilized">已绝育</span>
              <span class="tag" v-if="pet.isVaccinated">已疫苗</span>
            </div>
          </div>
        </div>
      </div>
      <div style="text-align:center;margin:20px 0">
        <n-button @click="$router.push('/pets')">查看更多宠物 <ArrowRight :size="16" style="margin-left:4px;vertical-align:-2px" /></n-button>
      </div>
      <h2 class="section-title">近期活动</h2>
      <div class="card-grid">
        <div class="pet-card" v-for="act in activities" :key="act.id">
          <div class="info">
            <h3>{{ act.title }}</h3>
            <p><MapPin :size="14" style="vertical-align:-2px;margin-right:4px" />{{ act.location }}</p>
            <p><Calendar :size="14" style="vertical-align:-2px;margin-right:4px" />{{ act.startTime }}</p>
            <p style="margin-top:8px">{{ act.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { petApi, activityApi, dashboardApi } from '@/api'
import { PawPrint, Heart, ShieldCheck, Users, ArrowRight, MapPin, Calendar, Stethoscope } from 'lucide-vue-next'
import { getPetImage } from '@/utils/petImage'

const petImage = (pet: any) => getPetImage(pet)

const pets = ref<any[]>([])
const activities = ref<any[]>([])
const stats = ref([
  { label: '累计救助', value: 0, icon: PawPrint },
  { label: '待领养', value: 0, icon: Heart },
  { label: '已领养', value: 0, icon: ShieldCheck },
  { label: '注册用户', value: 0, icon: Users }
])

onMounted(async () => {
  try {
    const pRes: any = await petApi.list({ page: 1, size: 8, status: 2 })
    pets.value = pRes?.records || []
  } catch {}
  try {
    const aRes: any = await activityApi.list({ page: 1, size: 4, status: 1 })
    activities.value = aRes?.records || []
  } catch {}
  try {
    const d: any = await dashboardApi.overview()
    stats.value = [
      { label: '累计救助', value: d?.totalPets || 0, icon: PawPrint },
      { label: '待领养', value: d?.waitingAdoption || 0, icon: Heart },
      { label: '已领养', value: d?.adopted || 0, icon: ShieldCheck },
      { label: '注册用户', value: d?.totalUsers || 0, icon: Users }
    ]
  } catch {}
})
</script>
