import {boot} from 'quasar/wrappers'
import {Notify} from 'quasar'
import {util} from 'boot/util'

// *************************************************
// Typed
// *************************************************

/**
 * Show snackbar to notify user
 */
export type Notifier = (content: string, type?: 'positive' | 'negative' | 'warning', err?: Error) => void

// *************************************************
// Implementation
// *************************************************

/**
 * Create a snackbar to notify user
 *
 * @param {string} content
 * @param {string} type
 * @param {Error} err
 */
const notify: Notifier = (content: string, type: 'positive' | 'negative' | 'warning' = 'positive', err: Error | null = null) => {
  if (process.env.DEV) {
    if (type === 'warning') {
      console.warn(content)
    } else if (type === 'negative') {
      console.error(content)
    }

    if (!util.isUnset(err)) console.error(err)
  }

  return Notify.create({
    type: type,
    message: content,
    position: 'top-right',
    textColor: 'white',
    actions: [
      {
        icon: 'close', color: 'white', handler: () => { /* ... */
        }
      }
    ]
  })
}

export default boot(({app}) => {
  app.config.globalProperties.$notify = notify
  app.config.globalProperties.$notifyWarn = (content) => notify(content, 'warning')
  app.config.globalProperties.$notifyErr = (content) => notify(content, 'negative')
})

export {notify}
