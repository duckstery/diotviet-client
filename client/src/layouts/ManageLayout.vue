<template>
  <q-layout view="hHh Lpr fFf"> <!-- Be sure to play with the Layout demo on docs -->
    <Header>
      <q-tabs
        dense
        no-caps
        inline-label
        mobile-arrows
        class="tw-ml-4"
      >
        <q-route-tab
          v-for="tab in activeTabs"
          exact
          :to="tab.to"
          :icon="`fa-solid ${tab.icon}`"
          :label="$t(`field.${tab.key}`)"
        />
      </q-tabs>
    </Header>

    <q-page-container>
      <router-view/>
    </q-page-container>

    <Footer/>
  </q-layout>
</template>

<script>
import Header from "components/General/Layout/Header.vue";
import Footer from "components/General/Layout/Footer.vue";

export default {
  name: 'ManageLayout',

  components: {Header, Footer},

  data: () => ({
    tabs: {
      'product': [
        {key: 'list', icon: 'fa-grip', to: '/product'}
      ],
      'transaction': [
        {key: 'order', icon: 'fa-inbox', to: '/transaction/order'},
        {key: 'receipt', icon: 'fa-receipt', to: '/transaction/receipt'},
        // {key: 'return', icon: 'fa-angles-left', to: '/transaction/return'}
      ],
      'partner': [
        {key: 'customer', icon: 'fa-user-tag', to: '/partner/customer'},
        {key: 'staff', icon: 'fa-user-tie', to: '/partner/staff'},
        // {key: 'supplier', icon: 'fa-box-open', to: '/partner/supplier'},
        // {key: 'shipper', icon: 'fa-truck-fast', to: '/partner/shipper'}
      ],
    }
  }),

  computed: {
    // The name of route is the key of tabs
    activeTabs() {
      return this.tabs[(this.$route.name ?? '').toLowerCase().split('.')[0]]
    }
  },
}
</script>
