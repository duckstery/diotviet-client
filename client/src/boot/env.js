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
  }
}

export default boot(({app}) => {
  app.config.globalProperties.$env = env
})

export {env}
