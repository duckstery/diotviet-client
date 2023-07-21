import {boot} from 'quasar/wrappers'
import {LocalStorage} from 'quasar'
import {util} from './util'
import {useEnvStore} from "stores/env";

const $store = useEnvStore()
const env = {
  /**
   * Init env variable
   *
   * @param key
   * @param value
   * @return {*}
   */
  init(key, value) {
    if (util.isUnset(this.get(key))) {
      this.set(key, value)
    }

    return this.get(key)
  },
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
    // Set in Pinia
    $store.setEnv(key, value)
    return LocalStorage.set(key, value)
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
  // Setup store
  $store.language = env.get('language')
  $store.display = env.get('display')
  $store.optimize = env.get('optimize')
})

export {env}
