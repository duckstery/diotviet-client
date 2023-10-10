import {useRoute} from "vue-router";
import {util} from "src/boot"

/**
 * Get route key
 *
 * @return {string}
 */
export function useRouteKey(): string {
  // Get route
  const route = useRoute()

  return <string>(util.isUnset(route.meta['forcedKey'])
    ? (route.meta['key'] ?? '')
    : route.meta['forcedKey'])
}
