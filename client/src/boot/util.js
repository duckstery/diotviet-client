import {boot} from 'quasar/wrappers'

const util = {
  /**
   * Format money (add comma to separate unit)
   *
   * @param {string} value
   * @returns {string}
   */
  formatMoney(value) {
    // Create endIdx, after each endIdx, a comma (,) will be inserted. endIdx will be initiated with remainder of value's length div by 3
    let endIdx = value.length % 3;
    // If no remainder, assign to 3
    endIdx = endIdx === 0 ? 3 : endIdx

    while (endIdx < value.length) {
      // Insert a comma into value
      value = value.slice(0, endIdx) + ',' + value.slice(endIdx);
      // Recalculate endIdx = endIdx + 2 (skip inserted comma and self) + 3
      endIdx += 1 + 3;
    }

    return value;
  },
}

export default boot(({app}) => {
  app.config.globalProperties.$util = util
})

export {util};
