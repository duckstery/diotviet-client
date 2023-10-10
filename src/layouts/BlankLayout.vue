<template>
  <q-layout view="hHh Lpr fFr">
    <q-page-container>
      <router-view/>
    </q-page-container>
  </q-layout>
</template>

<script>
import Footer from "components/General/Layout/Footer.vue";
import Header from "components/General/Layout/Header.vue";

export default {
  components: {Header, Footer},

  data: () => ({
    dialog: true
  })
}
</script>
