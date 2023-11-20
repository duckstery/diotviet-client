<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <OrderProcessor class="q-dialog-plugin"/>
  </q-dialog>
</template>

<script>
import {useDialogPluginComponent} from 'quasar'
import {useDialogEditor} from "src/composables/useDialogEditor";

import OrderProcessor from "components/Manage/Transaction/Order/OrderProcessor.vue";

export default {
  name: 'OrderProcessorEditor',
  components: {OrderProcessor},

  props: {
    // Selected id
    selectedCode: String,
    // Printer
    printer: Object
  },

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    return {
      ...useDialogEditor(null, props.mode),
    }
  },
}
</script>
