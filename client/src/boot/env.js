import {boot} from 'quasar/wrappers'
import {LocalStorage} from 'quasar'

const env = {
  /**
   * Get item in LocalStorage
   *
   * @param {string} key
   * @returns {any}
   */
  get(key) {
    return LocalStorage.getItem(key)
  },
  /**
   * Set item to LocalStorage
   *
   * @param {String} key
   * @param {any} value
   */
  set(key, value) {
    return LocalStorage.set(key, value)
  },

  /**
   * Check if is light mode
   *
   * @return {boolean}
   */
  isLight() {
    return this.get('display') === 'light'
  },

  /**
   * Check if is optimized for visual
   *
   * @return {boolean}
   */
  isOptimizeVisual() {
    return this.get('optimize') === 'visual'
  }
}

export default boot(({app}) => {
  app.config.globalProperties.$env = env
})

export {env}
