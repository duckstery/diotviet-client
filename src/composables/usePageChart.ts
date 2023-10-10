import {ref, computed, ComputedRef, Ref} from 'vue'
import {constant} from "src/boot";
import {ChartData, ChartOptions} from "chart.js";
import {ChartDataset} from "chart.js/dist/types";
import {useI18n} from "vue-i18n";

// *************************************************
// Typed
// *************************************************

/**
 * Chart type
 */
export type ChartType = 'bar' | 'line'

/**
 * Extended chart dataset
 */
export type ExtendedChartDataset = ChartDataset & {
  key: string,
  hint: string,
  color: "red" | "orange" | "yellow" | "green" | "blue" | "purple" | "grey",
}

/**
 * Resource type
 */
export type UsePageChartResources = {
  datasets: Ref<ExtendedChartDataset[]>,
  chartType: Ref<ChartType>,
  chartOptions: ComputedRef<ChartOptions>,
  chartData: ComputedRef<ChartData>,
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup Page Chart
 *
 * @param initOption
 */
export function usePageChart(initOption: ChartOptions = {}): UsePageChartResources {
  // i18n
  const $t = useI18n().t

  // Chart type
  const chartType: Ref<ChartType> = ref('bar')
  // Chart options
  const chartOptions: ComputedRef<ChartOptions> = computed(() => initOption)
  // Chart data
  const chartData: ComputedRef<ChartData> = computed(() => ({
    datasets: datasets.value.map((dataset: ExtendedChartDataset) => ({
      ...dataset,
      label: $t(`field.${dataset.key}`),
      borderColor: constant.chartColors(dataset.color, 1),
      backgroundColor: constant.chartColors(dataset.color, 0.5)
    }))
  }))

  // Report datasets
  const datasets: Ref<ExtendedChartDataset[]> = ref([])

  return {
    // Datasets
    datasets: datasets,
    // ChartJs properties
    chartType: chartType,
    chartOptions: chartOptions,
    chartData: chartData,
  }
}
