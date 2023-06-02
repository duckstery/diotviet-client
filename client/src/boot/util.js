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
   * Get corresponding icon of image
   *
   * @param {string} key
   * @return {string}
   */
  getMatchedIcon(key) {
    return {
      'actual_price': 'fa-solid fa-money-bill',
      'category': 'list',
      'code': 'fingerprint',
      'dark': 'dark_mode',
      'dark-mode': 'fa-solid fa-circle-half-stroke',
      'discount': 'fa-solid fa-tags',
      'en_US': 'fa-solid fa-e',
      'groups': 'fa-solid fa-people-group',
      'image': 'fa-solid fa-images',
      'light': 'light_mode',
      'mail': 'fa-solid fa-envelope',
      'man': 'fa-solid fa-user',
      'measure_unit': 'fa-solid fa-weight-scale',
      'note': 'fa-solid fa-comment',
      'open_folder': 'fa-solid fa-folder-open',
      'optimize': 'fa-solid fa-gauge',
      'original_price': 'fa-solid fa-coins',
      'pass': 'fa-solid fa-lock',
      'payment_amount': 'fa-solid fa-hand-holding-dollar',
      'provisional_amount': 'fa-solid fa-hand-holding-dollar',
      'speed': 'fa-solid fa-rocket',
      'title': 'fa-solid fa-signature',
      'translation': 'fa-solid fa-language',
      'vi_VN': 'fa-solid fa-v',
      'visual': 'fa-solid fa-chart-pie',
      'weight': 'fa-solid fa-weight-hanging',
      'woman': 'fa-solid fa-user',
    }[key]
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
  }
}

export default boot(({app}) => {
  app.config.globalProperties.$util = util
})

export {util};
