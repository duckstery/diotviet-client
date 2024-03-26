import {boot} from 'quasar/wrappers'
import {LocalStorage, Platform} from 'quasar'
import {Preferences} from "@capacitor/preferences";
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
  get(key: string): Promise<any>
  /**
   * Set env by key
   *
   * @param key
   * @param value
   */
  set(key: string, value: any): Promise<void>
  /**
   * Remove env by key
   *
   * @param key
   */
  remove(key: string): Promise<void>
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
   * @returns {Promise<any>}
   */
  async get(key: string): Promise<any> {
    return Platform.is.capacitor ? (await Preferences.get({key: key})).value : LocalStorage.getItem(key)
  },
  /**
   * Set item to LocalStorage
   *
   * @param {String} key
   * @param {any} value
   */
  async set(key: string, value: any) {
    // Set in Pinia
    $store.setEnv(key, value)
    if (Platform.is.capacitor) {
      return Preferences.set({key: key, value: value})
    } else {
      return LocalStorage.set(key, value)
    }
  },

  /**
   * Remove item
   *
   * @param key
   */
  async remove(key: string) {
    return Platform.is.capacitor ? Preferences.remove({key: key}) : LocalStorage.remove(key)
  },

  /**
   * Check if is optimized for visual
   *
   * @return {boolean}
   */
  isOptimizeVisual(): boolean {
    return $store.optimize !== 'speed'
  }
}

export default boot(async ({app}) => {
  app.config.globalProperties.$env = env
  // Setup store
  $store.language = await env.get('language')
  $store.display = await env.get('display')
  $store.optimize = await env.get('optimize')
})

export {env}
