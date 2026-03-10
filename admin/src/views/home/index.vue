<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue';
import { useAppStore } from '@/store/modules/app';
import { fetchDashboardOverview, fetchPetStatusStats, fetchPetSpeciesStats, fetchStationCapacity } from '@/service/api';
import { useAuthStore } from '@/store/modules/auth';
import * as echarts from 'echarts';

const appStore = useAppStore();
const authStore = useAuthStore();
const gap = computed(() => (appStore.isMobile ? 0 : 16));

const overview = ref<any>({});
const petStatus = ref<any[]>([]);
const petSpecies = ref<any[]>([]);
const stationCap = ref<any[]>([]);
const loading = ref(true);

const userName = computed(() => authStore.userInfo.userName || '管理员');

// Chart refs
const pieChartRef = ref<HTMLElement | null>(null);
const barChartRef = ref<HTMLElement | null>(null);
const stationChartRef = ref<HTMLElement | null>(null);

onMounted(async () => {
  try {
    const { data: ov, error: e1 } = await fetchDashboardOverview();
    if (!e1 && ov) overview.value = ov;
  } catch {}
  try {
    const { data: ps, error: e2 } = await fetchPetStatusStats();
    if (!e2 && ps) petStatus.value = ps;
  } catch {}
  try {
    const { data: sp, error: e3 } = await fetchPetSpeciesStats();
    if (!e3 && sp) petSpecies.value = sp;
  } catch {}
  try {
    const { data: sc, error: e4 } = await fetchStationCapacity();
    if (!e4 && sc) stationCap.value = sc;
  } catch {}
  loading.value = false;

  await nextTick();
  initPieChart();
  initBarChart();
  initStationChart();
});

const statusColors = ['#f56c6c', '#e6a23c', '#2d8c5a', '#409eff', '#909399'];
const statusNames = ['待救助', '救助中', '待领养', '已领养', '已归还'];

function initPieChart() {
  if (!pieChartRef.value) return;
  const chart = echarts.init(pieChartRef.value);
  const data = petSpecies.value.length
    ? petSpecies.value.map((s: any) => ({ name: s.species, value: s.count }))
    : [{ name: '猫', value: 15 }, { name: '狗', value: 12 }, { name: '兔', value: 3 }];
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}只 ({d}%)' },
    legend: { bottom: 0, textStyle: { fontSize: 12 } },
    color: ['#2d8c5a', '#409eff', '#e6a23c', '#f56c6c', '#67c23a', '#909399'],
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{c}只' },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

function initBarChart() {
  if (!barChartRef.value) return;
  const chart = echarts.init(barChartRef.value);
  const data = petStatus.value.length
    ? petStatus.value
    : statusNames.map((n, i) => ({ statusName: n, count: [3, 5, 12, 8, 2][i] }));
  chart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}: {c}只' },
    grid: { left: 60, right: 20, top: 20, bottom: 40 },
    xAxis: {
      type: 'category',
      data: data.map((d: any) => d.statusName || d.name),
      axisLabel: { fontSize: 12 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      data: data.map((d: any, i: number) => ({
        value: d.count || d.value || 0,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: statusColors[i] || '#2d8c5a' },
            { offset: 1, color: (statusColors[i] || '#2d8c5a') + '66' }
          ]),
          borderRadius: [6, 6, 0, 0]
        }
      })),
      barWidth: 36
    }]
  });
  window.addEventListener('resize', () => chart.resize());
}

function initStationChart() {
  if (!stationChartRef.value) return;
  const chart = echarts.init(stationChartRef.value);
  const data = stationCap.value.length
    ? stationCap.value
    : [
        { name: '阳光救助中心', capacity: 100, currentCount: 45 },
        { name: '爱心宠物之家', capacity: 60, currentCount: 32 },
        { name: '绿城动物保护站', capacity: 80, currentCount: 28 },
        { name: '新郑流浪宠物驿站', capacity: 40, currentCount: 15 }
      ];
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0, data: ['在站数量', '总容量'] },
    grid: { left: 120, right: 30, top: 20, bottom: 40 },
    yAxis: {
      type: 'category',
      data: data.map((d: any) => d.name),
      axisLabel: { fontSize: 11 }
    },
    xAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '总容量',
        type: 'bar',
        data: data.map((d: any) => d.capacity),
        barWidth: 16,
        itemStyle: { color: '#e0e0e0', borderRadius: [0, 4, 4, 0] },
        z: 1
      },
      {
        name: '在站数量',
        type: 'bar',
        data: data.map((d: any) => ({
          value: d.currentCount,
          itemStyle: {
            color: (d.currentCount / d.capacity) > 0.8 ? '#f56c6c' : '#2d8c5a',
            borderRadius: [0, 4, 4, 0]
          }
        })),
        barWidth: 16,
        barGap: '-100%',
        z: 2
      }
    ]
  });
  window.addEventListener('resize', () => chart.resize());
}

const statCards = computed(() => [
  { label: '宠物总数', value: overview.value.totalPets || 0, icon: 'mdi:paw', color: '#2d8c5a', bg: '#e8f5ee' },
  { label: '待领养', value: overview.value.waitingAdoption || 0, icon: 'mdi:heart-outline', color: '#e6a23c', bg: '#fdf6ec' },
  { label: '已领养', value: overview.value.adopted || 0, icon: 'mdi:home-heart', color: '#409eff', bg: '#ecf5ff' },
  { label: '救助中', value: overview.value.rescuing || 0, icon: 'mdi:ambulance', color: '#f56c6c', bg: '#fef0f0' },
  { label: '注册用户', value: overview.value.totalUsers || 0, icon: 'mdi:account-group', color: '#909399', bg: '#f4f4f5' },
  { label: '救助站', value: overview.value.totalStations || 0, icon: 'mdi:hospital-building', color: '#67c23a', bg: '#f0f9eb' }
]);

const recentNews = [
  { time: '2026-03-09', content: '系统新增了30只待领养宠物信息' },
  { time: '2026-03-08', content: '张小明成功领养了拉布拉多「旺财」' },
  { time: '2026-03-07', content: '阳光救助中心本月已救助15只流浪动物' },
  { time: '2026-03-06', content: '2026年春季领养日活动报名火热进行中' },
  { time: '2026-03-05', content: '绿城动物保护站完成了本月TNR计划' },
  { time: '2026-03-04', content: '爱心宠物之家接收了8只新救助猫咪' }
];
</script>

<template>
  <NSpin :show="loading">
    <NSpace vertical :size="16">
      <!-- 欢迎横幅 -->
      <NCard :bordered="false" class="card-wrapper">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-22px font-bold" style="color: #2d8c5a">
              <div class="i-mdi:paw inline-block text-24px vertical-mid mr-6px"></div>
              欢迎回来，{{ userName }}！
            </h2>
            <p class="text-14px mt-8px" style="color: #888">让我们一起守护每一个流浪的小生命</p>
          </div>
          <div class="flex gap-4px">
            <div class="i-mdi:cat text-48px" style="color:#e6a23c"></div>
            <div class="i-mdi:dog text-48px" style="color:#67c23a"></div>
            <div class="i-mdi:rabbit text-48px" style="color:#909399"></div>
          </div>
        </div>
      </NCard>

      <!-- 统计卡片 -->
      <NGrid :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
        <NGi v-for="(item, idx) in statCards" :key="idx" span="12 s:8 m:4">
          <NCard :bordered="false" class="card-wrapper" hoverable>
            <div class="flex items-center gap-16px">
              <div class="text-36px w-52px h-52px flex-center rd-12px" :style="{ background: item.bg }">
                <div :class="'i-' + item.icon" class="text-28px" :style="{ color: item.color }"></div>
              </div>
              <div>
                <div class="text-26px font-bold" :style="{ color: item.color }">{{ item.value }}</div>
                <div class="text-13px" style="color: #999">{{ item.label }}</div>
              </div>
            </div>
          </NCard>
        </NGi>
      </NGrid>

      <!-- 图表区域 第一行：饼图 + 柱状图 -->
      <NGrid :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
        <NGi span="24 s:24 m:12">
          <NCard :bordered="false" class="card-wrapper" title="宠物种类分布">
            <div ref="pieChartRef" class="chart-box"></div>
          </NCard>
        </NGi>
        <NGi span="24 s:24 m:12">
          <NCard :bordered="false" class="card-wrapper" title="宠物状态统计">
            <div ref="barChartRef" class="chart-box"></div>
          </NCard>
        </NGi>
      </NGrid>

      <!-- 图表区域 第二行：救助站容量 + 最近动态 -->
      <NGrid :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
        <NGi span="24 s:24 m:14">
          <NCard :bordered="false" class="card-wrapper" title="救助站容量">
            <div ref="stationChartRef" class="chart-box"></div>
          </NCard>
        </NGi>
        <NGi span="24 s:24 m:10">
          <NCard :bordered="false" class="card-wrapper" title="最近动态">
            <NTimeline>
              <NTimelineItem
                v-for="(item, idx) in recentNews"
                :key="idx"
                :title="item.content"
                :time="item.time"
                :type="idx === 0 ? 'success' : 'default'"
              />
            </NTimeline>
          </NCard>
        </NGi>
      </NGrid>
    </NSpace>
  </NSpin>
</template>

<style scoped>
.chart-box {
  width: 100%;
  height: 320px;
}
</style>
