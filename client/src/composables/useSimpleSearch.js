import {axios, error, util, } from "src/boot";
import {nextTick, reactive, watch} from "vue";
import _ from "lodash";

/**
 * Setup simple search and return options
 *
 * @param {string} api
 * @param {boolean} useWatch
 * @param {ComputedRef<*>|function} filter
 * @return {object}
 */
export function useSimpleSearch(api, useWatch = true, filter = null) {
  // Search resource
  const resource = reactive({
    query: '',
    data: [],
    original: [],
    filter: () => triggerFilter(),
    search: () => search(resource.query),
    // Event for Quasar's component
    onFilter: null
  })

  // Search logic
  const search = (value) => {
    if (value === null || value === '') {
      resource.data = []
      return
    }

    // Fetch data and apply filter
    axios.get(api, {params: {search: value}, loading: false})
      .then(res => {
        // Set original data
        resource.original = res.data.payload
        // Trigger filter
        triggerFilter()
      })
      .catch(error.any)
  }

  // Filter data
  const triggerFilter = () => {
    // Reset data
    resource.data = []

    nextTick(() => {
      // Deep clone original
      const clonedOriginal = _.cloneDeep(resource.original)

      if (util.isUnset(filter)) {
        resource.data = clonedOriginal
      } else if (typeof filter === 'function') {
        resource.data = filter(clonedOriginal)
      } else {
        resource.data = filter.value(clonedOriginal)
      }
    })
  }

  // Fetch options when query is changed
  if (useWatch) {
    watch(() => resource.query, search)
  } else {
    resource.onFilter = (value, update) => update(() => search(value))
  }

  return resource
}
