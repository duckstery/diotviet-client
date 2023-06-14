import {boot} from 'quasar/wrappers'
/**
 * Bunch of HTTP status error handler
 *
 * @type {object}
 */
const error = {
  /**
   * Handle 422
   *
   * @param key
   * @param err
   */
  $422(key, err) {
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
  }
}

export default boot(({app}) => {
  app.config.globalProperties.$error = error
})

export {error};
