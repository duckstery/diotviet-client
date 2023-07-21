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
    const map = {
      product: 0,
      order: 1,
      customer: 2
    }

    return this.types()[map[key]]
  },

  /**
   * Predefined statuses
   *
   * @returns {array}
   */
  statuses() {
    return [
      {id: 0, name: $t('field.pending'), icon: 'fa-solid fa-circle-notch', color: 'warning'},
      {id: 1, name: $t('field.processing'), verb: $t('field.process'), icon: 'fa-solid fa-circle-dot', color: 'info'},
      {id: 2, name: $t('field.resolved'), verb: $t('field.resolve'), icon: 'fa-solid fa-circle-check', color: 'positive'},
      {id: 3, name: $t('field.aborted'), verb: $t('field.abort'), icon: 'fa-solid fa-circle-stop', color: 'negative'},
    ]
  },

  /**
   * Convert status code to string
   *
   * @param {number|string} code
   * @return {string}
   */
  statusCodeToString(code) {
    return constant.statuses()[typeof code === "string" ? parseInt(code) : code].name
  },

  /**
   * Check if status is resolved
   */
  isStatusResolved(code) {
    return code === 2
  },

  /**
   * Check if status is aborted
   *
   * @param {number} code
   * @return {boolean}
   */
  isStatusAborted(code) {
    return code === 3
  },

  /**
   * Predefined genders
   *
   * @return {Array}
   */
  genders() {
    return [
      {id: 0, name: $t('field.female'), icon: 'fa-solid fa-venus', color: 'negative'},
      {id: 1, name: $t('field.male'), icon: 'fa-solid fa-mars', color: 'primary'}
    ]
  },

  /**
   * Predefined booleans
   *
   * @return {Array}
   */
  booleans() {
    return [
      {id: 0, name: $t('field.false'), icon: 'fa-solid fa-xmark', color: 'negative'},
      {id: 1, name: $t('field.true'), icon: 'fa-solid fa-check', color: 'positive'}
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
      'point': 'fa-solid fa-star',
      'created_by': 'fa-solid fa-feather',
      'resolved_at': 'fa-solid fa-circle-check',
      'info': 'fa-solid fa-circle-info',
      'customer': 'fa-solid fa-credit-card',
      'print': 'fa-solid fa-print',
      'setup': 'fa-solid fa-gear'
    }[key]
  },
}

export default boot(({app}) => {
  $t = app.config.globalProperties.$t
  app.config.globalProperties.$constant = constant
})

export {constant}
