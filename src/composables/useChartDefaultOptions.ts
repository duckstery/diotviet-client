import {computed, Ref, ComputedRef} from "vue";
import {useQuasar} from "quasar";
import {ChartOptions, Color, Chart} from "chart.js";
// @ts-ignore
import _ from "lodash";

/**
 * Setup ChartJs default options
 *
 * @param typeRef
 * @param optionsRef
 */
export function useChartDefaultOptions(typeRef: Ref<string>, optionsRef: Ref<ChartOptions>): ComputedRef<ChartOptions> {
  // Get Quasar Ref
  const $q = useQuasar()
  // Scheme related options
  const schemeOptions: ComputedRef<ChartOptions> = computed(() => {
    // Color
    const color: Color = $q.dark.isActive ? 'rgba(255, 255, 255, 0.1)' : <Color>Chart.defaults.borderColor

    return {
      scales: {
        y: {
          stacked: typeRef.value === 'bar',
          grid: {
            color: color
          },
        },
        x: {
          stacked: typeRef.value === 'bar',
          grid: {
            color: color
          }
        }
      }
    }
  })

  // Default options
  const defaultOptions: ChartOptions = {
    responsive: true,
    interaction: {
      intersect: false,
      mode: 'index',
    },
    scales: {
      y: {
        title: {
          display: true,
          text: 'VND',
        },
        min: 0,
        suggestedMin: 0,
        suggestedMax: 1000000,
        ticks: {
          // forces step size to be 50 units
          stepSize: 100000
        }
      },
    }
  }

  // ChartJs complete options
  return computed(() => _.defaultsDeep({}, optionsRef.value, schemeOptions.value, defaultOptions))
}
