<template>
  <div class="form-page">
    <h2>🐾 登录</h2>
    <n-form :model="form" label-placement="left">
      <n-form-item label="用户名"><n-input v-model:value="form.username" placeholder="请输入用户名" /></n-form-item>
      <n-form-item label="密码"><n-input v-model:value="form.password" type="password" placeholder="请输入密码" show-password-on="click" /></n-form-item>
      <n-button type="primary" block :loading="loading" @click="handleLogin" style="margin-top:8px">登 录</n-button>
    </n-form>
    <p style="text-align:center;margin-top:16px;color:#888">
      没有账号？<router-link to="/register" style="color:#2d8c5a">立即注册</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const msg = useMessage()
const userStore = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  if (!form.username || !form.password) { msg.warning('请填写完整'); return }
  loading.value = true
  try {
    await userStore.login(form)
    msg.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: any) { msg.error(e.message || '登录失败') }
  finally { loading.value = false }
}
</script>
