import {util} from "src/boot"

/**
 * Setup simple list filter
 *
 * @param {string} field
 * @param {string} sortAsc
 * @param {function} parser Use to parse value of 'key' with some specific logic
 * @param {function} cutter Use to cut value of 'key' with some specific logic
 * @return {function}
 */
export function useSimpleGrouper(field, sortAsc = true, parser = null, cutter = null) {
  return (value) => {
    // Cache
    const entries = {}

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
      .map(([key, item]) => [{isLabel: true, label: typeof parser === 'function' ? parser(key) : key}, ...item])
      .flat()
  }
}