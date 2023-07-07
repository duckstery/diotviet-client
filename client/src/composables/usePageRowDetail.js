import {ref, computed, watch} from "vue";
import {axios, util, error} from "src/boot"
import {useRouteKey} from "src/composables/useRouteKey";
import {useI18n} from "vue-i18n";

/**
 * Setup Detail pages in Manage
 *
 * @param {object} props
 * @param {object} context
 */
export function usePageRowDetail(props, context) {
  // Get key
  const key = useRouteKey()
  // Detail data
  const detail = ref({})
  // i18n
  const $t = useI18n().t

  // Check if component is ready to display data
  const isReady = computed(() => {
    // return true;
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
      .catch(error.$410.bind(null, () => request('fetch')))
  }

  /**
   * Request
   *
   * @param key
   * @param data
   */
  const request = (key, data) => {
    context.emit('request', key, data)
  }

  /**
   * Patch request
   *
   * @param payload
   */
  const patch = (payload) => {
    request('patch', {ids: [getItemId.value], versions: [detail.value.version], ...payload})
  }

  /**
   * Delete request
   */
  const remove = () => {
    request('delete', {ids: [getItemId.value]})
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
    request,
    patch,
    remove
  }
}
