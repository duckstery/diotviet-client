import {toRaw} from 'vue'
import {watchDebounced} from "@vueuse/core";
import {AdvanceStorage} from "boot/storage";
import {StoreDefinition, Store, StateTree, _GettersTree, _ActionsTree, PiniaCustomStateProperties} from "pinia"
import {UnwrapRef} from "vue";

export function useLocalStore<Id extends string = string, S extends StateTree = StateTree, G = _GettersTree<S>, A = _ActionsTree>(
  useStore: StoreDefinition<Id, S, G, A>): Store<Id, S, G, A> {
  // Create key
  const key = `store_${useStore.$id}`
  // Create store
  const store = useStore()

  // Check if LocalStorage contain store's key
  if (AdvanceStorage.has(key)) {
    // Init store's state
    store.$patch({...AdvanceStorage.getItem(key)})
  }
  // Transfer store's state to LocalStorage
  const toLocalStorage = (value: UnwrapRef<S> & PiniaCustomStateProperties<S>) => {
    AdvanceStorage.set(key, toRaw(value))
  }
  // Watch "Order" store's state
  watchDebounced(store.$state, toLocalStorage, {debounce: 1000, deep: true})

  return store
}
