import {watch} from "vue";

/**
 * Setup filter request on filter changed
 *
 * @param {object} filterRef
 * @param {{emit: function}} context
 */
export function useFilterRequest(filterRef, context) {
  // Setup watch handler
  const handler = (value) => {
    context.emit('update:modelValue', value)
    context.emit('request')
  }

  // Watch to emit filter request
  watch(() => filterRef, handler, {deep: true})
}
