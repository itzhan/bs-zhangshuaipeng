<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-notification-provider>
      <n-message-provider>
        <n-dialog-provider>
          <router-view />
        </n-dialog-provider>
      </n-message-provider>
    </n-notification-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import type { GlobalThemeOverrides } from 'naive-ui'

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#2d8c5a',
    primaryColorHover: '#3aaf6f',
    primaryColorPressed: '#1a6b3f',
    primaryColorSuppl: '#6dd099',
    borderRadius: '8px'
  }
}
</script>
