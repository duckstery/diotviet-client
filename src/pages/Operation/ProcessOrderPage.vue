<template>
  <q-page class="bg-grey-3 tw-p-5 tw-flex" >
    <OrderProcessor flat/>
  </q-page>
</template>

<script>
import OrderProcessor from "components/Manage/Transaction/Order/OrderProcessor.vue";
import BarcodeScanner from "components/Work/CodeScanner.vue";

export default {
  name: 'ProcessOrderPage',
  components: {BarcodeScanner, OrderProcessor},
}
</script>
