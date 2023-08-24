import {boot} from 'quasar/wrappers'
import {LocalStorage} from 'quasar'
import {util} from './util'
import {useEnvStore} from "stores/env";

// *************************************************
// Typed
// *************************************************

export interface Env {
  /**
   * Init env
   *
   * @param key
   * @param value
   */
  init(key: string, value: any): any
  /**
   * Get env by key
   *
   * @param key
   */
  get(key: string): any
  /**
   * Set env by key
   *
   * @param key
   * @param value
   */
  set(key: string, value: any): void
  /**
   * Check if optimizing by visual
   */
  isOptimizeVisual(): boolean
}

// *************************************************
// Implementation
// *************************************************

const $store = useEnvStore()
const env: Env = {
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
