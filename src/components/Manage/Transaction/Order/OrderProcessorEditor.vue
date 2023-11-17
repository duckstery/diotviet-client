<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <OrderProcessor class="q-dialog-plugin"/>
  </q-dialog>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import ConstantField from "components/General/Other/ConstantField.vue";
import InputField from "components/General/Other/InputField.vue";
import OrderProcessorAction from "components/Manage/Transaction/Order/OrderProcessorAction.vue";
import Skeleton from "components/General/Other/Skeleton.vue";

import {useDialogPluginComponent} from 'quasar'
import {computed, nextTick, onMounted, ref, toRef, watch, provide} from "vue";
import {useDialogEditor} from "src/composables/useDialogEditor";
import {useDebounceModel} from "src/composables/useDebounceModel";
import {useSimpleSearch} from "src/composables/useSimpleSearch";
import {useSimpleGrouper} from "src/composables/useSimpleGrouper";
import {env, util, constant, error} from "src/boot"
import {useI18n} from "vue-i18n";
import {watchOnce} from "@vueuse/core";
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
