<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-12 col-md-2 tw-pr-3">
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.order') }}</div>
      <ReportFilter v-model="filter" v-model:type="chartType"/>
    </div>
    <div class="col-12 col-md-10">
      <!-- Chart -->
      <Chart id="chart" :type="chartType" :data="chartData" :options="chartOptions"/>
      <!-- Hint -->
      <ReportHint :datasets="datasets" class="tw-mt-2"/>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import Chart from "components/General/Other/Chart.vue";
import ReportFilter from "components/Manage/Report/ReportFilter.vue";
import FilterPanel from "components/Manage/FilterPanel.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import RadioList from "components/General/Other/RadioList.vue";

import {computed, onMounted, ref, watch} from "vue";
import {axios, constant, error} from "src/boot";
import {Chart as ChartJS, BarController} from 'chart.js'
import {dayjs} from 'src/boot'
import ReportHint from "components/Manage/Report/ReportHint.vue";

ChartJS.register(BarController);

export default {
  name: 'IncomePage',

  components: {ReportHint, RadioList, RadioFilter, FilterPanel, ReportFilter, Chart, Page},

  setup() {
    // Chart type
    const chartType = ref('bar')
    // Chart options
    const chartOptions = computed(() => ({}))
    // Chart data
    const chartData = computed(() => ({
      datasets: datasets.value.map(dataset => ({
        ...dataset,
        borderColor: constant.chartColors(dataset.color),
        backgroundColor: constant.chartColors(dataset.color, 0.5)
      }))
    }))

    // Report datasets
    const datasets = ref([])
    // Fetch
    const fetch = () => axios.get("/transaction/report", {params: filter.value})
        .then(res => datasets.value = res.data.payload)
        .catch(error.any)
    // Fetch on mounted
    onMounted(fetch)

    // Get now
    const now = dayjs()
    // Filter
    const filter = ref({
      from: now.startOf('month').format('YYYY-MM-DD'),
      to: now.format('YYYY-MM-DD'),
      displayMode: 'date',
    })
    // Fetch when filter changed
    watch(filter, () => fetch(), {deep: true})

    return {
      // Datasets
      datasets: datasets,
      // ChartJs properties
      chartType: chartType, chartOptions: chartOptions, chartData: chartData,
      // Filter
      filter: filter
    }
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.transaction'), to: '/transaction', icon: 'fa-arrow-right-arrow-left'},
        {label: this.$t('field.order'), to: '/transaction/order', icon: 'fa-inbox'},
      ]
    },
  },
}
</script>
