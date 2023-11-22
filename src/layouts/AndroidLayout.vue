<template>
  <q-layout view="hHh Lpr fFr">
    <Header/>

    <q-page-container>
      <router-view/>
    </q-page-container>

    <Footer/>
  </q-layout>
</template>

<script>
import Footer from "components/General/Layout/Footer.vue";
import Header from "components/General/Layout/Header.vue";

import {useRouter} from "vue-router";
import {Platform} from "quasar";
import Button from "components/General/Other/Button.vue";

export default {
  components: {Button, Header, Footer},

  setup() {
    // Check if not using capacitor, then forbid to enter this route
    if (!Platform.is.capacitor) {
      useRouter().push({path: '/status/404'})
    }
  }
}
</script>
