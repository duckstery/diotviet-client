<template>
  <VueChart v-bind="$attrs"
            ref="chartRef"
            :options="chartOptions" :data="chartData"
            :type="type" :plugins="plugins" :updateMode="updateMode"
  />
</template>

<script>
import {Chart as VueChart} from "vue-chartjs";
import {Chart as ChartJS, Colors, Title, Tooltip, Legend, CategoryScale, LinearScale} from "chart.js";
import {BarController, LineController} from "chart.js";
import {BarElement, LineElement, PointElement} from "chart.js";

import {computed, toRef} from "vue";
import {templateRef} from "@vueuse/core";
import {useChartDefaultOptions} from "src/composables/useChartDefaultOptions";
import {useChartDefaultData} from "src/composables/useChartDefaultData";

ChartJS.register(
    BarController, LineController,
    BarElement, LineElement, PointElement,
    Colors, Title, Tooltip, Legend, CategoryScale, LinearScale
);

export default {
  name: "Chart",

  components: {VueChart},

  props: {
    type: {
      type: String,
      default: 'bar'
    },
    data: Object,
    options: Object,
    plugins: Array,
    updateMode: String,
  },

  setup(props, {expose}) {
    // Get ChartJs object
    const chartRef = templateRef('chartRef')
    // Export chart.js instance
    expose({instance: computed(() => chartRef.value.chart)})

    return {
      chartOptions: useChartDefaultOptions(toRef(props, 'options')),
      chartData: useChartDefaultData(toRef(props, 'data'))
    }
  }
}
</script>
