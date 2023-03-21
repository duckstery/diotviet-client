import { boot } from 'quasar/wrappers'
import { Notify } from 'quasar'
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
    textColor: 'white',
    actions: [
      { icon: 'close', color: 'white', handler: () => { /* ... */ } }
    ]
  })
}

export default boot(({ app }) => {
  app.config.globalProperties.$notify = notify
  app.config.globalProperties.$notifyWarn = (content) => notify(content, 'warning')
  app.config.globalProperties.$notifyErr = (content) => notify(content, 'negative')
})

export { notify }
