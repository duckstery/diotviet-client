import {ref, onMounted} from 'vue'
import {axios, util} from "src/boot";
import {useRouteKey} from "src/composables/useRouteKey";
import {Ref} from "@vue/reactivity";

// *************************************************
// Typed
// *************************************************

export type QTablePagination = {
  sortBy?: string,
  descending?: boolean,
  page?: number,
  rowsPerPage?: number,
  rowsNumber?: number
}

export type QTableOnSearch = {
  search?: string,
  pagination?: QTablePagination,
  filter?: string | any,
  getCellValue?: (col: any, row: any) => any
}

export type UsePageSearchResources<T> = {
  categories: Ref<any[]>,
  groups: Ref<any[]>,
  headers: Ref<any[]>,
  items: Ref<any[]>,
  pagination: Ref<QTablePagination>,
  previousSearch: Ref<string>,
  filter: Ref<T>,
  onSearch(data: QTableOnSearch): void,
  searchWithPreviousData(): void
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup page search for pages of type Manage
 *
 * @param {object} initFilter
 * @returns {object}
 */
export function usePageSearch<T>(initFilter: T): UsePageSearchResources<T> {
  // Get key
  const key = useRouteKey()
  // General
  const categories = ref([])
  const groups = ref([])
  // Table
  const headers = ref([])
  const items = ref([])
  // Paging
  const pagination: Ref<QTablePagination> = ref({
    page: 1,
    rowsPerPage: 10
  })
  const previousSearch = ref('')
  // Filter
  const filter: Ref = ref(initFilter)

  // On mounted, call API to get data for table
  onMounted(() =>
    axios.get(`/${key}/index`)
      .then(res => {
        applyCommon(res)
        applyItems(res)
      })
  )

  /**
   * On search
   *
   * @param data
   */
  const onSearch = (data: QTableOnSearch) => {
    // Save search content
    previousSearch.value = data ? data.search : ''
    // Call API to get data for table
    axios.get(`/${key}/search`, {
      params: {
        // @ts-ignore
        ...filter.value,
        search: previousSearch.value,
        page: data ? data.pagination.page - 1 : 0,
        itemsPerPage: data ? data.pagination.rowsPerPage : pagination.value.rowsPerPage
      }
    })
      .then(applyItems)
  }

  /**
   * Search with previous data and pagination
   */
  const searchWithPreviousData = () => {
    onSearch({
      search: previousSearch.value,
      pagination: pagination.value
    })
  }

  // Apply common data
  const applyCommon = (res) => {
    // Table's headers
    headers.value = res.data.payload.headers
    // Add formatter for actualPrice
    if (Array.isArray(headers.value)) {
      headers.value.forEach(header => {
        if (header.name === 'actualPrice') {
          header["format"] = util.formatMoney
        }
      })
    }
    // Categories
    categories.value = res.data.payload.categories
    // Groups
    groups.value = res.data.payload.groups
  }

  // Apply items data
  const applyItems = (res) => {
    // Table's items
    items.value = res.data.payload.items.content
    // Pageable
    pagination.value = {
      page: res.data.payload.items.number + 1,
      rowsPerPage: res.data.payload.items.size,
      rowsNumber: res.data.payload.items.totalElements
    }
  }

  return {
    // Data
    categories,
    groups,
    headers,
    items,
    pagination,
    previousSearch,
    filter,
    // Methods
    onSearch,
    searchWithPreviousData
  }
}
