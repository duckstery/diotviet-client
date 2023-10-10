import {computed, ComputedRef, nextTick, onUnmounted, ref, shallowReactive, toRaw, watch} from "vue";
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

export type EntryGroupAlias = { key: string, label: string }

export type EntryGroup = EntryGroupAlias & { entries: Entry[] }

export type EntryByDate = EntryGroup & {
  icon: string,
  collectedAmount: number,
  spentAmount: number,
  isEstimating: boolean
}

export type EntryByYearMonth = EntryGroupAlias & { groups: EntryByDate[] }

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
  entriesByYearMonth: ComputedRef<EntryByYearMonth[]>,
  setPrintYearMonth: (callback: string) => void,
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
  // Print year month
  const printYearMonth = ref((value) => value)
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

  // Group entries by yearMonth
  const entriesByYearMonth: ComputedRef<EntryByYearMonth[]> = computed(() => {
    // Create output
    const entriesByYearMonth: EntryByYearMonth[] = []
    // Current yearMonth
    let yearMonthGroup: EntryByYearMonth = {key: null}
    // Current date
    let dateGroup: EntryByDate = {key: null}

    // Iterate through each page
    timeline.current >= 0 && timeline.pages.forEach(page => {
      // Iterate through each entry in page
      page.forEach(entry => {
        // Check if entry.yearMonth is a new Group
        if (yearMonthGroup.key !== entry.yearMonth) {
          // Get label
          const label = typeof printYearMonth.value === 'function' ? printYearMonth.value(entry.yearMonth) : entry.yearMonth
          // Create new yearMonthGroup
          yearMonthGroup = {key: entry.yearMonth, label: label, groups: []}
          // Add yearMonthGroup to output
          entriesByYearMonth.push(yearMonthGroup)
        }

        // Check if entry.date is a new group
        if (dateGroup.key !== entry.date) {
          // Create new dateGroup
          dateGroup = {
            icon: 'fa-solid fa-calendar-day',
            key: entry.date,
            label: entry.date,
            collectedAmount: 0,
            spentAmount: 0,
            isEstimating: false,
            entries: []
          }
          // Add dateGroup to yearMonthGroup
          yearMonthGroup.groups.push(dateGroup)
        }

        // Add entry to dateGroup
        dateGroup.entries.push(entry)
        // Apply amount
        dateGroup[entry.isCollect ? 'collectedAmount' : 'spentAmount'] += entry.amount
      })
    })

    // Mark last group as estimating
    dateGroup.isEstimating = !timeline.last

    return entriesByYearMonth
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
    entriesByYearMonth: entriesByYearMonth,
    setPrintYearMonth: (callback) => printYearMonth.value = callback,
    flush: flush
  }
}
