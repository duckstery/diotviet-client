import {boot} from 'quasar/wrappers'
import {createI18n} from 'vue-i18n'
import messages from 'src/i18n'
import {axios} from 'boot/axios'
import {env} from 'boot/env'

// Set axios Accept-Language header
axios.interceptors.request.use((config) => {
  // Set Accept-Language header
  config.headers['Accept-Language'] = env.get('language')

  return config
})

export default boot(({app}) => {
  const i18n = createI18n({
    locale: 'en',
    globalInjection: true,
    messages
  })

  // Set i18n instance on app
  app.use(i18n)
})
