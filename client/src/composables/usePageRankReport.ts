import {ref, onMounted, watch, Ref, reactive} from 'vue'
import {axios, error} from "src/boot";
import {ChartOptions} from "chart.js";
import {ExtendedChartDataset, usePageChart, UsePageChartResources} from "src/composables/usePageChart";
// @ts-ignore
import _ from "lodash";
import {UnwrapNestedRefs} from "@vue/reactivity";

// *************************************************
// Typed
// *************************************************

/**
 * Markup table header for rank
 */
export type QMarkupTableLocalHeader = {
  name: string;
  field: string | ((row: any) => any);
  align?: "left" | "right" | "center";
  sortable?: boolean;
  format?: (val: any, row: any) => any;
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
export type UsePageRankReportResources = {
  datasets: Ref<ExtendedChartDataset[]>,
  charts: UnwrapNestedRefs<UsePageChartResources>[],
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
export function usePageRankReport(api: Ref<string> | string, initFilter: ReportFilter, initOption: ChartOptions = {}): UsePageRankReportResources {
  // Overall datasets
  const datasets = ref([])

  // Chart options to disable legend
  const chartOptions: ChartOptions = _.defaultsDeep({}, initOption, {
    plugins: {
      legend: {
        display: false,
      },
    },
    scales: {
      y: {
        title: {
          display: false
        }
      }
    }
  })
  // UsePageChart
  const charts: UnwrapNestedRefs<UsePageChartResources>[] = []
  // Total income
  charts.push(reactive(usePageChart(chartOptions)))
  // Ordered quantity
  charts.push(reactive(usePageChart(_.defaultsDeep({}, chartOptions, {
    scales: {
      y: {
        suggestedMax: 50,
        ticks: {
          stepSize: 10
        }
      },
    }
  }))))
  // Average income
  charts.push(reactive(usePageChart(_.defaultsDeep({}, chartOptions, {
    scales: {
      y: {
        suggestedMax: 100000,
        ticks: {
          stepSize: 10000
        }
      },
    }
  }))))

  // Fetch
  const fetch = () => axios.get(typeof api === 'string' ? api : api.value, {params: filter.value})
    .then(res => {
      datasets.value = res.data.payload
      charts.map((chart, i) => chart.datasets = [res.data.payload[i]])
    })
    .catch(error.any)
  // Fetch on mounted
  onMounted(fetch)
  // Re-fetch if api is a ComputedRef and api is changed
  if (typeof api !== 'string') {
    watch(api, fetch)
  }

  // Filter
  const filter: Ref<ReportFilter> = ref(initFilter)
  // Fetch when filter changed
  watch(filter, fetch, {deep: true})

  return {
    // Overall datasets
    datasets: datasets,
    // Use page chart resources
    charts: charts,
    // Filter
    filter: filter
  }
}
