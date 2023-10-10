import {ref, computed, watch, ComputedRef, Prop, SetupContext} from "vue";
import {axios, util, error} from "src/boot"
import {useRouteKey} from "src/composables/useRouteKey";
import {Ref} from "@vue/reactivity";

// *************************************************
// Typed
// *************************************************

export type UsePageRowDetailResources = {
  detail: Ref,
  isReady: ComputedRef<boolean>,
  getItemId: ComputedRef<any>,
  request(key: string, data: any): void,
  patch(payload: any): void,
  remove(): void
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup Detail pages in Manage
 *
 * @param {object} props
 * @param {object} context
 */
export function usePageRowDetail(props: any, context: SetupContext): UsePageRowDetailResources {
  // Get key
  const key = useRouteKey()
  // Detail data
  const detail: Ref = ref({})

  // Check if component is ready to display data
  const isReady = computed(() => {
    // return true;
    return !(util.isUnset(detail.value) || Object.keys(detail.value).length === 0)
  })
  // Get item id
  const getItemId = computed(() => {
    return props.item.value.find((col: any) => col.name === 'id').value
  })

  /**
   * Fetch data
   */
  const fetch = () => {
    axios.get(`/${key}/${getItemId.value}`, {loading: false})
      .then(res => detail.value = res.data.payload)
      .catch(error.$410.bind(null, () => request('fetch', null)))
  }

  /**
   * Request
   *
   * @param key
   * @param data
   */
  const request = (key: string, data: any) => {
    context.emit('request', key, data)
  }

  /**
   * Patch request
   *
   * @param payload
   */
  const patch = (payload: any) => {
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
