<template>
  <div class="container">
    <div class="page-header"><h2><Siren :size="22" style="vertical-align:-4px;margin-right:6px" /> 求助中心</h2><p>发现流浪动物需要救助？提交信息，我们会尽快响应！</p></div>
    <n-card title="提交救助信息" style="max-width:600px;margin:0 auto 40px">
      <n-form :model="form" label-placement="left" label-width="80px">
        <n-form-item label="地点"><n-input v-model:value="form.location" placeholder="发现流浪动物的地点" /></n-form-item>
        <n-form-item label="描述"><n-input v-model:value="form.description" type="textarea" :rows="4" placeholder="描述动物的情况" /></n-form-item>
        <n-form-item label="紧急程度"><n-select v-model:value="form.urgencyLevel" :options="[{label:'一般',value:1},{label:'紧急',value:2},{label:'非常紧急',value:3}]" /></n-form-item>
        <n-form-item label="联系电话"><n-input v-model:value="form.reporterPhone" placeholder="您的联系电话" /></n-form-item>
        <n-button type="primary" block @click="handleSubmit">提交求助</n-button>
      </n-form>
    </n-card>
    <h2 class="section-title">最近救助记录</h2>
    <n-spin :show="loading">
      <n-list bordered>
        <n-list-item v-for="r in list" :key="r.id">
          <n-thing :title="r.location" :description="r.description">
            <template #header-extra>
              <n-tag :type="r.status===0?'error':r.status===1?'warning':'success'" size="small">{{ ['待处理','处理中','已完成','已取消'][r.status] }}</n-tag>
            </template>
          </n-thing>
        </n-list-item>
      </n-list>
      <n-empty v-if="!loading && !list.length" description="暂无记录" style="margin:40px 0" />
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { useRouter } from 'vue-router'
import { rescueApi } from '@/api'
import { useUserStore } from '@/store/user'
import { Siren } from 'lucide-vue-next'

const msg = useMessage()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const list = ref<any[]>([])
const form = reactive({ location: '', description: '', urgencyLevel: 1, reporterPhone: '' })

async function handleSubmit() {
  if (!userStore.isLogin) { router.push('/login'); return }
  if (!form.location || !form.description) { msg.warning('请填写地点和描述'); return }
  try { await rescueApi.create(form); msg.success('提交成功'); loadList(); form.location = ''; form.description = '' }
  catch (e: any) { msg.error(e.message || '提交失败') }
}

async function loadList() {
  loading.value = true
  try { const res: any = await rescueApi.list({ page: 1, size: 20 }); list.value = res?.records || [] }
  catch {} finally { loading.value = false }
}
onMounted(() => loadList())
</script>
