<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { NButton, NSpace, NTag, NDataTable, NInput, NSelect, NModal, NForm, NFormItem, NImage,
  NInputNumber, NSwitch, useMessage, NPopconfirm, NDatePicker, NGrid, NGridItem } from 'naive-ui';
import type { DataTableColumns } from 'naive-ui';
import { fetchPetList, createPet, updatePet, deletePet, updatePetStatus, fetchAllStations } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, species: null as string | null, status: null as number | null, keyword: '' });
const showModal = ref(false);
const isEdit = ref(false);
const stations = ref<any[]>([]);

const speciesOptions = [
  { label: '全部', value: null }, { label: '猫', value: '猫' }, { label: '狗', value: '狗' },
  { label: '兔', value: '兔' }, { label: '其他', value: '其他' }
];
const statusOptions = [
  { label: '全部', value: null }, { label: '待救助', value: 0 }, { label: '救助中', value: 1 },
  { label: '待领养', value: 2 }, { label: '已领养', value: 3 }, { label: '已归还', value: 4 }
];
const statusTagMap: Record<number, { label: string; type: 'default' | 'warning' | 'info' | 'success' | 'error' }> = {
  0: { label: '待救助', type: 'error' }, 1: { label: '救助中', type: 'warning' },
  2: { label: '待领养', type: 'info' }, 3: { label: '已领养', type: 'success' }, 4: { label: '已归还', type: 'default' }
};
const genderMap: Record<number, string> = { 1: '公', 2: '母' };

const form = reactive<any>({
  id: null, name: '', species: '猫', breed: '', age: '', gender: 1, weight: null,
  color: '', healthStatus: '健康', isSterilized: 0, isVaccinated: 0, description: '',
  photo: '', location: '', stationId: null, status: 2
});

const columns: DataTableColumns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '名称', key: 'name', width: 80 },
  { title: '种类', key: 'species', width: 60 },
  { title: '品种', key: 'breed', width: 100 },
  { title: '年龄', key: 'age', width: 80 },
  { title: '性别', key: 'gender', width: 60, render: (row: any) => genderMap[row.gender] || '未知' },
  { title: '健康状况', key: 'healthStatus', width: 100 },
  { title: '状态', key: 'status', width: 80, render: (row: any) => {
    const s = statusTagMap[row.status]; return s ? h(NTag, { type: s.type, size: 'small' }, () => s.label) : '未知';
  }},
  { title: '操作', key: 'actions', width: 200, render: (row: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', type: 'primary', onClick: () => handleEdit(row) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, {
      trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'),
      default: () => '确认删除？'
    })
  ])}
];

import { h } from 'vue';

async function loadData() {
  loading.value = true;
  try {
    const { data: res, error } = await fetchPetList(query);
    if (!error && res) { data.value = res.records || []; total.value = res.total || 0; }
  } finally { loading.value = false; }
}

async function loadStations() {
  const { data: res, error } = await fetchAllStations();
  if (!error && res) stations.value = res;
}

function handleAdd() {
  isEdit.value = false;
  Object.assign(form, { id: null, name: '', species: '猫', breed: '', age: '', gender: 1, weight: null,
    color: '', healthStatus: '健康', isSterilized: 0, isVaccinated: 0, description: '',
    photo: '', location: '', stationId: null, status: 2 });
  showModal.value = true;
}

function handleEdit(row: any) {
  isEdit.value = true;
  Object.assign(form, { ...row });
  showModal.value = true;
}

async function handleSubmit() {
  try {
    if (isEdit.value) {
      const { error } = await updatePet(form.id, form);
      if (!error) { message.success('更新成功'); showModal.value = false; loadData(); }
    } else {
      const { error } = await createPet(form);
      if (!error) { message.success('创建成功'); showModal.value = false; loadData(); }
    }
  } catch { message.error('操作失败'); }
}

async function handleDelete(id: number) {
  const { error } = await deletePet(id);
  if (!error) { message.success('删除成功'); loadData(); }
}

function handlePageChange(page: number) { query.page = page; loadData(); }

onMounted(() => { loadData(); loadStations(); });
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px flex-wrap">
      <NInput v-model:value="query.keyword" placeholder="搜索宠物名称/品种" clearable style="width:200px" @keyup.enter="loadData" />
      <NSelect v-model:value="query.species" :options="speciesOptions" placeholder="种类" style="width:120px" @update:value="loadData" />
      <NSelect v-model:value="query.status" :options="statusOptions" placeholder="状态" style="width:120px" @update:value="loadData" />
      <NButton type="primary" @click="loadData">查询</NButton>
      <NButton type="success" @click="handleAdd">新增宠物</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{
      page: query.page, pageSize: query.size, itemCount: total,
      onUpdatePage: handlePageChange, showSizePicker: false
    }" :bordered="false" striped />

    <NModal v-model:show="showModal" :title="isEdit ? '编辑宠物' : '新增宠物'" preset="card" style="width:700px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="名称"><NInput v-model:value="form.name" placeholder="宠物名称" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="种类"><NSelect v-model:value="form.species" :options="speciesOptions.filter(o=>o.value)" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="品种"><NInput v-model:value="form.breed" placeholder="品种" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="年龄"><NInput v-model:value="form.age" placeholder="约2岁" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="性别"><NSelect v-model:value="form.gender" :options="[{label:'公',value:1},{label:'母',value:2}]" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="体重(kg)"><NInputNumber v-model:value="form.weight" :min="0" :max="100" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="毛色"><NInput v-model:value="form.color" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="健康状况"><NInput v-model:value="form.healthStatus" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="已绝育"><NSwitch v-model:value="form.isSterilized" :checked-value="1" :unchecked-value="0" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="已疫苗"><NSwitch v-model:value="form.isVaccinated" :checked-value="1" :unchecked-value="0" /></NFormItem></NGridItem>
          <NGridItem :span="2"><NFormItem label="描述"><NInput v-model:value="form.description" type="textarea" :rows="3" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="发现地点"><NInput v-model:value="form.location" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="所属救助站"><NSelect v-model:value="form.stationId" :options="stations.map((s:any)=>({label:s.name,value:s.id}))" clearable /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><NSelect v-model:value="form.status" :options="statusOptions.filter(o=>o.value!==null)" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #footer>
        <NSpace justify="end">
          <NButton @click="showModal = false">取消</NButton>
          <NButton type="primary" @click="handleSubmit">{{ isEdit ? '更新' : '创建' }}</NButton>
        </NSpace>
      </template>
    </NModal>
  </div>
</template>
