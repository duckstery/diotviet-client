<template>
  <Page :breadcrumbs="breadcrumbs" :split="[2, 10]">
    <template #left>
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t(`field.${target}`) }}</div>
      <RankFilter v-model="filter" v-model:target="target"/>
    </template>
    <template #right>
      <ReportRank :charts="charts" :datasets="datasets"/>
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
import RankFilter from "components/Manage/Report/RankFilter.vue";
import ReportHint from "components/Manage/Report/ReportHint.vue";
import ReportRank from "components/Manage/Report/ReportRank.vue";

import {dayjs} from "boot/dayjs";
import {usePageRankReport} from "src/composables/usePageRankReport";
import {computed, ref} from "vue";
import {Chart as ChartJS, BarController} from 'chart.js'

ChartJS.register(BarController);

export default {
  name: 'RankPage',

  components: {ReportRank, RankFilter, ReportHint, RadioList, RadioFilter, FilterPanel, ReportFilter, Chart, Page},

  setup() {
    // Report target
    const target = ref('product')
    // Dayjs
    const now = dayjs()

    return {
      target: target,
      // Use page rank resources
      ...usePageRankReport(computed(() => `/${target.value}/report`), {
        from: now.startOf('month').format('YYYY-MM-DD'),
        to: now.format('YYYY-MM-DD'),
        sort: -1,
        top: 10,
      }),
    }
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.report'), to: '/report', icon: 'fa-chart-line'},
        {label: this.$t('field.rank'), to: '/report/rank', icon: 'fa-ranking-star'},
      ]
    },
  },
}
</script>
