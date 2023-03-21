import {boot} from 'quasar/wrappers'
import {axios} from "./axios"
import {Loading, QSpinnerGears} from 'quasar'

const loading = {
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
    Loading.hide()
  }
}

export default boot(({app}) => {
  // Show loading screen when sending axios request
  axios.interceptors.request.use((cfg) => {
    loading.show()
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
