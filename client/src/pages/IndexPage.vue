<template>
  <q-page class="bg-grey-3 tw-p-5 tw-pr-20 tw-flex tw-h-screen">
    <LoginPanel/>

    <q-page-sticky position="top-right" :offset="[20, 20]">
      <Setting fab color="primary"/>
    </q-page-sticky>
  </q-page>
</template>

<script>
import LoginPanel from "components/Auth/LoginPanel.vue";
import Setting from "components/General/Layout/Setting.vue";

export default {
  name: 'IndexPage',
  components: {Setting, LoginPanel},
}
</script>
