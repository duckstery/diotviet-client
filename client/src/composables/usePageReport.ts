import {ref, onMounted, computed, watch, ComputedRef, Ref} from 'vue'
import {axios, constant, error} from "src/boot";
import {dayjs} from "boot/dayjs";
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
 * Report filter
 */
export type ReportFilter = {
  from: string,
  to: string,
  displayMode: 'date' | 'month',
}

/**
 * Resource type
 */
export type UsePageReportResources = {
  datasets: Ref<ExtendedChartDataset[]>,
  chartType: Ref<ChartType>,
  chartOptions: ComputedRef<ChartOptions>,
  chartData: ComputedRef<ChartData>,
  filter: Ref<ReportFilter>
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup Report page
 *
 * @param api
 * @param initType
 * @param initOption
 */
export function usePageReport(api: string, initOption: ChartOptions = {}): UsePageReportResources {
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
  // Fetch
  const fetch = () => axios.get(api, {params: filter.value})
    .then(res => datasets.value = res.data.payload)
    .catch(error.any)
  // Fetch on mounted
  onMounted(fetch)

  // Get now
  const now = dayjs()
  // Filter
  const filter: Ref<ReportFilter> = ref({
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
}
