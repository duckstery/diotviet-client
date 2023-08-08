import {boot} from 'quasar/wrappers'
import {createI18n} from 'vue-i18n'
import messages from 'src/i18n'
import {axios} from 'boot/axios'
import {env} from 'boot/env'

export type MessageLanguages = keyof typeof messages;
// Type-define 'en-US' as the master schema for the resource
export type MessageSchema = typeof messages['en'];

// See https://vue-i18n.intlify.dev/guide/advanced/typescript.html#global-resource-schema-type-definition
/* eslint-disable @typescript-eslint/no-empty-interface */
declare module 'vue-i18n' {
  // define the locale messages schema
  export interface DefineLocaleMessage extends MessageSchema {}

  // define the datetime format schema
  export interface DefineDateTimeFormat {}

  // define the number format schema
  export interface DefineNumberFormat {}
}

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
    // @ts-ignore
    messages
  })

  // Set i18n instance on app
  app.use(i18n)
})
