import {computed, ComputedRef, nextTick, onUnmounted, shallowReactive, toRaw, watch} from "vue";
import {Ref, ShallowReactive} from "@vue/reactivity";

// *************************************************
// Typed
// *************************************************

export type Entry = {
  time: string,
  date: string,
  yearMonth: string,
  reason: string,
  order: { id: number, code: string, customerCode: string, customerName: string },
  amount: number,
  isCollect: boolean
}

export type EntryGroup = {
  date: string,
  yearMonth: string,
  heading?: string,
  entries: Entry[],
  collectedAmount: number,
  spentAmount: number,
  isEstimating: boolean
}

export type PagedTimeline = {
  pages: Entry[][],
  last: boolean,
  current: number
}

export type TimelineSlice = {
  new: boolean,
  content: Entry[],
  pageable: {
    pageNumber: number,
    pageSize: number,
    sort: {
      empty: boolean,
      sorted: boolean,
      unsorted: boolean
    },
    offset: number,
    paged: boolean,
    unpaged: boolean
  },
  size: number,
  number: number,
  sort: {
    empty: boolean,
    sorted: boolean,
    unsorted: boolean
  },
  first: boolean,
  last: boolean,
  numberOfElements: number,
  empty: boolean
}

export type UseTimelineResources = {
  timeline: ShallowReactive<PagedTimeline>,
  entriesByDate: ComputedRef<EntryGroup[]>,
  flush: () => void
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup Timeline
 *
 * @param dataRef
 */
export function useTimeline(dataRef: Ref<TimelineSlice>): UseTimelineResources {
  // Local timeline data
  const timeline: ShallowReactive<PagedTimeline> = shallowReactive({
    pages: [],
    last: false,
    current: -1
  })
  // Watch for value changed
  watch(dataRef, (value) => {
    // Check if this is new raw value
    if (value.new) {
      // Flush timeline
      flush()
    } else {
      // Push new slice (page) to timeline
      timeline.pages.push({})
    }

    nextTick(() => {
      // Append all entry at page [value.number], a
      timeline.pages[value.number] = [...toRaw(value.content)]
      // Update last page flag
      timeline.last = value.last
      // Update current page
      timeline.current = value.number
    })
  })

  // Group entries
  const entriesByDate: ComputedRef<EntryGroup[]> = computed(() => {
    // Index map
    const indexByDate: Map<string, number> = new Map()
    // Create output
    const groups: EntryGroup[] = []
    // Current yearMonth
    let yearMonth = ''

    // Iterate through each page
    timeline.current >= 0 && timeline.pages.forEach(page => {
      // Iterate through each entry in page
      page.forEach(entry => {
        // Check if entry's group (determine by date) is not existing
        if (!indexByDate.has(entry.date)) {
          // IsHeading flag
          let heading = false
          // Check if entry is also a heading
          if (yearMonth !== entry.yearMonth) {
            // Mark this yearMonth
            yearMonth = entry.yearMonth
            // Add yearMonth heading
            heading = true
          }

          // Add new group to output
          let newIndex = groups.push({
            yearMonth: entry.yearMonth,
            date: entry.date,
            entries: [],
            collectedAmount: 0,
            spentAmount: 0,
            isEstimating: false,
            heading: heading
          }) - 1
          // Set to index map
          indexByDate.set(entry.date, newIndex)
        }

        // Get the index of group that entry belongs to
        let index = indexByDate.get(entry.date)
        // Push entry to group's entries
        groups[index].entries.push(entry)
        // Apply amount
        groups[index][entry.isCollect ? 'collectedAmount' : 'spentAmount'] += entry.amount
      })
    })

    // Mark last group as estimating
    if (groups.length > 0 && !timeline.last) {
      groups[groups.length - 1].isEstimating = true
    }

    return groups
  })

  // Flush all data
  const flush = () => {
    timeline.pages = []
    timeline.last = false
    timeline.current = -1
  }
  // Flush all on destroyed
  onUnmounted(flush)

  return {
    timeline: timeline,
    entriesByDate: entriesByDate,
    flush: flush
  }
}
