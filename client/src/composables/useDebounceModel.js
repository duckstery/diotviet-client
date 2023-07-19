import {watch, ref} from "vue";

/**
 * Setup debounce model <br>
 * This model will be attached to v-model <br>
 * When v-model emit @update, it'll will save data of @update and save to original ref after [millisecond]
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

    // There is no point of wasting resources if middle is equal target
    if (middle.value === refObj.value) return
    // When middle.value is changed, create a timeout to set refObj.value
    timeout = setTimeout(() => {
      // Set data
      refObj.value = value
      // Clear timeout if this timeout is activated successfully
      timeout = null
    }, millisecond)
  })
  // Watch when target is changed, middle must be change
  watch(refObj, (value) => middle.value = value)

  return middle
}
