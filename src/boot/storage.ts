import {LocalStorage} from "quasar";
import {createMD5} from "hash-wasm";


// *************************************************
// Typed
// *************************************************

export type AdvanceStorage = LocalStorage & {
  /**
   * Check if data of key is still valid
   *
   * @param key
   */
  isValid(key: string): boolean

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

// Create an increasing MD5 hash-er
const md5 = (await createMD5()).init().update("{\"pass\":\"aDBLqfkwVtVZpf9kFAwQt_4VvyY39nM6\",\"data\":\"")
// Save state
const state = md5.save()

export const AdvanceStorage: AdvanceStorage = {
  ...LocalStorage,

  /**
   * Check if data of key is still valid
   *
   * @param key
   */
  isValid(key: string): boolean {
    try {
      // Get data integrity string (hash) in LocalStorage
      const integrityString = this.getItem(`${key}_hash`)
      // Get data in LocalStorage
      const data = this.getItem(key)
      // Hash the data
      const hash = md5.load(state).update(`${JSON.stringify(data)}"}`).digest()

      // Check for data integrity
      return integrityString === hash
    } catch {
      return false
    }
  },

  /**
   * Set data strictly in storage by key
   *
   * @param key
   * @param data
   * @param strict
   */
  setStrict(key: string, data: any): void {
    // Hash data
    const hash = md5.load(state).update(`${JSON.stringify(data)}"}`).digest()
    // Save the hash to check for data integrity
    this.set(`${key}_hash`, hash)
    // Save data
    this.set(key, data)

  },

  /**
   * Get data strictly in storage by key
   *
   * @param key
   */
  getStrict(key: string): | Date | RegExp | number | boolean | ((...params: readonly any[]) => any) | any | readonly any[] | string | null {
    // Check if key's data is valid
    if (this.isValid(key)) {
      return this.getItem(key)
    }

    // Remove data if it isn't valid
    this.remove(key)
    this.remove(`${key}_hash`)

    return undefined
  }
}
