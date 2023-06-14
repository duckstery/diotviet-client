<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-3 tw-pr-3">
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.customer') }}</div>

      <!-- Filter -->
      <CustomerFilter v-model="filter" :categories="categories" :groups="groups" @request="onSearch"/>
    </div>
    <div class="col-9">
      <!-- Data table -->
      <DataTable v-model:pagination="pagination" :headers="headers" :items="items" :loading="loading"
                 @search="onSearch" @request="onRequest">
        <template #default="props">
          <CustomerDetail v-bind="props" @request="onRequest"/>
        </template>
      </DataTable>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import Breadcrumbs from "components/Manage/Breadcrumbs.vue";
import DataTable from "components/Manage/DataTable.vue";
import CustomerFilter from "components/Manage/Partner/Customer/CustomerFilter.vue";
import CustomerDetail from "components/Manage/Partner/Customer/CustomerDetail.vue";
import CustomerEditor from "components/Manage/Partner/Customer/CustomerEditor.vue";
import {saveAs} from 'file-saver'
import {date} from "quasar";
import {ref} from "vue";
import {usePageSearch} from "src/composables/usePageSearch";
import {usePageRequest} from "src/composables/usePageRequest";
import ProductEditor from "components/Manage/Product/ProductEditor.vue";

export default {
  name: 'CustomerPage',

  components: {CustomerDetail, CustomerFilter, Page, DataTable, Breadcrumbs},

  setup() {
    // Loading flag
    const loading = ref(false)
    // Page search functionality
    const pageSearch = usePageSearch(
      'customer',
      {
        group: null,
        createAtFrom: null,
        createAtTo: null,
        birthdayFrom: null,
        birthdayTo: null,
        lastTransactionAtFrom: null,
        lastTransactionAtTo: null,
        isMale: null
      }
    )
    // Page request functionality
    const pageRequest = usePageRequest(
      'customer',
      ProductEditor,
      () => ({categories: pageSearch.categories.value, groups: pageSearch.groups.value}),
      pageSearch.searchWithPreviousData
    )

    return {
      loading,
      ...pageSearch,
      ...pageRequest
    }
  },
}
</script>
