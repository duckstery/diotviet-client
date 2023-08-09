import {computed, watch, nextTick, ComputedRef, UnwrapNestedRefs} from 'vue'
import {useI18n} from 'vue-i18n'
import {useDiscountCalculator} from "src/composables/useDiscountCalculator";

// *************************************************
// Typed
// *************************************************

export type UsePriceControlResources = {
  // Computed
  discountUnitIcon: ComputedRef<string>,
  discountUnitLabel: ComputedRef<string>,
  discountMask: ComputedRef<string>,
  // Watcher
  actualUnwatch(): void,
  originalMaxMinUnwatch(): void,
  discountMaxMinUnwatch(): void,
  resetDiscountUnwatch(): void
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup control price system
 *
 * @param {object} refObj
 * @param {string} originalKey
 * @param {string} actualKey
 * @returns {object}
 */
export function usePriceControl(refObj: UnwrapNestedRefs<{
  discountUnit: string,
  discount: string,
  [key: string]: string
}>, originalKey: string, actualKey: string): UsePriceControlResources {
  // Resources
  const discountCalculator = useDiscountCalculator()
  // Make a computed fragments to modify computed key dynamically
  const computedFragments: { [key: string]: ComputedRef<string> } = {}
  // @ts-ignore
  computedFragments[actualKey] = computed(() => {
    const discountAmount = discountCalculator(refObj[originalKey], refObj.discountUnit, refObj.discount)

    return `${parseInt(refObj[originalKey]) - Math.round(parseInt(discountAmount))}`
  })

  const options = {
    // Computed
    computed: {
      // Icon of discountUnit switch
      discountUnitIcon: computed(() => 'fa-solid fa-' + (refObj.discountUnit === 'cash' ? 'money-bill-wave' : 'percent')),
      // Label of discountUnit text
      discountUnitLabel: computed(() => useI18n().t('field.discount_by') + ' ' + refObj.discountUnit),
      // TextField mask of discountUnit
      discountMask: computed(() => refObj.discountUnit === 'cash' ? '###,###,###,###' : '##%'),

      // Spread fragments
      ...computedFragments
    },

    // Watch
    watch: {
      // Watch for fragment. Assign "actual" computed to component's data
      actualUnwatch: watch(computedFragments[actualKey], (value) => {
        if (refObj[actualKey] !== undefined) {
          refObj[actualKey] = value
        }
      }),

      // Control original price max and min value
      originalMaxMinUnwatch: watch(() => refObj[originalKey], (value) => {

        // Get value as integer
        const intValue = parseInt(value);
        if (intValue > 999999999) {
          nextTick(() => refObj[originalKey] = '999999999')
        } else if (intValue < 0) {
          nextTick(() => refObj[originalKey] = '0')
        }
      }),

      // Control discount max and min value
      discountMaxMinUnwatch: watch(() => refObj.discount, (value) => {
        // Get value as integer
        const intValue = parseInt(value);
        // Can not lower than 0
        if (intValue < 0) {
          nextTick(() => refObj.discount = '0')
        }

        // Check discountUnit
        if (refObj.discountUnit === 'cash' && intValue > parseInt(refObj[originalKey])) {
          nextTick(() => refObj.discount = refObj[originalKey])
        } else if (refObj.discountUnit === '%' && intValue > 100) {
          nextTick(() => refObj.discount = '100')
        } else if (isNaN(intValue) || intValue === null || intValue === undefined) {
          nextTick(() => refObj.discount = '0')
        }
      }),

      // Reset discount to 0 if discountUnit is changed
      resetDiscountUnwatch: watch(() => refObj.discountUnit, (value, oldValue) => {
        if (oldValue) {
          nextTick(() => refObj.discount = '0')
        }
      }),
    }
  }

  return {
    ...options.computed,
    ...options.watch
  }
}
