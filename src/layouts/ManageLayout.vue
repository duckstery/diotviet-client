<template>
  <q-layout view="hHh Lpr fFf"> <!-- Be sure to play with the Layout demo on docs -->
    <Header>
      <template v-if="$q.screen.lt.md">
        <!-- Menu -->
        <Button flat icon="fa-solid fa-list" color="white" class="tw-ml-3" @click="drawer = true"/>
        <MenuDrawer v-model="drawer" :links="activeTabs"/>
      </template>

      <template v-else>
        <MenuTabs :tabs="activeTabs" class="tw-ml-4"/>
      </template>
    </Header>

    <q-page-container>
      <router-view/>
    </q-page-container>

    <Footer/>
  </q-layout>
</template>

<script>
import MenuTabs from "components/Manage/MenuTabs.vue";
import MenuDrawer from "components/Manage/MenuDrawer.vue";
import Button from "components/General/Other/Button.vue";
import Header from "components/General/Layout/Header.vue";
import Footer from "components/General/Layout/Footer.vue";

export default {
  name: 'ManageLayout',

  components: {MenuTabs, MenuDrawer, Button, Header, Footer},

  data: () => ({
    drawer: false,
    tabs: {
      'product': [
        {key: 'list', icon: 'fa-grip', to: '/product'}
      ],
      'transaction': [
        {key: 'order', icon: 'fa-inbox', to: '/transaction/order'},
        {key: 'other', icon: 'fa-arrow-right-arrow-left', to: '/transaction/other'},
        {key: 'history', icon: 'fa-timeline', to: '/transaction/history'},
      ],
      'partner': [
        {key: 'customer', icon: 'fa-user-tag', to: '/partner/customer'},
        {key: 'staff', icon: 'fa-user-tie', to: '/partner/staff'},
        // {key: 'supplier', icon: 'fa-box-open', to: '/partner/supplier'},
        // {key: 'shipper', icon: 'fa-truck-fast', to: '/partner/shipper'}
      ],
      'setting': [
        {key: 'print', icon: 'fa-print', to: '/setting/print'},
      ],
      'report': [
        {key: 'income', icon: 'fa-money-bill-trend-up', to: '/report/income'},
        {key: 'order', icon: 'fa-square-poll-vertical', to: '/report/order'},
        {key: 'rank', icon: 'fa-ranking-star', to: '/report/rank'},
      ]
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
