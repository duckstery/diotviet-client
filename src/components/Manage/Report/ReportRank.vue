<template>
  <div class="row">
    <div class="col-12 col-md-6 tw-pl-10">
      <!-- Hint -->
      <ReportHint :datasets="datasets" class="tw-mt-2"/>
    </div>
    <div v-for="(chart, index) in charts" :key="index" class="col-12 col-md-6">
      <div class="tw-text-center tw-text-xl">
        {{ $util.isUnset(chart.datasets[0]) || $t(`field.${chart.datasets[0].key}`) }}
      </div>
      <Chart :type="chart.chartType" :data="chart.chartData" :options="chart.chartOptions"/>
    </div>
  </div>
</template>

<script>
import Chart from "components/General/Other/Chart.vue";
import ReportHint from "components/Manage/Report/ReportHint.vue";

export default {
  name: "ReportRank",
  components: {ReportHint, Chart},

  props: {
    charts: Array,
    datasets: Array,
  },
}
</script>
