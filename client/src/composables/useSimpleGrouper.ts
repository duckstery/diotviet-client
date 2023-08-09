import {util} from "src/boot"

/**
 * Setup simple list filter
 *
 * @param {string} field
 * @param {boolean} sortAsc
 * @param {function} parser Use to parse value of 'key' with some specific logic
 * @param {function} cutter Use to cut value of 'key' with some specific logic
 * @return {function}
 */
export function useSimpleGrouper(
  field: string,
  sortAsc: boolean = true,
  parser: (code: string) => string,
  cutter: (code: string) => string
) {
  return (value: any) => {
    // Cache
    const entries: { [key: string]: any } = {}

    // Iterate through each item
    for (const item of value) {
      // Get target field for filter
      const target = typeof cutter === 'function' ? cutter(item[field]) : item[field]

      // Check if target has an entry in entries
      if (util.isUnset(entries[target])) {
        // If not, create an entry for target and push item to that entry
        entries[target] = []
      }
      // Push item to that entry
      entries[target].push({...item})
    }

    // Spread entries, so then, we will have a label with all item that matched the label criteria
    return Object
      .entries(entries)
      .sort((a, b) => util.compare(a[0], b[0]) * (sortAsc ? 1 : -1))
      // @ts-ignore
      .map(([key, item]) => [{isLabel: true, label: typeof parser === 'function' ? parser(key) : key}, ...item])
      .flat()
  }
}
