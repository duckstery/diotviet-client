import {computed, Ref, ComputedRef} from "vue";
import {useQuasar} from "quasar";
import {ChartOptions, Color, Chart} from "chart.js";
// @ts-ignore
import _ from "lodash";

/**
 * Setup ChartJs default options
 *
 * @param optionsRef
 */
export function useChartDefaultOptions(optionsRef: Ref<ChartOptions>): ComputedRef<ChartOptions> {
  // Get Quasar Ref
  const $q = useQuasar()
  // Scheme related options
  const schemeOptions: ComputedRef<ChartOptions> = computed(() => {
    // Color
    const color: Color = $q.dark.isActive ? 'rgba(255, 255, 255, 0.1)' : <Color>Chart.defaults.borderColor

    return {
      scales: {
        y: {
          grid: {
            color: color
          },
        },
        x: {
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
      intersect: true,
    },
    scales: {
      y: {
        stacked: true,
        title: {
          display: true,
          text: 'VND',
        },
        min: 0,
        suggestedMin: 0,
        suggestedMax: 100000,
        ticks: {
          // forces step size to be 50 units
          stepSize: 50000
        }
      },
      x: {
        stacked: true
      }
    }
  }

  // ChartJs complete options
  return computed(() => _.defaultsDeep({}, optionsRef.value, schemeOptions.value, defaultOptions))
}
