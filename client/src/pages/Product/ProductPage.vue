<template>
  <Page>
    <!-- Breadcrumbs -->
    <Breadcrumbs :items="breadcrumbs" ref="breadcrumbs"/>
    <div class="row tw-pt-5" :style="`min-height: ${contentHeight}px`">
      <div class="col-3 tw-pr-3">
        <!-- Title -->
        <div class="tw-text-3xl tw-font-semibold">{{ $t('field.product') }}</div>

        <!-- Filter -->
        <ProductFilter v-model="filter" :categories="categories" :groups="groups" @request="onSearch"/>
      </div>
      <div class="col-9">
        <!-- Data table -->
        <DataTable v-model:pagination="pagination" :headers="headers" :items="items" :loading="loading"
                   @search="onSearch" @request="onRequest">
          <template #default="props">
            <ProductDetail v-bind="props" @request="onRequest"/>
          </template>
        </DataTable>
      </div>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import Breadcrumbs from "components/Manage/Breadcrumbs.vue";
import DataTable from "components/Manage/DataTable.vue";
import ProductFilter from "components/Manage/Product/ProductFilter.vue";
import ProductDetail from "components/Manage/Product/ProductDetail.vue";
import ProductEditor from "components/Manage/Product/ProductEditor.vue";
import {saveAs} from 'file-saver'
import {date} from "quasar";


export default {
  name: 'ProductPage',

  components: {ProductDetail, ProductFilter, Page, DataTable, Breadcrumbs},

  inject: ['globalVars'],

  data: () => ({
    // General
    categories: [],
    groups: [],

    // Filter
    filter: {
      categories: [],
      group: null,
      minPrice: '',
      maxPrice: '',
      canBeAccumulated: null,
      isInBusiness: null
    },

    // Table
    headers: [],
    items: [],
    title: '',

    // Paging
    pagination: {
      page: 1,
      rowsPerPage: 10
    },
    previousSearch: '',

    // Loading flag
    loading: false
  }),

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.product'), to: '/product', icon: 'fa-box'},
        {label: this.$t('field.list'), to: '/product', icon: 'fa-grip'},
      ]
    },
    // Remaining space below breadcrumbs
    contentHeight() {
      // Calculate content height
      const contentHeight = this.globalVars.usableHeight - this.$refs.breadcrumbs?.$el.clientHeight
      return isNaN(contentHeight)
        ? this.globalVars.usableHeight - 30
        : contentHeight
    },
    // Get previous search data
    getPreviousSearchData() {
      return {
        search: this.previousSearch,
        pagination: this.pagination
      }
    }
  },

  mounted() {
    // Initiate everything
    this.index()
  },

  methods: {
    /**
     * Apply common data
     */
    applyCommon(res) {
      // Table's headers
      this.headers = res.data.payload.headers
      // Add formatter for actualPricek
      if (Array.isArray(this.headers)) {
        this.headers.forEach(header => {
          if (header.name === 'actualPrice') {
            header["format"] = this.$util.formatMoney
          }
        })
      }
      // Categories
      this.categories = res.data.payload.categories
      // Groups
      this.groups = res.data.payload.groups
    },

    /**
     * Apply items data
     *
     * @param res
     */
    applyItems(res) {
      // Table's items
      this.items = res.data.payload.items.content
      // Pageable
      this.pagination = {
        page: res.data.payload.items.number + 1,
        rowsPerPage: res.data.payload.items.size,
        rowsNumber: res.data.payload.items.totalElements
      }
    },

    /**
     * Initiate load
     */
    index() {
      // Call API to get data for table
      this.$axios.get('/product/index')
        .then(res => {
          this.applyCommon(res)
          this.applyItems(res)
        })
    },

    /**
     * On search
     *
     * @param data
     */
    onSearch(data) {
      // Save search content
      this.previousSearch = data ? data.search : ''
      // Call API to get data for table
      this.$axios.get('/product/search', {
        params: {
          ...this.filter,
          search: this.previousSearch,
          page: data ? data.pagination.page - 1 : 0,
          itemsPerPage: data ? data.pagination.rowsPerPage : this.pagination.rowsPerPage
        }
      })
        .then(this.applyItems)
    },

    /**
     * On request an operation
     *
     * @param {string} mode
     * @param {*} item
     */
    onRequest(mode, item = null) {
      if (['create', 'update', 'copy'].includes(mode)) {
        // Need to be interacted before sending request
        this.onInteractiveRequest(mode, item)
      } else if (['legacy', 'import'].includes(mode)) {
        // Need to send file to server
        this.$util.promptConfirm(this)
          .onOk(() => this.onImportRequest(mode, item))
      } else if (['export'].includes(mode)) {
        // Need to send file to server
        this.$util.promptConfirm(this)
          .onOk(() => this.onExportRequest())
      } else {
        // Directly send to server
        this.$util.promptConfirm(this)
          .onOk(() => this.onDirectRequest(mode, item))
      }
    },

    /**
     * On send file request
     *
     * @param {string} mode
     * @param {File} item
     */
    onImportRequest(mode, item) {
      // Craft formData
      const formData = this.$util.craftFormData({file: item})

      // Send request
      this.$axios.post((mode === 'legacy' ? 'legacy' : '') + '/product/import', formData, {headers: {"Content-Type": "multipart/form-data"}})
        .then(this.onSuccessOperation)
        .catch(this.$error.$422.bind(this, 'input'))
    },

    /**
     * On export file request
     */
    onExportRequest() {
      // Send request
      this.$axios.get('/product/export', {responseType: "blob"})
        .then(res => saveAs(res.data, `Products_${date.formatDate(Date.now(), 'YYMMDD')}.csv`))
    },

    /**
     * On direct request (non-interactive)
     *
     * @param {string} mode
     * @param {array} item
     */
    onDirectRequest(mode, item) {
      let api, option, target

      if (mode === 'delete') {
        api = 'delete'
      } else {
        // Split mode by _
        [option, target] = mode.split('_')
        // Convert option to boolean
        option = option === 'start'
        api = 'patch'
      }

      // Send request
      this.$axios[api](`/product/${api}`, {ids: item, target, option})
        .then(this.onSuccessOperation)
        .catch(() => this.$notifyErr(this.$t('message.fail', {attr: this.$t('field.operation')})))
    },

    /**
     * On interactive request like add, edit, copy
     *
     * @param {string} mode
     * @param {*} item
     */
    onInteractiveRequest(mode, item) {
      // Create props so item("null") won't override Editor default value
      const componentProps = {mode, categories: this.categories, groups: this.groups}

      // Add item
      if (item !== null) {
        componentProps.item = {
          ...item,
          id: mode === 'copy' ? null : item.id,
          code: mode === 'copy' ? null : item.code,
          category: item.categoryId,
          groups: item.groupIds
        }
      }

      // Invoke dialog
      this.$q.dialog({
        component: ProductEditor,
        componentProps: componentProps
      }).onOk((data) => {
        this.onSearch(this.getPreviousSearchData)
      }).onCancel(() => {

      }).onDismiss(() => {

      })
    },

    /**
     * On any operation success
     */
    onSuccessOperation() {
      this.$notify(this.$t('message.success', {attr: this.$t('field.operation')}))
      this.onSearch(this.getPreviousSearchData)
    }
  }
}
</script>
