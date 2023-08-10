import {boot} from 'quasar/wrappers';
import {Dialog, date, DialogChainObject} from "quasar";
import {$T} from "boot/i18n";

// *************************************************
// Typed
// *************************************************

export interface Util {
  dateOnly(value: string): string
  formatMoney(value: string): string
  promptConfirm(message: string): DialogChainObject
  promptReason(): DialogChainObject
  nullIfEmpty(value: string): string | null
  camelToSnake(value: string): string
  isUnset(value: any): boolean
  craftFormData(value: object): FormData
  getProp(obj: object, notation: string, delimiter?: string): any
  compare(a: string, b: string): number
  div(htmlString: string): HTMLDivElement
  skipNull(callback: Function): (value: any, oldValue: any, onCleanup: Function) => void
  async<T>(callback: () => T): Promise<Awaited<T>>
}

// *************************************************
// Implementation
// *************************************************

// I18n
let $t: $T<string>;

const util: Util = {
  /**
   * Get date only
   *
   * @param {string} value
   */
  dateOnly(value) {
    return value.substring(0, 10)
  },

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
   * @param {string} message
   * @return {DialogChainObject}
   */
  promptConfirm(message) {
    return Dialog.create({
      title: $t('field.confirm'),
      message: $t('message.confirm', {attr: message.toLowerCase()}),
      cancel: {icon: 'fa-solid fa-xmark', color: 'negative', label: $t('field.confirm_cancel')},
      ok: {icon: 'fa-solid fa-check', color: 'positive', label: $t('field.confirm')},
    })
  },

  /**
   * Prompt for reason input and confirm
   *
   * @return {DialogChainObject}
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
   * @param {string} notation
   * @param {string} delimiter
   * @return {*}
   */
  getProp(obj, notation, delimiter = '.') {
    if (this.isUnset(obj) || this.isUnset(notation)) {
      console.warn(notation)
      return obj
    }

    // Result holder
    let result = obj
    // Split dotNotation by dot
    const notations = notation.split(delimiter)

    while (notations.length > 0) {
      // Get prop
      // @ts-ignore
      result = result[notations.shift()];
      // Immediately return if undefined
      if (this.isUnset(result)) {
        return result
      }
    }

    return result
  },

  /**
   * Complex compare cases
   *
   * @param {string} a
   * @param {string} b
   * @return {number}
   */
  compare(a, b) {
    // Check if this is a Date string
    try {
      return Date.parse(a) - Date.parse(b)
    } catch (e) {
      if (process.env.DEV) {
        console.warn(e)
      }
    }

    return a.localeCompare(b)
  },

  /**
   * Create a div element to wrap htmlString
   *
   * @param {string} htmlString
   * @return {HTMLDivElement}
   */
  div(htmlString) {
    // Create div element
    const div = document.createElement('div')
    div.innerHTML = htmlString
    return div
  },

  /**
   * Skip executing callback if new or old value is null or undefined
   *
   * @param callback
   */
  skipNull(callback) {
    return (value, oldValue, onCleanup) => {
      if (this.isUnset(value) || this.isUnset(oldValue)) {
        return;
      }

      if (typeof callback === 'function') {
        callback(value, oldValue, onCleanup)
      }
    }
  },

  /**
   * Execute callback asynchronously
   *
   * @param {function} callback
   * @return {Promise<void>}
   */
  async async<T>(callback: () => T): Promise<Awaited<T>> {
    return await callback()
  }
}

export default boot(({app}) => {
  $t = app.config.globalProperties.$t
  app.config.globalProperties.$util = util
})

export {util};
