import { boot } from 'quasar/wrappers'
import { Notify } from 'quasar'
import axios from "boot/axios";

/**
 * Create a snackbar to notify user
 *
 * @param {string} content
 * @param {string} type
 */
const notify = (content, type = 'positive') => {
  return Notify.create({
    type: type,
    message: content,
    position: 'top-right',
    actions: [
      { icon: 'close', color: 'white', handler: () => { /* ... */ } }
    ]
  })
}

export default boot(({ app }) => {
  app.config.globalProperties.$notify = notify
})

export { notify }
