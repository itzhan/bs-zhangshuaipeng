<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NDataTable, NSelect, NModal, NForm, NFormItem, NInput,
  NInputNumber, useMessage, NPopconfirm, NGrid, NGridItem } from 'naive-ui';
import { fetchSupplyList, createSupply, updateSupply, deleteSupply, fetchLowStockSupplies, fetchAllStations } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, stationId: null as number | null, category: null as string | null });
const showModal = ref(false);
const isEdit = ref(false);
const stations = ref<any[]>([]);
const categoryOptions = [{ label: '全部', value: null }, { label: '食物', value: '食物' }, { label: '药品', value: '药品' }, { label: '用品', value: '用品' }, { label: '其他', value: '其他' }];
const form = reactive<any>({ id: null, name: '', category: '食物', quantity: 0, unit: '千克', stationId: null, threshold: 10, remark: '' });

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '名称', key: 'name', width: 140 },
  { title: '分类', key: 'category', width: 70 },
  { title: '数量', key: 'quantity', width: 70 },
  { title: '单位', key: 'unit', width: 60 },
  { title: '预警值', key: 'threshold', width: 70 },
  { title: '库存状态', key: 'stockStatus', width: 80, render: (row: any) =>
    h(NTag, { type: row.quantity <= row.threshold ? 'error' : 'success', size: 'small' }, () => row.quantity <= row.threshold ? '偏低' : '充足') },
  { title: '备注', key: 'remark', width: 120 },
  { title: '操作', key: 'actions', width: 160, render: (row: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', type: 'primary', onClick: () => { isEdit.value = true; Object.assign(form, row); showModal.value = true; } }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确认删除？' })
  ])}
];

async function loadData() { loading.value = true; try { const { data: res, error } = await fetchSupplyList(query); if (!error && res) { data.value = res.records || []; total.value = res.total || 0; } } finally { loading.value = false; } }
async function loadStations() { const { data: res, error } = await fetchAllStations(); if (!error && res) stations.value = res; }
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, name: '', category: '食物', quantity: 0, unit: '千克', stationId: null, threshold: 10, remark: '' }); showModal.value = true; }
async function handleSubmit() { const fn = isEdit.value ? updateSupply(form.id, form) : createSupply(form); const { error } = await fn; if (!error) { message.success(isEdit.value ? '更新成功' : '创建成功'); showModal.value = false; loadData(); } }
async function handleDelete(id: number) { const { error } = await deleteSupply(id); if (!error) { message.success('删除成功'); loadData(); } }
function handlePageChange(page: number) { query.page = page; loadData(); }
onMounted(() => { loadData(); loadStations(); });
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px flex-wrap">
      <NSelect v-model:value="query.stationId" :options="[{label:'全部救助站',value:null},...stations.map((s:any)=>({label:s.name,value:s.id}))]" placeholder="救助站" style="width:180px" @update:value="loadData" />
      <NSelect v-model:value="query.category" :options="categoryOptions" placeholder="分类" style="width:120px" @update:value="loadData" />
      <NButton type="primary" @click="loadData">查询</NButton>
      <NButton type="success" @click="handleAdd">新增物资</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" :bordered="false" striped />
    <NModal v-model:show="showModal" :title="isEdit ? '编辑物资' : '新增物资'" preset="card" style="width:550px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="名称"><NInput v-model:value="form.name" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="分类"><NSelect v-model:value="form.category" :options="categoryOptions.filter(o=>o.value)" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="数量"><NInputNumber v-model:value="form.quantity" :min="0" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="单位"><NInput v-model:value="form.unit" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="预警值"><NInputNumber v-model:value="form.threshold" :min="0" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="救助站"><NSelect v-model:value="form.stationId" :options="stations.map((s:any)=>({label:s.name,value:s.id}))" /></NFormItem></NGridItem>
          <NGridItem :span="2"><NFormItem label="备注"><NInput v-model:value="form.remark" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #footer><NSpace justify="end"><NButton @click="showModal = false">取消</NButton><NButton type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</NButton></NSpace></template>
    </NModal>
  </div>
</template>
