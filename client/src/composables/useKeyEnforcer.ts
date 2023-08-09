import {onUnmounted} from "vue";
import {useRoute} from "vue-router";

/**
 * Enforce a key
 *
 * @param key
 */
export function useKeyEnforcer(key: string) {
  // Add forced key
  useRoute().meta.forcedKey = key

  // Delete forced key when component is unmounted
  onUnmounted(() => delete useRoute().meta.forcedKey)
}
