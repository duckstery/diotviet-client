<template>
  <Page>
    <!-- Breadcrumbs -->
    <Breadcrumbs :items="breadcrumbs" ref="breadcrumbs"/>
    <div class="row tw-pt-5" :style="`min-height: ${contentHeight}px`">
      <div class="col-3">

      </div>
      <div class="col-9">
        <DataTable v-model:pagination="paginate" :headers="headers" :items="items" :loading="loading"
                   @request="search"/>
      </div>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import Breadcrumbs from "components/Manage/Breadcrumbs.vue";
import DataTable from "components/Manage/DataTable.vue";

export default {
  name: 'ProductPage',

  components: {Page, DataTable, Breadcrumbs},

  inject: ['globalVars'],

  data: () => ({
    // Filter
    paginate: {
      page: 1,
      rowsPerPage: 10
    },

    // Table
    headers: [],
    items: [],
    title: ''
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
     * @param pagination
     */
    search({pagination}) {
      // Call API to get data for table
      this.$axios.get('/product/search', {
        params: {
          page: pagination.page - 1,
          itemsPerPage: pagination.rowsPerPage
        }
      })
        .then(this.applyItems)
    }
  }
}
</script>
