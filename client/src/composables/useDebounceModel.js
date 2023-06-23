import {watch, ref} from "vue";

/**
 * Setup debounce model
 *
 * @param {Ref<UnwrapRef<...>>} refObj
 * @param {number} millisecond
 * @return {T}
 */
export function useDebounceModel(refObj, millisecond= 500) {
  // Create ref to catch data
  const middle = ref(refObj.value)
  // Timeout
  let timeout = null

  // Watch when middle is change, active debounce to set ref data
  watch(middle, (value) => {
    // Check if timeout is set
    if (timeout !== null) {
      // Clear timeout
      clearTimeout(timeout)
      // Set to null
      timeout = null
    }

    // When middle.value is changed, create a timeout to set refObj.value
    timeout = setTimeout(() => {
      // Set data
      refObj.value = value
      // Clear timeout if this timeout is activated successfully
      timeout = null
    }, millisecond)
  })

  return middle
}
