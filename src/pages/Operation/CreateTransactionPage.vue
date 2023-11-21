<template>
  <q-page class="bg-grey-3 tw-p-5 tw-flex tw-h-screen">
    <TransactionEditor :model-value="true" seamless @before-hide="$router.back"/>
  </q-page>
</template>

<script>
import TransactionEditor from "components/Manage/Transaction/TransactionEditor.vue";

export default {
  name: 'CreateTransactionPage',

  components: {TransactionEditor},
}
</script>
