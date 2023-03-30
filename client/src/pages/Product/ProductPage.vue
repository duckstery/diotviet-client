<template>
  <Page>
    <!-- Breadcrumbs -->
    <Breadcrumbs :items="breadcrumbs" ref="breadcrumbs"/>
    <div class="row tw-pt-5" :style="`min-height: ${contentHeight}px`">
      <div class="col-3">

      </div>
      <div class="col-9">
        <DataTable/>
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
}
</script>
