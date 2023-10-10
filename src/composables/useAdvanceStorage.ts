import {toRaw} from 'vue'
import {watchDebounced} from "@vueuse/core";
import {AdvanceStorage} from "boot/storage";
import {StoreDefinition, Store, StateTree, _GettersTree, _ActionsTree, PiniaCustomStateProperties} from "pinia"
import {UnwrapRef, nextTick} from "vue";
import {error} from "src/boot";

export function useAdvanceStorage<Id extends string = string, S extends StateTree = StateTree, G = _GettersTree<S>, A = _ActionsTree>(
  useStore: StoreDefinition<Id, S, G, A>,
  onFetch?: () => Promise<object> | object
): Store<Id, S, G, A> {
  // Create key
  const key = `store_${useStore.$id}`
  // Create store
  const store = useStore()

  // Check if AdvanceStorage contains entry
  if (AdvanceStorage.has(key)) {
    // Get data from AdvanceStorage
    let data = <object>AdvanceStorage.getStrict(key)
    // Check if data is invalid and is fetch-able
    if (data !== undefined) {
      // Init store's state
      store.$patch({...data})
    } else if (typeof onFetch === 'function') {
      nextTick(async () => {
        // Fetch data
        data = await onFetch()
        // Save data to AdvanceStorage
        AdvanceStorage.setStrict(key, data)
        // Init store's state
        store.$patch({...data})
      }).catch(error.log)
    }
  }

  // Transfer store's state to AdvanceStorage
  const transferToStorage = (value: UnwrapRef<S> & PiniaCustomStateProperties<S>) => {
    AdvanceStorage.setStrict(key, toRaw(value))
  }
  // Watch "Order" store's state
  watchDebounced(store.$state, transferToStorage, {debounce: 1000, deep: true})

  return store
}

