<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NDataTable, NInput, NSelect, NModal, NForm, NFormItem,
  useMessage, NPopconfirm, NGrid, NGridItem, NInputNumber } from 'naive-ui';
import { fetchStationList, createStation, updateStation, deleteStation } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, keyword: '' });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ id: null, name: '', address: '', capacity: 50, currentCount: 0, contactPhone: '', manager: '', description: '', status: 1 });

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '名称', key: 'name', width: 160 },
  { title: '地址', key: 'address', width: 200, ellipsis: { tooltip: true } },
  { title: '容量', key: 'capacity', width: 70 },
  { title: '当前数量', key: 'currentCount', width: 80 },
  { title: '使用率', key: 'usage', width: 80, render: (row: any) => {
    const pct = row.capacity > 0 ? Math.round(row.currentCount * 100 / row.capacity) : 0;
    return h(NTag, { type: pct > 80 ? 'error' : pct > 50 ? 'warning' : 'success', size: 'small' }, () => pct + '%');
  }},
  { title: '联系电话', key: 'contactPhone', width: 120 },
  { title: '负责人', key: 'manager', width: 80 },
  { title: '状态', key: 'status', width: 70, render: (row: any) => h(NTag, { type: row.status === 1 ? 'success' : 'error', size: 'small' }, () => row.status === 1 ? '运营中' : '已关闭') },
  { title: '操作', key: 'actions', width: 160, render: (row: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', type: 'primary', onClick: () => { isEdit.value = true; Object.assign(form, row); showModal.value = true; } }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确认删除？' })
  ])}
];

async function loadData() {
  loading.value = true;
  try {
    const { data: res, error } = await fetchStationList(query);
    if (!error && res) { data.value = res.records || []; total.value = res.total || 0; }
  } finally { loading.value = false; }
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(form, { id: null, name: '', address: '', capacity: 50, currentCount: 0, contactPhone: '', manager: '', description: '', status: 1 });
  showModal.value = true;
}

async function handleSubmit() {
  const fn = isEdit.value ? updateStation(form.id, form) : createStation(form);
  const { error } = await fn;
  if (!error) { message.success(isEdit.value ? '更新成功' : '创建成功'); showModal.value = false; loadData(); }
}

async function handleDelete(id: number) { const { error } = await deleteStation(id); if (!error) { message.success('删除成功'); loadData(); } }
function handlePageChange(page: number) { query.page = page; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px">
      <NInput v-model:value="query.keyword" placeholder="搜索救助站名称" clearable style="width:200px" @keyup.enter="loadData" />
      <NButton type="primary" @click="loadData">查询</NButton>
      <NButton type="success" @click="handleAdd">新增救助站</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" :bordered="false" striped />
    <NModal v-model:show="showModal" :title="isEdit ? '编辑救助站' : '新增救助站'" preset="card" style="width:600px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="名称"><NInput v-model:value="form.name" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="负责人"><NInput v-model:value="form.manager" /></NFormItem></NGridItem>
          <NGridItem :span="2"><NFormItem label="地址"><NInput v-model:value="form.address" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="容量"><NInputNumber v-model:value="form.capacity" :min="1" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="联系电话"><NInput v-model:value="form.contactPhone" /></NFormItem></NGridItem>
          <NGridItem :span="2"><NFormItem label="描述"><NInput v-model:value="form.description" type="textarea" :rows="2" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #footer><NSpace justify="end"><NButton @click="showModal = false">取消</NButton><NButton type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</NButton></NSpace></template>
    </NModal>
  </div>
</template>
