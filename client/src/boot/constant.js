import {boot} from 'quasar/wrappers'

// I18n
let $t;

const constant = {
  /**
   * Predefined types
   *
   * @returns {array}
   */
  types() {
    return [
      {id: 0, name: $t('field.product')},
      {id: 1, name: $t('field.transaction')},
      {id: 2, name: $t('field.customer')},
    ]
  },

  /**
   * Get predefined types by key
   *
   * @param {string} key
   * @return {object}
   */
  typeByKey(key) {
    // Generate map
    const map = {product: 0, transaction: 1, customer: 2}

    return this.types()[map[key]]
  },

  /**
   * Predefined statuses
   *
   * @returns {array}
   */
  statuses() {
    return [
      {id: 0, name: $t('field.pending')},
      {id: 1, name: $t('field.resolved')},
      {id: 2, name: $t('field.aborted')},
    ]
  },

  /**
   * Get corresponding icon of image
   *
   * @param {string} key
   * @return {string}
   */
  matchedIcon(key) {
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
      'name': 'fa-solid fa-signature',
      'translation': 'fa-solid fa-language',
      'vi_VN': 'fa-solid fa-v',
      'visual': 'fa-solid fa-chart-pie',
      'weight': 'fa-solid fa-weight-hanging',
      'woman': 'fa-solid fa-user',
      'address': 'fa-solid fa-map-location-dot',
      'birthday': 'fa-solid fa-cake-candles',
      'created_at': 'fa-solid fa-pen',
      'email': 'fa-solid fa-envelope-open-text',
      'gender': 'fa-solid fa-venus-mars',
      'facebook': 'fa-brands fa-facebook',
      'last_order_at': 'fa-solid fa-arrows-rotate',
      'last_transaction_at': 'fa-solid fa-arrows-rotate',
      'phone_number': 'fa-solid fa-phone-volume',
    }[key]
  },
}

export default boot(({app}) => {
  $t = app.config.globalProperties.$t
  app.config.globalProperties.$constant = constant
})

export {constant}
