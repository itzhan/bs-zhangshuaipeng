<template>
  <div class="container">
    <div class="page-header">
      <h2><PawPrint :size="22" style="vertical-align:-4px;margin-right:6px" /> 待领养宠物</h2>
      <p>给它们一个温暖的家</p>
    </div>
    <div style="display:flex;gap:12px;margin-bottom:20px;flex-wrap:wrap">
      <n-input v-model:value="query.keyword" placeholder="搜索名称/品种" clearable style="width:200px" @keyup.enter="load" />
      <n-select v-model:value="query.species" :options="[{label:'全部种类',value:null},{label:'猫',value:'猫'},{label:'狗',value:'狗'},{label:'兔',value:'兔'}]" style="width:120px" @update:value="load" />
      <n-select v-model:value="query.gender" :options="[{label:'全部性别',value:null},{label:'公',value:1},{label:'母',value:2}]" style="width:110px" @update:value="load" />
      <n-button type="primary" @click="load">搜索</n-button>
    </div>
    <n-spin :show="loading">
      <div class="card-grid">
        <div class="pet-card" v-for="pet in list" :key="pet.id" @click="$router.push(`/pets/${pet.id}`)">
          <img :src="petImage(pet)" :alt="pet.name" />
          <div class="info">
            <h3>{{ pet.name }}</h3>
            <p>{{ pet.breed || '未知品种' }} · {{ pet.age || '未知年龄' }} · {{ pet.gender === 1 ? '公' : '母' }}</p>
            <p style="color:#888;font-size:0.85rem"><MapPin :size="14" style="vertical-align:-2px;margin-right:2px" /> {{ pet.location || '未知' }}</p>
            <div class="tags">
              <span class="tag">{{ pet.species }}</span>
              <span class="tag" v-if="pet.healthStatus">{{ pet.healthStatus }}</span>
            </div>
          </div>
        </div>
      </div>
      <n-empty v-if="!loading && !list.length" description="暂无待领养宠物" style="margin:60px 0" />
    </n-spin>
    <div style="display:flex;justify-content:center;margin:24px 0">
      <n-pagination v-model:page="query.page" :page-count="Math.ceil(total / query.size)" @update:page="load" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { petApi } from '@/api'
import { PawPrint, MapPin } from 'lucide-vue-next'
import { getPetImage } from '@/utils/petImage'

const petImage = (pet: any) => getPetImage(pet)

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const query = reactive({ page: 1, size: 12, keyword: '', species: null as string | null, gender: null as number | null, status: 2 })

async function load() {
  loading.value = true
  try {
    const res: any = await petApi.list(query)
    list.value = res?.records || []
    total.value = res?.total || 0
  } catch {} finally { loading.value = false }
}
onMounted(() => load())
</script>
