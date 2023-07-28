import {boot} from 'quasar/wrappers'
import {notify} from "boot/notify"
import {util} from "boot/util"

let $t;

// Log to console
const log = (err) => {
  if (process.env.DEV) {
    console.error(err)
  }
}

// Check if error status match the handler
const mustBe = (status, error) => {
  log(error)
  if (error.response.status !== status) {
    throw error
  }
}

/**
 * Common notify logic
 *
 * @param {function} callback
 * @param {Axios.Error} err
 * @param {string} type
 */
const commonNotify = (callback, err, type) => {
  // Notify
  notify($t(`message.${err.response.data.payload}`), type)
  if (typeof callback === 'function') {
    callback(err)
  }
}

/**
 * Bunch of HTTP status error handler
 *
 * @type {object}
 */
const error = {
  /**
   * Handle 400
   *
   * @param {function} callback
   * @param {Axios.Error} err
   */
  $400(callback, err) {
    mustBe(400, err)
    // Notify
    commonNotify(callback, err, 'negative')
  },

  /**
   * Handle 410
   *
   * @param {function} callback
   * @param {Axios.Error} err
   */
  $410(callback, err) {
    mustBe(410, err)
    // Notify
    commonNotify(callback, err, 'warning')
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
    notify($t('message.invalid_input'), 'negative')
  },

  /**
   * Any code
   *
   * @param err
   */
  any(err) {
    log(err)
    notify(err.response.data.message, 'negative')
  },

  /**
   * Switch and execute corresponding handler
   *
   * @param cases
   * @return {(function(*): void)|*}
   */
  switch(cases) {
    return (error) => {
      // Get code of error
      const httpCode = error.response.status

      // Get handler of httpCode
      if (typeof this[`$${httpCode}`] === 'function') {
        // Check if case of httpCode is an array, if not, convert to array
        const bindArgs = Array.isArray(cases[httpCode]) ? cases[httpCode] : [null, cases[httpCode]]
        // Execute the corresponding http status handler
        this[`$${httpCode}`].bind(...bindArgs)(error)
      } else if (typeof cases.default === 'function') {
        // Else if default case is present, execute it
        cases.default(error)
      }
    }
  }
}

export default boot(({app}) => {
  $t = app.config.globalProperties.$t
  app.config.globalProperties.$error = error
})

export {error};
