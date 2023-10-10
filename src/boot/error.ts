import {boot} from 'quasar/wrappers'
import {notify} from "boot/notify"
import {AxiosError} from "axios";
import {LocalAxiosResponse} from "boot/axios";
import {Validation} from "@vuelidate/core";
import {$T} from "boot/i18n";

// *************************************************
// Error
// *************************************************

export type ErrorSwitcher = { 400?: Function, 410?: Function, 422?: [{ v$: Validation }, string], default: Function }

export interface ErrorHandler {
  /**
   * Handle HTTP Status: 400
   *
   * @param callback
   * @param err
   */
  $400(callback: Function, err: AxiosError<LocalAxiosResponse, any>): void
  /**
   * Handle HTTP Status: 410
   *
   * @param callback
   * @param err
   */
  $410(callback: Function, err: AxiosError<LocalAxiosResponse, any>): void
  /**
   * Handle HTTP Status: 422 (Work with Vuelidate)
   *
   * @param key
   * @param err
   */
  $422(key: string, err: AxiosError<LocalAxiosResponse, any>): void
  /**
   * Simple log
   *
   * @param err
   */
  any(err: AxiosError<LocalAxiosResponse, any>): void
  /**
   * Match error with cases
   *
   * @param cases
   */
  switch(cases: ErrorSwitcher | Function): (error: AxiosError) => void
  /**
   * Log error
   *
   * @param err
   */
  log(err: Error): void
}

// *************************************************
// Implementation
// *************************************************

let $t: $T<string>;

// Log to console
const log = (err: Error) => {
  if (process.env.DEV) {
    console.error(err)
  }
}

// Check if error status match the handler
const mustBe = (status: number, error: AxiosError<LocalAxiosResponse, any>) => {
  log(error)
  // @ts-ignore
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
const commonNotify = (callback: Function, err: AxiosError<LocalAxiosResponse, any>, type: string | undefined) => {
  // Notify
  // @ts-ignore
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
const error: ErrorHandler = {
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
    // @ts-ignore
    const data = err.response.data
    // Merge error
    // @ts-ignore
    this.v$[key][data.payload].$server = {
      $error: true,
      $errors: [{
        $validator: 'server',
        $message: data.message // @ts-ignore
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
    // @ts-ignore
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
      // @ts-ignore
      const httpCode = error.response.status

      // Check if cases has case for [httpCode]
      if (typeof cases === 'object' && cases.hasOwnProperty(`$${httpCode}`)) {
        // Get handler of httpCode
        // @ts-ignore
        if (typeof this[`$${httpCode}`] === 'function') {
          // Check if case of httpCode is an array, if not, convert to array
          // @ts-ignore
          const bindArgs = Array.isArray(cases[httpCode]) ? cases[httpCode] : [null, cases[httpCode]]
          // Execute the corresponding http status handler
          // @ts-ignore
          this[`$${httpCode}`].bind(...bindArgs)(error)
          // @ts-ignore
        }
      } else if (typeof cases['default'] === 'function') {
        // @ts-ignore Else if default case is present, execute it
        cases['default'](error)
      }
    }
  },

  log(err) {
    log(err)
  }
}

export default boot(({app}) => {
  $t = app.config.globalProperties.$t
  app.config.globalProperties.$error = error
})

export {error};
