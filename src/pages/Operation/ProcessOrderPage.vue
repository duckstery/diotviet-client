<template>
  <q-page class="bg-grey-3 tw-p-5 tw-flex tw-h-screen" >
    <OrderProcessor flat/>
  </q-page>
</template>

<script>
import OrderProcessor from "components/Manage/Transaction/Order/OrderProcessor.vue";

export default {
  name: 'ProcessOrderPage',
  components: {OrderProcessor},
}
</script>
