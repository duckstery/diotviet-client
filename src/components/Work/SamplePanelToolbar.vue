<template>
  <CustomerQuery />
  <Button
    color="secondary"
    class="tw-p-2 tw-ml-3"
    icon="receipt"
    :tooltip="$t('field.process_order')"
    @click="onProcess"
  />
  <Button
    color="info"
    class="tw-p-2 tw-ml-3"
    icon="fa-solid fa-hand-holding-dollar"
    :tooltip="$t('field.create_transaction')"
    @click="onCreateStream"
  />
  <Button
    color="secondary"
    class="tw-p-2 tw-ml-3"
    icon="fa-solid fa-ticket"
    :tooltip="$t('field.create_ticket')"
    @click="onCreateTicket"
  />

  <q-space />

  <Button
    :icon="displayMode.icon"
    class="tw-w-[41px]"
    flat
    :tooltip="displayMode.tooltip"
    @click="$emit('update:isVisualizing', !isVisualizing)"
  />
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import CustomerQuery from "components/Work/CustomerQuery.vue";
import { useOrderProcessor } from "src/composables/useOrderProcessor";
import { useTransactionEditor } from "src/composables/useTransactionEditor";
import { useTicketCreator } from "src/composables/useTicketCreator";
import {computed, toRaw} from "vue";
import { useOrderStore } from "stores/order";

export default {
  name: "SamplePanelToolbar",

  components: { CustomerQuery, TextField, Button },

  props: {
    modelValue: Object,
    isVisualizing: Boolean,
  },

  setup() {
    return {
      onProcess: useOrderProcessor(null),
      onCreateStream: useTransactionEditor(),
      onCreateTicket: useTicketCreator(computed(() => toRaw(useOrderStore().getActiveCustomer))),
    };
  },

  computed: {
    // Display mode
    displayMode() {
      return this.isVisualizing
        ? {
            icon: "fa-solid fa-down-left-and-up-right-to-center",
            tooltip: this.$t("field.compactize"),
          }
        : {
            icon: "fa-solid fa-image",
            tooltip: this.$t("field.visualize"),
          };
    },
  },
};
</script>
