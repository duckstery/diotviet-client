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
    notify(this.$t(`message.${err.response.data.payload}`), 'warning')
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
    notify(this.$t('message.invalid_input'), 'negative')
  },

  /**
   * Any code
   *
   * @param err
   */
  any(err) {
    notify(err.response.data.message, 'negative')
  },

  switch(cases) {
    return (error) => {
      // Get code of error
      const httpCode = error.response.status

      // Get handler of httpCode
      if (typeof this[`$${httpCode}`] === 'function') {
        this[`$${httpCode}`].bind(...cases[httpCode])(error)
      }
    }
  }
}

export default boot(({app}) => {
  app.config.globalProperties.$error = error
})

export {error};
