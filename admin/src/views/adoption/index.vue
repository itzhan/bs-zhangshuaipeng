<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NDataTable, NSelect, NModal, NForm, NFormItem, NInput,
  useMessage, NPopconfirm } from 'naive-ui';
import { fetchAdoptionList, reviewAdoption, deleteAdoption } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, status: null as number | null });
const showReview = ref(false);
const reviewForm = reactive({ id: 0, status: 1, reviewNote: '' });

const statusOptions = [
  { label: '全部', value: null }, { label: '待审核', value: 0 },
  { label: '已通过', value: 1 }, { label: '已拒绝', value: 2 }, { label: '已取消', value: 3 }
];
const statusTagMap: Record<number, { label: string; type: 'default' | 'warning' | 'info' | 'success' | 'error' }> = {
  0: { label: '待审核', type: 'warning' }, 1: { label: '已通过', type: 'success' },
  2: { label: '已拒绝', type: 'error' }, 3: { label: '已取消', type: 'default' }
};

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '宠物ID', key: 'petId', width: 70 },
  { title: '申请人', key: 'realName', width: 80 },
  { title: '联系电话', key: 'phone', width: 120 },
  { title: '居住条件', key: 'livingCondition', width: 120 },
  { title: '养宠经验', key: 'experience', width: 120 },
  { title: '申请理由', key: 'reason', width: 150, ellipsis: { tooltip: true } },
  { title: '状态', key: 'status', width: 80, render: (row: any) => { const s = statusTagMap[row.status]; return s ? h(NTag, { type: s.type, size: 'small' }, () => s.label) : ''; }},
  { title: '审核备注', key: 'reviewNote', width: 120, ellipsis: { tooltip: true } },
  { title: '操作', key: 'actions', width: 170, render: (row: any) => h(NSpace, null, () => [
    row.status === 0 ? h(NButton, { size: 'small', type: 'success', onClick: () => openReview(row.id) }, () => '审核') : null,
    h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确认删除？' })
  ])}
];

function openReview(id: number) { reviewForm.id = id; reviewForm.status = 1; reviewForm.reviewNote = ''; showReview.value = true; }

async function loadData() {
  loading.value = true;
  try {
    const { data: res, error } = await fetchAdoptionList(query);
    if (!error && res) { data.value = res.records || []; total.value = res.total || 0; }
  } finally { loading.value = false; }
}

async function handleReview() {
  const { error } = await reviewAdoption(reviewForm.id, reviewForm);
  if (!error) { message.success('审核完成'); showReview.value = false; loadData(); }
}

async function handleDelete(id: number) {
  const { error } = await deleteAdoption(id);
  if (!error) { message.success('删除成功'); loadData(); }
}

function handlePageChange(page: number) { query.page = page; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px">
      <NSelect v-model:value="query.status" :options="statusOptions" placeholder="状态筛选" style="width:130px" @update:value="loadData" />
      <NButton type="primary" @click="loadData">查询</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" :bordered="false" striped />
    <NModal v-model:show="showReview" title="审核领养申请" preset="card" style="width:450px">
      <NForm :model="reviewForm" label-placement="left" label-width="80px">
        <NFormItem label="审核结果"><NSelect v-model:value="reviewForm.status" :options="[{label:'通过',value:1},{label:'拒绝',value:2}]" /></NFormItem>
        <NFormItem label="审核备注"><NInput v-model:value="reviewForm.reviewNote" type="textarea" :rows="3" placeholder="填写审核备注" /></NFormItem>
      </NForm>
      <template #footer><NSpace justify="end"><NButton @click="showReview = false">取消</NButton><NButton type="primary" @click="handleReview">确认</NButton></NSpace></template>
    </NModal>
  </div>
</template>
