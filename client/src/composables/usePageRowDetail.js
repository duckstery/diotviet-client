import {ref, computed, watch} from "vue";
import {axios, util} from "src/boot"
import {useRouteKey} from "src/composables/useRouteKey";

/**
 * Setup page row detail
 *
 * @param {object} props
 * @param {object} context
 */
export function usePageRowDetail(props, context) {
  // Get key
  const key = useRouteKey()
  // Detail data
  const detail = ref({})

  // Check if component is ready to display data
  const isReady = computed(() => {
    return true;
    // return !(util.isUnset(detail.value) || Object.keys(detail.value).length === 0) //////////////////////////////////////////////////////////////////////////
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
