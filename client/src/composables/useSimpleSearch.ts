import {axios, error, util,} from "src/boot";
import {nextTick, reactive, UnwrapNestedRefs, watch} from "vue";
// @ts-ignore
import _ from "lodash";
import {Ref} from "@vue/reactivity";

// *************************************************
// Typed
// *************************************************

export type UseSimpleSearchResources = {
  query: string,
  data: [],
  original: [],
  filter(): void,
  search(): void,
  // Event for Quasar's component
  onFilter: null | ((value: any, update: Function) => void)
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup simple search and return options
 *
 * @param {string} api
 * @param {boolean} useWatch
 * @param {ComputedRef<*>|function} filter
 * @return {object}
 */
export function useSimpleSearch(api: string, useWatch: boolean = true, filter: Function | Ref<Function> | null = null): UnwrapNestedRefs<UseSimpleSearchResources> {
  // Search resource
  const resource: UnwrapNestedRefs<UseSimpleSearchResources> = reactive({
    query: '',
    data: [],
    original: [],
    filter: () => triggerFilter(),
    search: () => search(resource.query),
    // Event for Quasar's component
    onFilter: null
  })

  // Search logic
  const search = (value: string | null) => {
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
    // Reset data only if data is not empty
    if (!_.isEmpty(resource.data)) {
      resource.data = []
    }

    nextTick(() => {
      // Deep clone original
      const clonedOriginal = _.cloneDeep(resource.original)

      if (util.isUnset(filter)) {
        resource.data = clonedOriginal
      } else if (typeof filter === 'function') {
        resource.data = filter(clonedOriginal)
      } else {
        // @ts-ignore
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
