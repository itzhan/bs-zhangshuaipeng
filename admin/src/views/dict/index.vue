<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NDataTable, NInput, NSelect, NModal, NForm, NFormItem,
  NInputNumber, useMessage, NPopconfirm, NGrid, NGridItem } from 'naive-ui';
import { fetchDictList, createDict, updateDict, deleteDict } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 20, type: '' });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ id: null, type: '', label: '', value: '', sort: 0, status: 1, remark: '' });
const typeOptions = ['pet_species', 'pet_status', 'rescue_status', 'adoption_status', 'supply_category', 'shift_type'];

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '字典类型', key: 'type', width: 140 },
  { title: '标签', key: 'label', width: 100 },
  { title: '值', key: 'value', width: 100 },
  { title: '排序', key: 'sort', width: 60 },
  { title: '状态', key: 'status', width: 60, render: (row: any) => row.status === 1 ? '启用' : '禁用' },
  { title: '操作', key: 'actions', width: 160, render: (row: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', type: 'primary', onClick: () => { isEdit.value = true; Object.assign(form, row); showModal.value = true; } }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确认？' })
  ])}
];

async function loadData() { loading.value = true; try { const { data: res, error } = await fetchDictList(query); if (!error && res) { data.value = res.records || []; total.value = res.total || 0; } } finally { loading.value = false; } }
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, type: '', label: '', value: '', sort: 0, status: 1, remark: '' }); showModal.value = true; }
async function handleSubmit() { const fn = isEdit.value ? updateDict(form.id, form) : createDict(form); const { error } = await fn; if (!error) { message.success('操作成功'); showModal.value = false; loadData(); } }
async function handleDelete(id: number) { const { error } = await deleteDict(id); if (!error) { message.success('删除成功'); loadData(); } }
function handlePageChange(page: number) { query.page = page; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px">
      <NSelect v-model:value="query.type" :options="[{label:'全部类型',value:''},...typeOptions.map(t=>({label:t,value:t}))]" style="width:180px" @update:value="loadData" />
      <NButton type="primary" @click="loadData">查询</NButton>
      <NButton type="success" @click="handleAdd">新增字典</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" :bordered="false" striped />
    <NModal v-model:show="showModal" :title="isEdit ? '编辑字典' : '新增字典'" preset="card" style="width:500px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="类型"><NInput v-model:value="form.type" placeholder="如 pet_species" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="标签"><NInput v-model:value="form.label" placeholder="显示名" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="值"><NInput v-model:value="form.value" placeholder="键值" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="排序"><NInputNumber v-model:value="form.sort" :min="0" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><NSelect v-model:value="form.status" :options="[{label:'启用',value:1},{label:'禁用',value:0}]" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #footer><NSpace justify="end"><NButton @click="showModal = false">取消</NButton><NButton type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</NButton></NSpace></template>
    </NModal>
  </div>
</template>
