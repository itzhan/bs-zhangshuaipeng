<template>
  <div class="container" style="padding-top:24px">
    <h2 style="margin-bottom:20px">👤 个人中心</h2>
    <n-tabs type="line">
      <n-tab-pane name="info" tab="个人信息">
        <n-card v-if="userStore.userInfo" style="max-width:500px">
          <n-descriptions :column="1" label-placement="left" bordered>
            <n-descriptions-item label="用户名">{{ userStore.userInfo.username }}</n-descriptions-item>
            <n-descriptions-item label="昵称">{{ userStore.userInfo.nickname }}</n-descriptions-item>
            <n-descriptions-item label="手机">{{ userStore.userInfo.phone }}</n-descriptions-item>
            <n-descriptions-item label="邮箱">{{ userStore.userInfo.email || '未绑定' }}</n-descriptions-item>
            <n-descriptions-item label="角色">{{ userStore.userInfo.role }}</n-descriptions-item>
          </n-descriptions>
        </n-card>
      </n-tab-pane>
      <n-tab-pane name="adoptions" tab="我的领养">
        <n-spin :show="adoptLoading">
          <n-list bordered>
            <n-list-item v-for="a in adoptions" :key="a.id">
              <n-thing :title="'宠物#' + a.petId" :description="a.reason">
                <template #header-extra>
                  <n-tag :type="[,'success','error','default'][a.status] || 'warning'" size="small">{{ ['待审核','已通过','已拒绝','已取消'][a.status] }}</n-tag>
                </template>
              </n-thing>
            </n-list-item>
          </n-list>
          <n-empty v-if="!adoptLoading && !adoptions.length" description="暂无领养记录" />
        </n-spin>
      </n-tab-pane>
      <n-tab-pane name="favorites" tab="我的收藏">
        <n-spin :show="favLoading">
          <div class="card-grid">
            <div class="pet-card" v-for="f in favorites" :key="f.id" @click="$router.push(`/pets/${f.petId}`)">
              <div class="info"><h3>宠物 #{{ f.petId }}</h3><p style="color:#888">点击查看详情</p></div>
            </div>
          </div>
          <n-empty v-if="!favLoading && !favorites.length" description="暂无收藏" />
        </n-spin>
      </n-tab-pane>
      <n-tab-pane name="notifications" tab="消息通知">
        <n-spin :show="notiLoading">
          <div style="margin-bottom:12px"><n-button size="small" @click="markAllRead">全部已读</n-button></div>
          <n-list bordered>
            <n-list-item v-for="n in notifications" :key="n.id">
              <n-thing :title="n.title" :description="n.content">
                <template #header-extra>
                  <n-tag :type="n.isRead ? 'default' : 'warning'" size="small">{{ n.isRead ? '已读' : '未读' }}</n-tag>
                </template>
              </n-thing>
            </n-list-item>
          </n-list>
          <n-empty v-if="!notiLoading && !notifications.length" description="暂无通知" />
        </n-spin>
      </n-tab-pane>
    </n-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { adoptionApi, favoriteApi, notificationApi } from '@/api'
import { useUserStore } from '@/store/user'

const msg = useMessage(); const userStore = useUserStore()
const adoptLoading = ref(false); const adoptions = ref<any[]>([])
const favLoading = ref(false); const favorites = ref<any[]>([])
const notiLoading = ref(false); const notifications = ref<any[]>([])

onMounted(async () => {
  userStore.fetchUserInfo()
  adoptLoading.value = true; try { const r: any = await adoptionApi.myList({ page: 1, size: 50 }); adoptions.value = r?.records || [] } catch {} finally { adoptLoading.value = false }
  favLoading.value = true; try { const r: any = await favoriteApi.list({ page: 1, size: 50 }); favorites.value = r?.records || [] } catch {} finally { favLoading.value = false }
  notiLoading.value = true; try { const r: any = await notificationApi.list({ page: 1, size: 50 }); notifications.value = r?.records || [] } catch {} finally { notiLoading.value = false }
})

async function markAllRead() { try { await notificationApi.markAllRead(); msg.success('全部已读');
  const r: any = await notificationApi.list({ page: 1, size: 50 }); notifications.value = r?.records || [] } catch {} }
</script>
