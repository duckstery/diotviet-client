import {boot} from 'quasar/wrappers'
import {axios, LocalAxiosInternalRequestConfig} from "./axios"
import {util} from "./util"
import {Loading, QSpinnerGears} from 'quasar'
import * as process from "process";

// *************************************************
// Typed
// *************************************************

export interface Loader {
  /**
   * Show loader overlay
   */
  show(): void
  /**
   * Hide loader overlay
   */
  hide(): void
  /**
   * Wait for a callback
   *
   * @param callback
   */
  wait(callback: () => void): void
}

// *************************************************
// Implementation
// *************************************************

const loading: Loader = {
  /**
   * Show loading overlay
   */
  show() {
    Loading.show({spinner: QSpinnerGears,})
  },
  /**
   * Hide loading overlay
   */
  hide() {
    if (Loading.isActive) {
      Loading.hide()
    }
  },
  /**
   * Wait for a callback
   *
   * @param callback
   */
  async wait(callback: () => void) {
    try {
      this.show()
      await callback()
      this.hide()
    } catch (e) {
      this.hide()
      console.error(e)
    }
  }
}

export default boot(({app}) => {
  // Show loading screen when sending axios request
  axios.interceptors.request.use((cfg: LocalAxiosInternalRequestConfig) => {
    // Disable loading option
    if (util.isUnset(cfg.loading) || cfg.loading) {
      loading.show()
    }

    return cfg
  })

  // Hide loading screen when receiving axios request
  axios.interceptors.response.use((res) => {
    loading.hide()

    return res
  }, (error) => {
    loading.hide()

    throw error
  })

  // Set this.$loading usage
  app.config.globalProperties.$loading = loading
})

export {loading}
