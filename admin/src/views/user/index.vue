<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NDataTable, NInput, NSelect, NModal, NForm, NFormItem,
  useMessage, NPopconfirm, NGrid, NGridItem } from 'naive-ui';
import { fetchUserList, updateUser, updateUserStatus, deleteUser } from '@/service/api';

const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, keyword: '', role: null as string | null });
const showModal = ref(false);

const roleOptions = [
  { label: '全部', value: null }, { label: '管理员', value: 'ADMIN' },
  { label: '工作人员', value: 'STAFF' }, { label: '普通用户', value: 'USER' }
];
const roleTagMap: Record<string, { label: string; type: 'default' | 'warning' | 'info' | 'success' | 'error' }> = {
  ADMIN: { label: '管理员', type: 'error' }, STAFF: { label: '工作人员', type: 'warning' }, USER: { label: '普通用户', type: 'info' }
};

const form = reactive<any>({ id: null, nickname: '', phone: '', email: '', gender: 1, role: 'USER', status: 1, password: '' });

const columns = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '用户名', key: 'username', width: 100 },
  { title: '昵称', key: 'nickname', width: 100 },
  { title: '手机', key: 'phone', width: 120 },
  { title: '邮箱', key: 'email', width: 150 },
  { title: '角色', key: 'role', width: 90, render: (row: any) => { const r = roleTagMap[row.role]; return r ? h(NTag, { type: r.type, size: 'small' }, () => r.label) : row.role; }},
  { title: '状态', key: 'status', width: 80, render: (row: any) => h(NTag, { type: row.status === 1 ? 'success' : 'error', size: 'small' }, () => row.status === 1 ? '正常' : '禁用' )},
  { title: '操作', key: 'actions', width: 240, render: (row: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', type: 'primary', onClick: () => { Object.assign(form, { ...row, password: '' }); showModal.value = true; } }, () => '编辑'),
    h(NButton, { size: 'small', type: row.status === 1 ? 'warning' : 'success', onClick: () => handleToggleStatus(row) }, () => row.status === 1 ? '禁用' : '启用'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确认删除？' })
  ])}
];

async function loadData() {
  loading.value = true;
  try {
    const { data: res, error } = await fetchUserList(query);
    if (!error && res) { data.value = res.records || []; total.value = res.total || 0; }
  } finally { loading.value = false; }
}

async function handleToggleStatus(row: any) {
  const { error } = await updateUserStatus(row.id, row.status === 1 ? 0 : 1);
  if (!error) { message.success('操作成功'); loadData(); }
}

async function handleSubmit() {
  const { error } = await updateUser(form.id, form);
  if (!error) { message.success('更新成功'); showModal.value = false; loadData(); }
}

async function handleDelete(id: number) {
  const { error } = await deleteUser(id);
  if (!error) { message.success('删除成功'); loadData(); }
}

function handlePageChange(page: number) { query.page = page; loadData(); }
onMounted(() => loadData());
</script>

<template>
  <div class="p-16px">
    <div class="mb-16px flex items-center gap-12px flex-wrap">
      <NInput v-model:value="query.keyword" placeholder="搜索用户名/昵称/手机" clearable style="width:220px" @keyup.enter="loadData" />
      <NSelect v-model:value="query.role" :options="roleOptions" placeholder="角色" style="width:130px" @update:value="loadData" />
      <NButton type="primary" @click="loadData">查询</NButton>
    </div>
    <NDataTable :columns="columns" :data="data" :loading="loading" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" :bordered="false" striped />
    <NModal v-model:show="showModal" title="编辑用户" preset="card" style="width:600px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="昵称"><NInput v-model:value="form.nickname" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="手机"><NInput v-model:value="form.phone" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="邮箱"><NInput v-model:value="form.email" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="角色"><NSelect v-model:value="form.role" :options="roleOptions.filter(o=>o.value)" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="重置密码"><NInput v-model:value="form.password" placeholder="留空不修改" type="password" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #footer><NSpace justify="end"><NButton @click="showModal = false">取消</NButton><NButton type="primary" @click="handleSubmit">保存</NButton></NSpace></template>
    </NModal>
  </div>
</template>
