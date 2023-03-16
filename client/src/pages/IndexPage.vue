<template>
  <q-page class="bg-grey-3- bg-dark tw-p-5 tw-pr-20 tw-flex tw-h-screen">
    <LoginPanel/>
  </q-page>
</template>

<script>
import LoginPanel from "components/Auth/LoginPanel.vue";

export default {
  name: 'IndexPage',
  components: {LoginPanel},
}
</script>
