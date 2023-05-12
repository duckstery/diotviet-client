import {computed, watch, nextTick} from 'vue'
import {useI18n} from 'vue-i18n'

export default function (refObj, originalKey, actualKey) {
  // Make a computed fragments to modify computed key dynamically
  const computedFragments = {}
  computedFragments[actualKey] = computed(() => {
    const discountAmount = refObj.discountUnit === '%'
      // Discount by percentage
      ? parseInt(refObj[originalKey]) / 100 * parseInt(refObj.discount)
      // Discount by plain value
      : parseInt(refObj.discount)

    return `${parseInt(refObj[originalKey]) - Math.round(discountAmount)}`
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
    watch: [
      // Watch for fragment. Assign "actual" computed to component's data
      watch(computedFragments[actualKey], (value) => {
        if (refObj[actualKey] !== undefined) {
          refObj[actualKey] = value
        }
      }),

      // Control original price max and min value
      watch(() => refObj[originalKey], (value) => {

        // Get value as integer
        const intValue = parseInt(value);
        if (intValue > 999999999) {
          nextTick(() => refObj[originalKey] = '999999999')
        } else if (intValue < 1) {
          nextTick(() => refObj[originalKey] = '1000')
        }
      }),

      // Control discount max and min value
      watch(() => refObj.discount, (value) => {
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
      watch(() => refObj.discountUnit, (value, oldValue) => {
        if (oldValue) {
          nextTick(() => refObj.discount = '0')
        }
      }),
    ]
  }

  return {
    ...options.computed,
    ...options.watch
  }
}
