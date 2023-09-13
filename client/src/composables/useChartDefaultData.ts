import {computed, Ref, ComputedRef} from "vue";
import {ChartData} from "chart.js";
import {constant} from "src/boot";
// @ts-ignore
import _ from "lodash";
import {
  BarControllerDatasetOptions,
  FillerControllerDatasetOptions,
  LineControllerDatasetOptions
} from "chart.js/dist/types";

/**
 * Setup ChartJs default data options
 *
 * @param dataRef
 */
export function useChartDefaultData(dataRef: Ref<ChartData>): ComputedRef<ChartData> {
  // Default data options
  // @ts-ignore
  const defaultDatasetOptions: {
    type: String
  } & (BarControllerDatasetOptions | (LineControllerDatasetOptions & FillerControllerDatasetOptions)) = {
    type: '',
    label: '',
    fill: false,
    cubicInterpolationMode: 'monotone',
    tension: 0.4,
    borderWidth: 2,
    borderRadius: 5,
    borderSkipped: false
  }

  // ChartJs complete options
  return computed(() => {
    // Options
    const options: ChartData = _.defaultsDeep({}, dataRef.value)
    // Iterate through each option's datasets to apply default options
    options.datasets.map(dataset => _.defaultsDeep(dataset, defaultDatasetOptions))

    return options
  })
}
