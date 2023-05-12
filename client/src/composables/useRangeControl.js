import {watch, nextTick, ref} from 'vue'

/**
 * Setup control range system
 *
 * @param {object} refObj
 * @param {string} on
 * @param {Number} upperBound
 * @param {Number} lowerBound
 * @return {{rangeControlCommit: String}}
 */
export function useRangeControl (refObj, on, upperBound, lowerBound) {
  // Create commit flag
  const rangeControlCommit = ref(null)

  watch(() => refObj[on], (newValue, oldValue) => {
    // Get value as integer
    const intValue = parseInt(newValue);
    if (intValue > upperBound) {
      nextTick(() => refObj[on] = upperBound.toString())
    } else if (intValue < lowerBound || isNaN(intValue)) {
      nextTick(() => refObj[on] = lowerBound.toString())
    } else if (oldValue === '0') {
      nextTick(() => refObj[on] = intValue.toString())
    } else {
      nextTick(() => rangeControlCommit.value = intValue.toString())
    }
  })

  return {
    rangeControlCommit
  }
}
