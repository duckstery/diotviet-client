<template>
  <TextField v-model="search" class="tw-w-5/12 tw-max-w-lg" :label="$t('field.search_customer')"/>
  <Button icon="add" :tooltip="$t('field.add')" color="positive" no-flat class="tw-w-[41px] tw-ml-1"
          @click="onAddCustomer"/>

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

export default {
  name: 'SamplePanelToolbar',

  components: {TextField, Button},

  props: {
    modelValue: Object,
    isVisualizing: Boolean,
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

  data: () => ({
    // Other
    search: '',
  }),

  methods: {
    /**
     * On search customer
     */
    onSearchCustomer() {

    },

    /**
     * On add new customer
     */
    onAddCustomer() {

    },

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
