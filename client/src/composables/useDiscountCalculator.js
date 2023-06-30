/**
 * Setup discount calculator
 *
 * @return {function(original: string, discountUnit: string, discount: string): string}
 */
export function useDiscountCalculator() {
  /**
   * Calculate discount amount
   *
   * @param {string} original
   * @param {string} discountUnit
   * @param {string} discount
   */
  return (original, discountUnit, discount) => {
    try {
      const discountAmount = discountUnit === '%'
        // Discount by percentage
        ? parseInt(original) / 100 * parseInt(discount)
        // Discount by plain value
        : parseInt(discount)

      return `${discountAmount}` ?? '0'
    } catch {
      return '0'
    }
  }
}
