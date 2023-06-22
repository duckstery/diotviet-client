<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-3 tw-pr-3">
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.product') }}</div>

      <!-- Filter -->
      <ProductFilter v-model="filter" :categories="categories" :groups="groups"
                     @request="onSearch" @control="onGroupControl"/>
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
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import Breadcrumbs from "components/Manage/Breadcrumbs.vue";
import DataTable from "components/Manage/DataTable.vue";
import ProductFilter from "components/Manage/Product/ProductFilter.vue";
import ProductDetail from "components/Manage/Product/ProductDetail.vue";
import ProductEditor from "components/Manage/Product/ProductEditor.vue";

import {usePageSearch} from "src/composables/usePageSearch"
import {usePageRequest} from "src/composables/usePageRequest"
import {ref} from "vue";
import CustomerFilter from "components/Manage/Partner/Customer/CustomerFilter.vue";
import {useGroupControl} from "src/composables/useGroupControl";

export default {
  name: 'ProductPage',

  components: {CustomerFilter, ProductDetail, ProductFilter, Page, DataTable, Breadcrumbs},

  setup() {
    // Loading flag
    const loading = ref(false)
    // Page search functionality
    const pageSearch = usePageSearch({
      categories: [],
      group: null,
      minPrice: '',
      maxPrice: '',
      canBeAccumulated: null,
      isInBusiness: null
    })
    // Page request functionality
    const pageRequest = usePageRequest(
      ProductEditor,
      () => ({categories: pageSearch.categories.value, groups: pageSearch.groups.value}),
      pageSearch.searchWithPreviousData
    )
    // Group control
    const groupControl = useGroupControl(pageSearch.groups)

    return {
      loading,
      ...pageSearch,
      ...pageRequest,
      ...groupControl
    }
  },


  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.product'), to: '/product', icon: 'fa-box'},
        {label: this.$t('field.list'), to: '/product', icon: 'fa-grip'},
      ]
    },
  },
}
</script>
