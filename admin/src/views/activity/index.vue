<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NDataTable, NModal, NForm, NFormItem, NInput,
  NInputNumber, NDatePicker, useMessage, NPopconfirm, NGrid, NGridItem } from 'naive-ui';
import { fetchActivityList, createActivity, updateActivity, deleteActivity } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, status: null as number | null });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ id: null, title: '', description: '', location: '', startTime: null as number | null, endTime: null as number | null, maxParticipants: 50, status: 0 });
const statusMap: Record<number, { label: string; type: 'default' | 'warning' | 'info' | 'success' | 'error' }> = {
  0: { label: '未开始', type: 'default' }, 1: { label: '进行中', type: 'success' }, 2: { label: '已取消', type: 'error' }, 3: { label: '已结束', type: 'info' }
};

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '标题', key: 'title', width: 180 },
  { title: '地点', key: 'location', width: 160, ellipsis: { tooltip: true } },
  { title: '开始时间', key: 'startTime', width: 160 },
  { title: '最大人数', key: 'maxParticipants', width: 80 },
  { title: '已报名', key: 'currentParticipants', width: 70 },
  { title: '状态', key: 'status', width: 80, render: (row: any) => { const s = statusMap[row.status]; return s ? h(NTag, { type: s.type, size: 'small' }, () => s.label) : ''; }},
  { title: '操作', key: 'actions', width: 160, render: (row: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', type: 'primary', onClick: () => { isEdit.value = true; Object.assign(form, row); showModal.value = true; } }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确认删除？' })
  ])}
];

async function loadData() { loading.value = true; try { const { data: res, error } = await fetchActivityList(query); if (!error && res) { data.value = res.records || []; total.value = res.total || 0; } } finally { loading.value = false; } }
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, title: '', description: '', location: '', startTime: null, endTime: null, maxParticipants: 50, status: 0 }); showModal.value = true; }
async function handleSubmit() { const fn = isEdit.value ? updateActivity(form.id, form) : createActivity(form); const { error } = await fn; if (!error) { message.success(isEdit.value ? '更新成功' : '创建成功'); showModal.value = false; loadData(); } }
async function handleDelete(id: number) { const { error } = await deleteActivity(id); if (!error) { message.success('删除成功'); loadData(); } }
function handlePageChange(page: number) { query.page = page; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px">
      <NButton type="primary" @click="loadData">刷新</NButton>
      <NButton type="success" @click="handleAdd">新增活动</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" :bordered="false" striped />
    <NModal v-model:show="showModal" :title="isEdit ? '编辑活动' : '新增活动'" preset="card" style="width:600px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NFormItem label="标题"><NInput v-model:value="form.title" /></NFormItem>
        <NFormItem label="地点"><NInput v-model:value="form.location" /></NFormItem>
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="最大人数"><NInputNumber v-model:value="form.maxParticipants" :min="1" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><n-select v-model:value="form.status" :options="[{label:'未开始',value:0},{label:'进行中',value:1},{label:'已取消',value:2},{label:'已结束',value:3}]" /></NFormItem></NGridItem>
        </NGrid>
        <NFormItem label="描述"><NInput v-model:value="form.description" type="textarea" :rows="3" /></NFormItem>
      </NForm>
      <template #footer><NSpace justify="end"><NButton @click="showModal = false">取消</NButton><NButton type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</NButton></NSpace></template>
    </NModal>
  </div>
</template>
