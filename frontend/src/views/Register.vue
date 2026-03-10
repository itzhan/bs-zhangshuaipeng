<template>
  <div class="form-page">
    <h2>🐾 注册</h2>
    <n-form :model="form" label-placement="left">
      <n-form-item label="用户名"><n-input v-model:value="form.username" placeholder="请输入用户名" /></n-form-item>
      <n-form-item label="密码"><n-input v-model:value="form.password" type="password" placeholder="请输入密码" /></n-form-item>
      <n-form-item label="确认密码"><n-input v-model:value="form.confirmPwd" type="password" placeholder="确认密码" /></n-form-item>
      <n-form-item label="昵称"><n-input v-model:value="form.nickname" placeholder="昵称" /></n-form-item>
      <n-form-item label="手机号"><n-input v-model:value="form.phone" placeholder="手机号" /></n-form-item>
      <n-button type="primary" block :loading="loading" @click="handleReg" style="margin-top:8px">注 册</n-button>
    </n-form>
    <p style="text-align:center;margin-top:16px;color:#888">
      已有账号？<router-link to="/login" style="color:#2d8c5a">去登录</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/store/user'

const router = useRouter()
const msg = useMessage()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPwd: '', nickname: '', phone: '' })

async function handleReg() {
  if (!form.username || !form.password) { msg.warning('请填写完整'); return }
  if (form.password !== form.confirmPwd) { msg.warning('密码不一致'); return }
  loading.value = true
  try { await userStore.register(form); msg.success('注册成功，请登录'); router.push('/login') }
  catch (e: any) { msg.error(e.message || '注册失败') }
  finally { loading.value = false }
}
</script>
