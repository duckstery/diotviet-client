<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-12 col-md-2 tw-pr-3">
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.order') }}</div>
      <!-- Chart type -->
      <FilterPanel :title="$t('field.chart_type')">
        <RadioList v-model="chartType" :options="$constant.chartTypes()"/>
      </FilterPanel>
      <IncomeFilter v-model="filter"/>
    </div>
    <div class="col-12 col-md-10">
      <!-- Data table -->
      <Chart id="chart" :type="chartType" :data="chartData" :options="chartOptions"/>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import Chart from "components/General/Other/Chart.vue";
import IncomeFilter from "components/Manage/Report/Income/IncomeFilter.vue";
import FilterPanel from "components/Manage/FilterPanel.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import RadioList from "components/General/Other/RadioList.vue";

import {computed, onMounted, ref, watch} from "vue";
import {axios, constant, error} from "src/boot";
import {Chart as ChartJS, BarController} from 'chart.js'

ChartJS.register(BarController);

export default {
  name: 'IncomePage',

  components: {RadioList, RadioFilter, FilterPanel, IncomeFilter, Chart, Page},

  setup() {
    // Chart type
    const chartType = ref('bar')
    // Chart options
    const chartOptions = computed(() => (chartType.value === 'bar' ? {
      plugins: {
        tooltip: {
          callbacks: {
            footer: (tooltipItem) => {
              console.warn(tooltipItem)
              // let total = tooltipItems.reduce((a, e) => a + parseInt(e.yLabel), 0);
              return 'Total: ';
            }
          }
        }
      }
    } : {}))
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

    // Filter
    const filter = ref({
      from: null,
      to: null
    })
    // Fetch when filter changed
    watch(filter, () => {
      console.warn("fetch")
      fetch()
    }, {deep: true})

    return {
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
