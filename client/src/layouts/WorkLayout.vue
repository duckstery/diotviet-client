<template>
  <q-layout view="hHh Lpr fFr">
    <Header>
      <TextField v-model="search" icon="fa-solid fa-search" class="tw-w-1/4 tw-max-w-lg tw-ml-4"/>

      <OrderBar/>
    </Header>

    <q-page-container>
      <router-view/>
    </q-page-container>

    <Footer/>
  </q-layout>
</template>

<script>
import Footer from "components/General/Layout/Footer.vue";
import Header from "components/General/Layout/Header.vue";
import OrderBar from "components/General/Layout/OrderBar.vue";
import TextField from "components/General/Other/TextField.vue";

export default {
  name: 'WorkLayout',

  components: {Header, Footer, OrderBar, TextField},

  data: () => ({
    search: '',
  }),
}
</script>
