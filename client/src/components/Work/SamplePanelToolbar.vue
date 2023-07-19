<template>
  <CustomerQuery/>
  <q-space/>
  <Button
    color="info"
    class="tw-p-2"
    icon="receipt"
    :label="$t('field.order')"
    :tooltip="$t('field.order')"
    @click="onProcess"
  />
  <q-space/>

  <Button icon="list" class="tw-w-[41px]" flat :tooltip="$t('field.categorize')"
          @click="onCategorize"/>
  <Button icon="filter_alt" class="tw-w-[41px]" flat :tooltip="$t('field.filter')"
          @click="onFilter"/>
  <Button :icon="displayMode.icon" class="tw-w-[41px]" flat :tooltip="displayMode.tooltip"
          @click="$emit('update:isVisualizing', !isVisualizing)"/>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import CustomerQuery from "components/Work/CustomerQuery.vue";
import {useOrderProcessor} from "src/composables/useOrderProcessor";

export default {
  name: 'SamplePanelToolbar',

  components: {CustomerQuery, TextField, Button},

  props: {
    modelValue: Object,
    isVisualizing: Boolean,
  },

  setup() {
    return {
      onProcess: useOrderProcessor()
    }
  },

  computed: {
    // Display mode
    displayMode() {
      return this.isVisualizing
        ? {
          icon: 'fa-solid fa-down-left-and-up-right-to-center',
          tooltip: this.$t('field.compactize')
        }
        : {
          icon: 'fa-solid fa-image',
          tooltip: this.$t('field.visualize')
        }
    },
  },

  methods: {
    /**
     * On categorize sample item event handler
     */
    onCategorize() {

    },

    /**
     * On filter sample item event handler
     */
    onFilter() {

    },
  }
}
</script>
