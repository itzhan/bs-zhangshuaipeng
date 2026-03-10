<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NDataTable, NInput, NSelect, NModal, NForm, NFormItem,
  useMessage, NPopconfirm, NGrid, NGridItem } from 'naive-ui';
import { fetchRescueList, createRescue, updateRescue, updateRescueStatus, deleteRescue } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, status: null as number | null, keyword: '' });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ id: null, location: '', description: '', urgencyLevel: 1, reporterPhone: '', status: 0 });
const statusMap: Record<number, { label: string; type: 'default' | 'warning' | 'info' | 'success' | 'error' }> = {
  0: { label: '待处理', type: 'error' }, 1: { label: '处理中', type: 'warning' }, 2: { label: '已完成', type: 'success' }, 3: { label: '已取消', type: 'default' }
};
const statusOptions = [{ label: '全部', value: null }, ...Object.entries(statusMap).map(([k, v]) => ({ label: v.label, value: Number(k) }))];

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '地点', key: 'location', width: 160, ellipsis: { tooltip: true } },
  { title: '描述', key: 'description', width: 200, ellipsis: { tooltip: true } },
  { title: '紧急度', key: 'urgencyLevel', width: 70, render: (row: any) => ['', '一般', '紧急', '非常紧急'][row.urgencyLevel] || '' },
  { title: '报告人电话', key: 'reporterPhone', width: 120 },
  { title: '状态', key: 'status', width: 80, render: (row: any) => { const s = statusMap[row.status]; return s ? h(NTag, { type: s.type, size: 'small' }, () => s.label) : ''; }},
  { title: '创建时间', key: 'createdAt', width: 160 },
  { title: '操作', key: 'actions', width: 250, render: (row: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', type: 'primary', onClick: () => { isEdit.value = true; Object.assign(form, row); showModal.value = true; } }, () => '编辑'),
    ...(row.status < 2 ? [h(NButton, { size: 'small', type: 'success', onClick: () => handleChangeStatus(row.id, row.status + 1) }, () => row.status === 0 ? '开始处理' : '标记完成')] : []),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确认删除？' })
  ])}
];

async function loadData() { loading.value = true; try { const { data: res, error } = await fetchRescueList(query); if (!error && res) { data.value = res.records || []; total.value = res.total || 0; } } finally { loading.value = false; } }
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, location: '', description: '', urgencyLevel: 1, reporterPhone: '', status: 0 }); showModal.value = true; }
async function handleSubmit() { const fn = isEdit.value ? updateRescue(form.id, form) : createRescue(form); const { error } = await fn; if (!error) { message.success('操作成功'); showModal.value = false; loadData(); } }
async function handleChangeStatus(id: number, status: number) { const { error } = await updateRescueStatus(id, status); if (!error) { message.success('状态更新'); loadData(); } }
async function handleDelete(id: number) { const { error } = await deleteRescue(id); if (!error) { message.success('删除成功'); loadData(); } }
function handlePageChange(page: number) { query.page = page; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px">
      <NInput v-model:value="query.keyword" placeholder="搜索地点" clearable style="width:200px" @keyup.enter="loadData" />
      <NSelect v-model:value="query.status" :options="statusOptions" placeholder="状态" style="width:120px" @update:value="loadData" />
      <NButton type="primary" @click="loadData">查询</NButton>
      <NButton type="success" @click="handleAdd">新增救助</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" :bordered="false" striped />
    <NModal v-model:show="showModal" :title="isEdit ? '编辑救助记录' : '新增救助'" preset="card" style="width:550px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NFormItem label="地点"><NInput v-model:value="form.location" /></NFormItem>
        <NFormItem label="描述"><NInput v-model:value="form.description" type="textarea" :rows="3" /></NFormItem>
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="紧急度"><NSelect v-model:value="form.urgencyLevel" :options="[{label:'一般',value:1},{label:'紧急',value:2},{label:'非常紧急',value:3}]" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="联系电话"><NInput v-model:value="form.reporterPhone" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #footer><NSpace justify="end"><NButton @click="showModal = false">取消</NButton><NButton type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</NButton></NSpace></template>
    </NModal>
  </div>
</template>
