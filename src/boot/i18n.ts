import {boot} from 'quasar/wrappers'
import {
  createI18n,
  DefineLocaleMessage,
  IsEmptyObject,
  IsNever,
  Locale,
  NamedValue,
  Path,
  PickupPaths,
  RemovedIndexResources,
  TranslateOptions,
  TranslateResult
} from 'vue-i18n'
import messages from 'src/i18n'
import {axios} from 'boot/axios'
import {env} from 'boot/env'

// Set axios Accept-Language header
axios.interceptors.request.use(async (config) => {
  // Set Accept-Language header
  config.headers['Accept-Language'] = await env.get('language')

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

export type $T<
  Key extends string,
  DefinedLocaleMessage extends RemovedIndexResources<DefineLocaleMessage> = RemovedIndexResources<DefineLocaleMessage>,
  Keys = IsEmptyObject<DefinedLocaleMessage> extends false
    ? PickupPaths<{
      [K in keyof DefinedLocaleMessage]: DefinedLocaleMessage[K]
    }>
    : never,
  ResourceKeys extends Keys = IsNever<Keys> extends false ? Keys : never
> = ((
  key: Key | ResourceKeys | Path
) => TranslateResult) & ((
  key: Key | ResourceKeys | Path,
  locale: Locale,
  list: unknown[]
) => TranslateResult) & ((
  key: Key | ResourceKeys | Path,
  locale: Locale,
  named: object
) => TranslateResult) & ((
  key: Key | ResourceKeys | Path,
  list: unknown[]
) => TranslateResult) & ((
  key: Key | ResourceKeys | Path,
  named: Record<string, unknown>
) => TranslateResult) & ((
  key: Key | ResourceKeys | Path
) => string) & ((
  key: Key | ResourceKeys | Path,
  plural: number
) => string) & ((
  key: Key | ResourceKeys | Path,
  plural: number,
  options: TranslateOptions
) => string) & ((
  key: Key | ResourceKeys | Path,
  defaultMsg: string
) => string) & ((
  key: Key | ResourceKeys | Path,
  defaultMsg: string,
  options: TranslateOptions
) => string) & ((
  key: Key | ResourceKeys | Path,
  list: unknown[]
) => string) & ((
  key: Key | ResourceKeys | Path,
  list: unknown[],
  plural: number
) => string) & ((
  key: Key | ResourceKeys | Path,
  list: unknown[],
  defaultMsg: string
) => string) & ((
  key: Key | ResourceKeys | Path,
  list: unknown[],
  options: TranslateOptions
) => string) & ((
  key: Key | ResourceKeys | Path,
  named: NamedValue
) => string) & ((
  key: Key | ResourceKeys | Path,
  named: NamedValue,
  plural: number
) => string) & ((
  key: Key | ResourceKeys | Path,
  named: NamedValue,
  defaultMsg: string
) => string) & ((
  key: Key | ResourceKeys | Path,
  named: NamedValue,
  options: TranslateOptions
) => string)
