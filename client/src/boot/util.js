import {boot} from 'quasar/wrappers'
import {Dialog} from "quasar";

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

  /**
   * Prompt for confirm
   *
   * @param {Vue} root
   */
  promptConfirm(root) {
    return Dialog.create({
      title: root.$t('field.confirm'),
      message: root.$t('message.confirm'),
      cancel: {icon: 'fa-solid fa-xmark', color: 'negative', label: root.$t('field.confirm_cancel')},
      ok: {icon: 'fa-solid fa-check', color: 'positive', label: root.$t('field.confirm')},
    })
  },

  /**
   * Return null if value is an empty string
   *
   * @param value
   * @returns {any}
   */
  nullIfEmpty(value) {
    return value === '' ? null : value
  },

  /**
   * Turn camelCase to snake_case
   *
   * @param value
   * @returns {*}
   */
  camelToSnake(value) {
    return value.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`)
  }
}

export default boot(({app}) => {
  app.config.globalProperties.$util = util
})

export {util};
