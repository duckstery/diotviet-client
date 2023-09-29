<template>
  <q-layout view="hHh Lpr fFr">
    <Header>
      <SampleQuery/>

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
import SampleQuery from "components/Work/SampleQuery.vue";

export default {
  name: 'WorkLayout',

  components: {SampleQuery, Header, Footer, OrderBar, TextField},

  mounted() {
    console.warn(this.$q.platform)
    // Block device with screen size smaller than [md] except desktop
    if (this.$q.screen.lt.md && !this.$q.platform.is.desktop) {
      this.$router.push({name: 'Error', params: {status: 403}, query: {error: 'forbidden_device'}})
    }
  }
}
</script>
