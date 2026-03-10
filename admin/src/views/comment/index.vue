<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NDataTable, NInput, useMessage, NPopconfirm } from 'naive-ui';
import { fetchCommentList, deleteComment } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, targetType: '', targetId: null as number | null });

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '用户ID', key: 'userId', width: 70 },
  { title: '目标类型', key: 'targetType', width: 80 },
  { title: '目标ID', key: 'targetId', width: 70 },
  { title: '内容', key: 'content', width: 300, ellipsis: { tooltip: true } },
  { title: '创建时间', key: 'createdAt', width: 160 },
  { title: '操作', key: 'actions', width: 100, render: (row: any) => h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, {
    trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确认删除此评论？'
  })}
];

async function loadData() { loading.value = true; try { const { data: res, error } = await fetchCommentList(query); if (!error && res) { data.value = res.records || []; total.value = res.total || 0; } } finally { loading.value = false; } }
async function handleDelete(id: number) { const { error } = await deleteComment(id); if (!error) { message.success('删除成功'); loadData(); } }
function handlePageChange(page: number) { query.page = page; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px">
      <NButton type="primary" @click="loadData">刷新</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" :bordered="false" striped />
  </div>
</template>
