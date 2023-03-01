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

export default {
  components: {Header, Footer},
}
</script>
