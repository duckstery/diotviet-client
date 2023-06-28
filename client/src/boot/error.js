import {boot} from 'quasar/wrappers'
import {notify} from "boot/notify";

// Check if error status match the handler
const mustBe = (status, error) => {
  if (error.response.status !== status) {
    throw error
  }
}

/**
 * Bunch of HTTP status error handler
 *
 * @type {object}
 */
const error = {
  /**
   * Handle 410
   *
   * @param key
   * @param callback
   * @param err
   */
  $410(callback, err) {
    mustBe(410, err)
    // Notify
    this.$notifyWarn(this.$t('message.inconsistent_data'))
    callback()
  },

  /**
   * Handle 422
   *
   * @param key
   * @param err
   */
  $422(key, err) {
    mustBe(422, err)

    // Get response's data of err
    const data = err.response.data
    // Merge error
    this.v$[key][data.payload].$server = {
      $error: true,
      $errors: [{
        $validator: 'server',
        $message: data.message
      }]
    }
    // Notify
    this.$notifyErr(this.$t('message.invalid_input'))
  },

  /**
   * Any code
   *
   * @param err
   */
  any(err) {
    notify(err.response.data.message, 'negative')
  }
}

export default boot(({app}) => {
  app.config.globalProperties.$error = error
})

export {error};
