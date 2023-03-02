<template>
  <q-page class="bg-grey-3 tw-p-5 tw-pr-20">
    <div class="row">
      <div class="col-6">
        <OrderPanel/>
      </div>
      <div class="col-6">
        <ItemPanel class="tw-ml-5"/>
      </div>
    </div>

  </q-page>
</template>

<script>
import {defineComponent} from 'vue'
import OrderPanel from "components/Work/OrderPanel.vue";
import ItemPanel from "components/Work/SamplePanel.vue";

export default defineComponent({
  name: 'IndexPage',

  components: {ItemPanel, OrderPanel}
})
</script>
