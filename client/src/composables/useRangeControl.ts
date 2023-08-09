import {watch, nextTick, ref, Ref} from "vue"

/**
 * Setup control range system
 *
 * @param {Ref<UnwrapRef<...>>} refObj
 * @param {Number} upperBound
 * @param {Number} lowerBound
 * @return {{rangeControlCommit: String}}
 */
export function useRangeControl(refObj: Ref<string>, upperBound: number = 999999999999, lowerBound: number = null): {
  rangeControlCommit: Ref<string>
} {
  // Create commit flag
  const rangeControlCommit = ref(null)

  watch(refObj, (newValue, oldValue) => {
    // Allow null value
    if (lowerBound === null && (newValue === null || newValue === '')) {
      return;
    }
    // Get value as integer
    const intValue = parseInt(newValue);

    if (intValue > upperBound) {
      nextTick(() => refObj.value = upperBound.toString())
    } else if (intValue < (lowerBound ?? 0) || isNaN(intValue)) {
      nextTick(() => refObj.value = (lowerBound ?? 0).toString())
    } else if (oldValue === '0') {
      nextTick(() => refObj.value = intValue.toString())
    } else {
      nextTick(() => rangeControlCommit.value = intValue.toString())
    }
  })

  return {
    rangeControlCommit
  }
}
