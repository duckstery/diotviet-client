import {watch, SetupContext} from "vue";
import {UnwrapNestedRefs} from "@vue/reactivity";

/**
 * Setup filter request on filter changed
 *
 * @param {object} filterRef
 * @param {{emit: function}} context
 */
export function useFilterRequest<T>(filterRef: UnwrapNestedRefs<T>, context: SetupContext) {
  // Setup watch handler
  const handler = (value: any) => {
    context.emit('update:modelValue', value)
    context.emit('request')
  }

  // Watch to emit filter request
  watch(() => filterRef, handler, {deep: true})
}
