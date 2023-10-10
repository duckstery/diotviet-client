<template>
  <Page :breadcrumbs="breadcrumbs" :split="[2, 10]">
    <template #left>
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.customer') }}</div>

      <!-- Filter -->
      <CustomerFilter v-model="filter" :categories="categories" :groups="groups"
                      @request="onSearch" @control="onGroupControl"/>
    </template>
    <template #right>
      <!-- Data table -->
      <DataTable v-model:pagination="pagination" legacy
                 :headers="headers" :items="items" :loading="loading"
                 @search="onSearch" @request="onRequest">
        <template #default="props">
          <CustomerDetail v-bind="props" @request="onRequest"/>
        </template>
      </DataTable>
    </template>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import DataTable from "components/Manage/DataTable.vue";
import CustomerFilter from "components/Manage/Partner/Customer/CustomerFilter.vue";
import CustomerDetail from "components/Manage/Partner/Customer/CustomerDetail.vue";
import CustomerEditor from "components/Manage/Partner/Customer/CustomerEditor.vue";

import {ref} from "vue";
import {usePageSearch} from "src/composables/usePageSearch";
import {usePageRequest} from "src/composables/usePageRequest";
import {useGroupControl} from "src/composables/useGroupControl";

export default {
  name: 'CustomerPage',

  components: {CustomerDetail, CustomerFilter, Page, DataTable},

  setup() {
    // Loading flag
    const loading = ref(false)
    // Page search functionality
    const pageSearch = usePageSearch({
      group: null,
      createAtFrom: null,
      createAtTo: null,
      birthdayFrom: null,
      birthdayTo: null,
      lastTransactionAtFrom: null,
      lastTransactionAtTo: null,
      isMale: null
    })
    // Page request functionality
    const pageRequest = usePageRequest(
      CustomerEditor,
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
        {label: this.$t('field.partner'), to: '/partner', icon: 'fa-handshake'},
        {label: this.$t('field.customer'), to: '/partner/customer', icon: 'fa-user-tag'},
      ]
    },
  },
}
</script>
