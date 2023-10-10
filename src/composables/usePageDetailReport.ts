import {ref, onMounted, watch, ComputedRef, Ref} from 'vue'
import {axios, error} from "src/boot";
import {ChartData, ChartOptions} from "chart.js";
import {ChartDataset} from "chart.js/dist/types";
import {usePageChart, UsePageChartResources} from "src/composables/usePageChart";

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
  [key: string]: string,
}

/**
 * Resource type
 */
export type UsePageDetailReportResources = {
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
 * @param initFilter
 * @param initOption
 */
export function usePageDetailReport(api: string, initFilter: ReportFilter, initOption: ChartOptions = {}): UsePageDetailReportResources {
  // UsePageChart
  const usePageChartResources: UsePageChartResources = usePageChart(initOption)

  // Fetch
  const fetch = () => axios.get(api, {params: filter.value})
    .then(res => usePageChartResources.datasets.value = res.data.payload)
    .catch(error.any)
  // Fetch on mounted
  onMounted(fetch)

  // Filter
  const filter: Ref<ReportFilter> = ref(initFilter)
  // Fetch when filter changed
  watch(filter, fetch, {deep: true})

  return {
    // Use page chart resources
    ...usePageChartResources,
    // Filter
    filter: filter
  }
}
