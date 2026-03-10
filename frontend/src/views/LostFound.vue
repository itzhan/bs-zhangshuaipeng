<template>
  <div class="container">
    <div class="page-header">
      <h2><SearchIcon :size="22" style="vertical-align:-4px;margin-right:6px" /> 寻宠招领</h2>
      <p>丢失了宠物或捡到了流浪动物？在这里发布信息</p>
    </div>
    <div style="display:flex;gap:12px;margin-bottom:20px">
      <n-select v-model:value="query.type" :options="[{label:'全部',value:null},{label:'寻宠',value:0},{label:'招领',value:1}]" style="width:120px" @update:value="load" />
      <n-input v-model:value="query.keyword" placeholder="搜索" clearable style="width:200px" @keyup.enter="load" />
      <n-button type="primary" @click="load">搜索</n-button>
      <n-button type="success" @click="showModal = true">
        <Plus :size="16" style="margin-right:4px" /> 发布信息
      </n-button>
    </div>
    <n-spin :show="loading">
      <div class="card-grid">
        <n-card v-for="item in list" :key="item.id" hoverable class="lost-card" @click="$router.push(`/lost-found/${item.id}`)">
          <template #cover>
            <div class="card-cover">
              <img :src="itemPhoto(item)" />
              <n-tag :type="item.type===0?'warning':'info'" size="small" class="card-type-tag">{{ item.type===0?'寻宠':'招领' }}</n-tag>
            </div>
          </template>
          <h3 class="card-title">{{ item.petName || (item.type === 0 ? '寻找宠物' : '招领宠物') }}</h3>
          <p class="card-meta"><MapPin :size="14" style="vertical-align:-2px;margin-right:2px" /> {{ item.location }}</p>
          <p class="card-desc">{{ item.description }}</p>
          <div class="card-footer">
            <span class="card-date"><CalendarIcon :size="14" style="vertical-align:-2px;margin-right:2px" /> {{ item.lostDate || item.foundDate }}</span>
            <n-tag :type="item.status === 1 ? 'success' : 'default'" size="small">{{ item.status === 1 ? '已找到' : '寻找中' }}</n-tag>
          </div>
        </n-card>
      </div>
      <n-empty v-if="!loading && !list.length" description="暂无信息" style="margin:40px 0" />
    </n-spin>

    <!-- 发布弹窗 -->
    <n-modal v-model:show="showModal" title="发布寻宠/招领" preset="card" style="width:550px">
      <n-form :model="form" label-placement="left" label-width="80px">
        <n-form-item label="类型">
          <n-select v-model:value="form.type" :options="[{label:'寻宠',value:0},{label:'招领',value:1}]" />
        </n-form-item>
        <n-form-item label="宠物名">
          <n-input v-model:value="form.petName" placeholder="如知道名字请填写" />
        </n-form-item>
        <n-form-item label="种类">
          <n-input v-model:value="form.species" placeholder="猫/狗/兔/其他" />
        </n-form-item>
        <n-form-item label="颜色">
          <n-input v-model:value="form.color" placeholder="毛色特征" />
        </n-form-item>
        <n-form-item label="地点">
          <n-input v-model:value="form.location" placeholder="走失/发现地点" />
        </n-form-item>
        <n-form-item label="日期">
          <n-date-picker v-model:value="form.lostDateTs" type="date" style="width:100%" />
        </n-form-item>
        <n-form-item label="联系电话">
          <n-input v-model:value="form.contactPhone" placeholder="您的联系电话" />
        </n-form-item>
        <n-form-item label="照片">
          <n-upload
            :max="1"
            :custom-request="handleUpload"
            list-type="image-card"
            v-model:file-list="fileList"
            accept="image/*"
          >
            <div style="display:flex;flex-direction:column;align-items:center;gap:4px">
              <ImagePlus :size="24" style="color:#999" />
              <span style="font-size:12px;color:#999">上传照片</span>
            </div>
          </n-upload>
        </n-form-item>
        <n-form-item label="描述">
          <n-input v-model:value="form.description" type="textarea" :rows="3" placeholder="详细描述宠物特征和情况" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal=false">取消</n-button>
          <n-button type="primary" @click="handleSubmit">发布</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import type { UploadCustomRequestOptions } from 'naive-ui'
import { lostFoundApi, uploadFile } from '@/api'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { Search as SearchIcon, MapPin, Calendar as CalendarIcon, Plus, ImagePlus } from 'lucide-vue-next'
import { getLostFoundImage } from '@/utils/lostFoundImage'

const msg = useMessage(); const router = useRouter(); const userStore = useUserStore()
const loading = ref(false); const list = ref<any[]>([]); const showModal = ref(false)
const query = reactive({ page: 1, size: 20, type: null as number | null, keyword: '' })
const form = reactive({ type: 0, petName: '', species: '', color: '', location: '', contactPhone: '', description: '', photo: '', lostDateTs: null as number | null })
const fileList = ref<any[]>([])

const itemPhoto = (item: any) => getLostFoundImage(item)

async function handleUpload({ file, onFinish, onError }: UploadCustomRequestOptions) {
  try {
    const url: any = await uploadFile(file.file as File, 'lost-found')
    form.photo = url
    onFinish()
  } catch { onError(); msg.error('上传失败') }
}

async function load() {
  loading.value = true
  try { const r: any = await lostFoundApi.list(query); list.value = r?.records || [] }
  catch {} finally { loading.value = false }
}

async function handleSubmit() {
  if (!userStore.isLogin) { router.push('/login'); return }
  if (!form.location || !form.description) { msg.warning('请填写地点和描述'); return }
  if (!form.contactPhone) { msg.warning('请填写联系电话'); return }
  const submitData: any = { ...form }
  if (form.lostDateTs) {
    submitData.lostDate = new Date(form.lostDateTs).toISOString().split('T')[0]
  }
  delete submitData.lostDateTs
  try { await lostFoundApi.create(submitData); msg.success('发布成功'); showModal.value = false; fileList.value = []; load() }
  catch (e: any) { msg.error(e.message || '发布失败') }
}

onMounted(() => load())
</script>

<style scoped>
.lost-card { cursor: pointer; transition: all 0.2s; }
.lost-card:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.1); }
.card-cover { position: relative; height: 180px; overflow: hidden; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; }
.card-type-tag { position: absolute; top: 8px; left: 8px; }
.card-title { font-size: 1.1rem; font-weight: 600; margin: 0 0 8px; }
.card-meta { color: #888; font-size: 0.85rem; margin-bottom: 6px; }
.card-desc { color: #666; font-size: 0.9rem; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.card-date { color: #999; font-size: 0.8rem; }
</style>
