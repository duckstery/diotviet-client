<template>
  <Page :breadcrumbs="breadcrumbs" :split="[2, 10]">
    <template #left>
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.customer') }}</div>

      <!-- Filter -->
      <StaffFilter v-model="filter" :categories="categories" :groups="groups"
                      @request="onSearch" @control="onGroupControl"/>
    </template>
    <template #right>
      <!-- Data table -->
      <DataTable v-model:pagination="pagination" :headers="headers" :items="items" :loading="loading"
                 @search="onSearch" @request="onRequest">
        <template #default="props">
          <StaffDetail v-bind="props" @request="onRequest"/>
        </template>
      </DataTable>
    </template>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import DataTable from "components/Manage/DataTable.vue";
import StaffFilter from "components/Manage/Partner/Staff/StaffFilter.vue";
import StaffDetail from "components/Manage/Partner/Staff/StaffDetail.vue";
import {ref} from "vue";
import {usePageSearch} from "src/composables/usePageSearch";
import {usePageRequest} from "src/composables/usePageRequest";
import StaffEditor from "components/Manage/Partner/Staff/StaffEditor.vue";
import {useGroupControl} from "src/composables/useGroupControl";

export default {
  name: 'StaffPage',

  components: {StaffDetail, StaffFilter, DataTable, Page},

  setup() {
    // Loading flag
    const loading = ref(false)
    // Page search functionality
    const pageSearch = usePageSearch({
      createAtFrom: null,
      createAtTo: null,
      birthdayFrom: null,
      birthdayTo: null,
      isMale: null,
      isDeactivated: null,
      roles: []
    })
    // Page request functionality
    const pageRequest = usePageRequest(
      StaffEditor,
      () => ({groups: pageSearch.groups.value}),
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
        {label: this.$t('field.staff'), to: '/partner/staff', icon: 'fa-user-tie'},
      ]
    },
  }
}
</script>
