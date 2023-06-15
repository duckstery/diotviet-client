import {ref, computed, watch, } from "vue";
import {util} from "boot/util"
import {axios} from "boot/axios"

/**
 * Setup page row detail
 *
 * @param {string} key
 * @param {object} props
 * @param {object} context
 */
export function usePageRowDetail(key, props, context) {
  // Detail data
  const detail = ref({})

  // Check if component is ready to display data
  const isReady = computed(() => {
    return !(util.isUnset(detail.value) || Object.keys(detail.value).length === 0)
  })
  // Get item id
  const getItemId = computed(() => {
    return props.item.value.find(col => col.name === 'id').value
  })

  /**
   * Fetch data
   */
  const fetch = () => {
    axios.get(`/${key}/${getItemId.value}`, {loading: false})
      .then(res => detail.value = res.data.payload)
  }
  const request = (key, data) => {
    context.emit('request', key, data)
  }

  // Create watcher to fetch data when it's active
  watch(props.active, (value) => value && fetch())

  return {
    // Data
    detail,
    // Computed
    isReady,
    getItemId,
    // Methods
    request
  }
}
