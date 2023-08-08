import {boot} from 'quasar/wrappers'
import {axios, LocalAxiosInternalRequestConfig} from "./axios"
import {Loading, QSpinnerGears} from 'quasar'

// *************************************************
// Typed
// *************************************************

export interface Loader {
  show(): void
  hide(): void
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
  }
}

export default boot(({app}) => {
  // Show loading screen when sending axios request
  axios.interceptors.request.use((cfg: LocalAxiosInternalRequestConfig) => {
    // Disable loading option
    if (cfg.loading) {
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
