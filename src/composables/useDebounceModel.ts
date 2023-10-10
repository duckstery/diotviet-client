import {watch, ref} from "vue";
import {Ref} from "@vue/reactivity";

/**
 * Setup debounce model <br>
 * This model will be attached to v-model <br>
 * When v-model emit @update, it'll will save data of @update and save to original ref after [millisecond]
 *
 * @param {Ref<UnwrapRef<T>>} refObj
 * @param {number} millisecond
 * @return {Ref<UnwrapRef<T>>}
 */
export function useDebounceModel<T>(refObj: Ref<T>, millisecond: number = 500): Ref<T> {
  // Create ref to catch data
  const middle: Ref = ref(refObj.value)

  // Timeout
  let timeout: number | null = null

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
    // @ts-ignore
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
