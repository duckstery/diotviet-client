import {boot} from 'quasar/wrappers'
import {$T} from "boot/i18n";
import {Color} from "chart.js";

// *************************************************
// Typed
// *************************************************

export interface Constant {
  roles(): { id: number, name: string }[]
  /**
   * Get predefined types
   */
  types(): { id: number, name: string }[]
  /**
   * Get predefined types by key
   *
   * @param key
   */
  typeByKey(key: string): { id: number, name: string }
  /**
   * Get predefined statuses
   */
  statuses(): { id: number, name: string, icon: string, color: string, verb?: string }[]
  /**
   * Convert status code to string
   *
   * @param code
   */
  statusCodeToString(code: string | number): string
  /**
   * Check if code is Status: RESOLVED
   *
   * @param code
   */
  isStatusResolved(code: number): boolean
  /**
   * Check if code is Status: ABORT
   * @param code
   */
  isStatusAborted(code: number): boolean
  /**
   * Get predefined genders
   */
  genders(): { id: number, name: string, icon: string, color: string }[]
  /**
   * Get predefined boolean options
   */
  booleans(): { id: number, name: string, icon: string, color: string }[]
  /**
   * Get ChartJs predefined color by key
   */
  chartColors(key: 'red' | 'orange' | 'yellow' | 'green' | 'blue' | 'purple' | 'grey', opacity: number): Color | undefined
  /**
   * ChartJS available types
   */
  chartTypes(): { value: string, label: string }[]
  /**
   * Transaction types
   */
  transactionTypes(): { id: number, name: string, icon: string, color: string }[]
  /**
   * Map png to icon
   *
   * @param key
   */
  matchedIcon(key: string): string | undefined
}

// *************************************************
// Implementation
// *************************************************

// I18n
let $t: $T<string>;

const constant: Constant = {
  /**
   * Predefined roles
   */
  roles() {
    return [
      {id: 0, name: $t('field.role_0')},
      {id: 1, name: $t('field.role_1')},
      {id: 2, name: $t('field.role_2')},
      {id: 3, name: $t('field.role_3')},
      {id: 4, name: $t('field.role_4')},
    ]
  },

  /**
   * Predefined types
   */
  types() {
    return [
      {id: 0, name: $t('field.product')},
      {id: 1, name: $t('field.transaction')},
      {id: 2, name: $t('field.customer')},
      {id: 3, name: $t('field.print')},
    ]
  },

  /**
   * Get predefined types by key
   *
   * @param key
   */
  typeByKey(key: string) {
    // Generate map
    const map: { [key: string]: number } = {
      product: 0,
      order: 1,
      customer: 2
    }

    return this.types()[map[key]]
  },

  /**
   * Predefined statuses
   */
  statuses() {
    return [
      {id: 0, name: $t('field.pending'), icon: 'fa-solid fa-circle-notch', color: 'warning'},
      {id: 1, name: $t('field.processing'), verb: $t('field.process'), icon: 'fa-solid fa-circle-dot', color: 'info'},
      {
        id: 2,
        name: $t('field.resolved'),
        verb: $t('field.resolve'),
        icon: 'fa-solid fa-circle-check',
        color: 'positive'
      },
      {id: 3, name: $t('field.aborted'), verb: $t('field.abort'), icon: 'fa-solid fa-circle-stop', color: 'negative'},
    ]
  },

  /**
   * Convert status code to string
   *
   * @param code
   */
  statusCodeToString(code: string | number) {
    return constant.statuses()[typeof code === "string" ? parseInt(code) : code].name
  },

  /**
   * Check if status is resolved
   */
  isStatusResolved(code: number) {
    return code === 2
  },

  /**
   * Check if status is aborted
   *
   * @param code
   */
  isStatusAborted(code: number) {
    return code === 3
  },

  /**
   * Predefined genders
   */
  genders() {
    return [
      {id: 0, name: $t('field.female'), icon: 'fa-solid fa-venus', color: 'negative'},
      {id: 1, name: $t('field.male'), icon: 'fa-solid fa-mars', color: 'primary'}
    ]
  },

  /**
   * Predefined booleans
   */
  booleans() {
    return [
      {id: 0, name: $t('field.false'), icon: 'fa-solid fa-xmark', color: 'negative'},
      {id: 1, name: $t('field.true'), icon: 'fa-solid fa-check', color: 'positive'}
    ]
  },

  /**
   * Get ChartJs predefined color by key
   *
   * @param key
   * @param opacity
   */
  chartColors(key: 'red' | 'orange' | 'yellow' | 'green' | 'blue' | 'purple' | 'grey', opacity: number = 1): any {
    return {
      'red': 'rgba(255, 99, 132, 256)',
      'orange': 'rgba(255, 159, 64, 256)',
      'yellow': 'rgba(255, 205, 86, 256)',
      'green': 'rgba(75, 192, 192, 256)',
      'blue': 'rgba(54, 162, 235, 256)',
      'purple': 'rgba(153, 102, 255, 256)',
      'grey': 'rgba(201, 203, 207, 256)'
    }[key]?.replace('256', `${opacity}`)
  },

  /**
   * ChartJS available types
   */
  chartTypes(): { value: string; label: string }[] {
    return [
      {value: 'bar', label: $t('field.bar')},
      {value: 'line', label: $t('field.line')}
    ]
  },

  /**
   * Transaction types
   */
  transactionTypes() {
    return [
      {id: 0, name: $t('field.spend'), icon: 'fa-solid fa-minus', color: 'negative'},
      {id: 1, name: $t('field.collect'), icon: 'fa-solid fa-plus', color: 'positive'}
    ]
  },

  /**
   * Get corresponding icon of image
   *
   * @param key
   * @return
   */
  matchedIcon(key: string) {
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
      'user': 'fa-solid fa-user',
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
      'setup': 'fa-solid fa-gear',
      'amount': 'fa-solid fa-hand-holding-dollar',
      'reason': 'fa-solid fa-lightbulb',
      'role': 'fa-solid fa-shield',
    }[key]
  },
}

export default boot(({app}) => {
  $t = app.config.globalProperties.$t
  app.config.globalProperties.$constant = constant
})

export {constant}
