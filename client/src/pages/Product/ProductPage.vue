<template>
  <Page>
    <!-- Breadcrumbs -->
    <Breadcrumbs :items="breadcrumbs" ref="breadcrumbs"/>
    <div class="row tw-pt-5" :style="`min-height: ${contentHeight}px`">
      <div class="col-3 tw-pr-3">
        <!-- Title -->
        <div class="tw-text-3xl tw-font-semibold">{{ $t('field.product') }}</div>

        <!-- Filter -->
        <ProductFilter v-model="filter" :categories="categories" :groups="groups" @request="search"/>
      </div>
      <div class="col-9">
        <!-- Data table -->
        <DataTable v-model:pagination="paginate" :headers="headers" :items="items" :loading="loading"
                   @request="search">
          <template #default="props">
            {{props.cols}}
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

export default {
  name: 'ProductPage',

  components: {ProductFilter, Page, DataTable, Breadcrumbs},

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
    paginate: {
      page: 1,
      rowsPerPage: 10
    },

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
      this.paginate = {
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
     * On request new page data
     *
     * @param data
     */
    search(data) {
      // Call API to get data for table
      this.$axios.get('/product/search', {
        params: {
          ...this.filter,
          page: data ? data.pagination.page - 1 : 0,
          itemsPerPage: data ? data.pagination.rowsPerPage : this.paginate.rowsPerPage
        }
      })
        .then(this.applyItems)
    },
  }
}
</script>
