<template>
  <Skeleton v-model="isReady" height="150px" width="300px">
    <VueChart v-bind="$attrs"
              ref="chartRef"
              :options="chartOptions" :data="chartData"
              :type="type" :plugins="plugins" :updateMode="updateMode"
    />
  </Skeleton>
</template>

<script>
import {Chart as VueChart} from "vue-chartjs";
import {Chart as ChartJS, Colors, Title, Tooltip, Legend, CategoryScale, LinearScale} from "chart.js";
import {BarController, LineController} from "chart.js";
import {BarElement, LineElement, PointElement} from "chart.js";

import {computed, nextTick, ref, toRef, watch} from "vue";
import {templateRef} from "@vueuse/core";
import {useChartDefaultOptions} from "src/composables/useChartDefaultOptions";
import {useChartDefaultData} from "src/composables/useChartDefaultData";
import Skeleton from "components/General/Other/Skeleton.vue";

ChartJS.register(
    BarController, LineController,
    BarElement, LineElement, PointElement,
    Colors, Title, Tooltip, Legend, CategoryScale, LinearScale
);

export default {
  name: "Chart",

  components: {Skeleton, VueChart},

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
    // Is chart ready
    const isReady = ref(true)
    // Get ChartJs object
    const chartRef = templateRef('chartRef')
    // Export ChartJs instance
    expose({instance: computed(() => chartRef.value.chart)})
    // Force reload ChartJs if type is changed
    watch(toRef(props, 'type'), () => {
      isReady.value = false
      nextTick(() => isReady.value = true)
    })

    return {
      isReady: isReady,
      chartOptions: useChartDefaultOptions(toRef(props, 'type'), toRef(props, 'options')),
      chartData: useChartDefaultData(toRef(props, 'data'))
    }
  }
}
</script>
