import {boot} from 'quasar/wrappers';
import {Dialog} from "quasar";

// I18n
let $t;

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
   */
  promptConfirm() {
    return Dialog.create({
      title: $t('field.confirm'),
      message: $t('message.confirm'),
      cancel: {icon: 'fa-solid fa-xmark', color: 'negative', label: $t('field.confirm_cancel')},
      ok: {icon: 'fa-solid fa-check', color: 'positive', label: $t('field.confirm')},
    })
  },

  /**
   * Prompt for reason input and confirm
   */
  promptReason() {
    return Dialog.create({
      title: $t('field.reason'),
      message: $t('message.reason_confirm'),
      prompt: {model: '', isValid: val => !!val},
      cancel: {icon: 'fa-solid fa-xmark', color: 'negative', label: $t('field.confirm_cancel')},
      ok: {icon: 'fa-solid fa-check', color: 'positive', label: $t('field.confirm')},
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
  },

  /**
   * Check if value is unset
   *
   * @param {any} value
   * @return {boolean}
   */
  isUnset(value) {
    return value === null || value === undefined
  },

  /**
   * Craft FormData with object
   *
   * @param {object} input
   * @return {FormData}
   */
  craftFormData(input) {
    // Create output
    const formData = new FormData()

    // Set data
    if (!this.isUnset(input)) {
      Object.entries(input).forEach(entry => this.isUnset(entry[1]) || formData.append(...entry))
    }

    return formData
  },

  /**
   * Check prop in object by dot notation
   *
   * @param {object} obj
   * @param {string} dotNotation
   * @return {*}
   */
  getProp(obj, dotNotation) {
    if (this.isUnset(obj)) {
      return obj
    }

    // Result holder
    let result = obj
    // Split dotNotation by dot
    const notations = dotNotation.split(".")

    while (notations.length > 0) {
      // Get prop
      result = result[notations.shift()];
      // Immediately return if undefined
      if (this.isUnset(result)) {
        return result
      }
    }

    return result
  },

  /**
   * Compare alphabetically
   *
   * @param {string} a
   * @param {string} b
   */
  alphabetically(a, b) {
    return a.localeCompare(b)
  },
}

export default boot(({app}) => {
  $t = app.config.globalProperties.$t
  app.config.globalProperties.$util = util
})

export {util};
