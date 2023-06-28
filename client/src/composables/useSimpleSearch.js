import {axios} from "src/boot";
import {reactive, watch} from "vue";

/**
 * Setup simple search and return options
 *
 * @param {string} api
 * @param {boolean} useWatch
 * @return {object}
 */
export function useSimpleSearch(api, useWatch = true) {
  // Search resource
  const resource = reactive({
    query: '',
    options: [],
    onFilter: null
  })

  // Search logic
  const search = async (value) => value === null || value === ''
    ? []
    : (await axios.get(api, {params: {search: value}, loading: false})).data.payload

  // Fetch options when query is changed
  if (useWatch) {
    watch(() => resource.query, search)
  } else {
    resource.onFilter = (value, update) => update(async () => resource.options = await search(value))
  }

  return resource
}
