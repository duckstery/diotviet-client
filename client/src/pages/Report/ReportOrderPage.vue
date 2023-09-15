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

import {Chart as ChartJS, BarController} from 'chart.js'
import {dayjs} from 'src/boot'
import ReportHint from "components/Manage/Report/ReportHint.vue";
import {usePageDetailReport} from "src/composables/usePageDetailReport";
import {useI18n} from "vue-i18n";

ChartJS.register(BarController);

export default {
  name: 'ReportOrderPage',

  components: {ReportHint, RadioList, RadioFilter, FilterPanel, ReportFilter, Chart, Page},

  setup() {
    // Get now
    const now = dayjs()
    // Resources
    const resources = usePageDetailReport(
        '/order/report',
        {
          from: now.startOf('month').format('YYYY-MM-DD'),
          to: now.format('YYYY-MM-DD'),
          displayMode: 'date',
        },
        {
          scales: {
            y: {
              title: {
                display: true,
                text: useI18n().t('field.order'),
              },
              suggestedMax: 50,
              ticks: {
                // forces step size to be 50 units
                stepSize: 10
              }
            },
          }
        })

    return {...resources}
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.report'), to: '/report', icon: 'fa-chart-line'},
        {label: this.$t('field.income'), to: '/report/income', icon: 'fa-square-poll-vertical'},
      ]
    },
  },
}
</script>
