<template>
  <Page :breadcrumbs="breadcrumbs" :split="[2, 10]">
    <template #left>
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.income') }}</div>
      <ReportFilter v-model="filter" v-model:type="chartType"/>
    </template>
    <template #right>
      <!-- Chart -->
      <Chart id="chart" :type="chartType" :data="chartData" :options="chartOptions"/>
      <!-- Hint -->
      <ReportHint :datasets="datasets" class="tw-mt-2"/>
    </template>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import Chart from "components/General/Other/Chart.vue";
import ReportFilter from "components/Manage/Report/ReportFilter.vue";
import FilterPanel from "components/Manage/FilterPanel.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import RadioList from "components/General/Other/RadioList.vue";
import ReportHint from "components/Manage/Report/ReportHint.vue";

import {usePageDetailReport} from "src/composables/usePageDetailReport";
import {dayjs} from "boot/dayjs";
import {Chart as ChartJS, BarController} from 'chart.js'

ChartJS.register(BarController);

export default {
  name: 'IncomePage',

  components: {ReportHint, RadioList, RadioFilter, FilterPanel, ReportFilter, Chart, Page},

  setup() {
    // Get now
    const now = dayjs()

    return {
      ...usePageDetailReport('/transaction/report', {
        from: now.startOf('month').format('YYYY-MM-DD'),
        to: now.format('YYYY-MM-DD'),
        displayMode: 'date',
      }),
    }
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.report'), to: '/report', icon: 'fa-chart-line'},
        {label: this.$t('field.income'), to: '/report/income', icon: 'fa-money-bill-trend-up'},
      ]
    },
  },
}
</script>
