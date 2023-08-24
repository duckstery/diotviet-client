import {LocalStorage} from "quasar";

// *************************************************
// Typed
// *************************************************

export type AdvanceStorage = LocalStorage & {
  /**
   * Set data strictly in storage by key
   * @param key
   * @param value
   */
  setStrict(
    key: string,
    value: | Date | RegExp | number | boolean | ((...params: readonly any[]) => any) | any | readonly any[] | string | null,
  ): void

  /**
   * Get data strictly from storage by key
   *
   * @param key
   */
  getStrict(key: string): | Date | RegExp | number | boolean | ((...params: readonly any[]) => any) | any | readonly any[] | string | null;
}

// *************************************************
// Implementation
// *************************************************

export const AdvanceStorage: AdvanceStorage = {
  ...LocalStorage,

  /**
   * Set data in storage by key
   *
   * @param key
   * @param data
   * @param strict
   */
  setStrict(key: string, data: any, strict: boolean = true) {
    LocalStorage.set(key, data)
  },

  getStrict(key: string): | Date | RegExp | number | boolean | ((...params: readonly any[]) => any) | any | readonly any[] | string | null  {
    return {}
  }
}
