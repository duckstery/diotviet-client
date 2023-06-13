<template>
  <Page :breadcrumbs="breadcrumbs">

  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";

export default {
  name: 'StaffPage',
  components: {Page},

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
