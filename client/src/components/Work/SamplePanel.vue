<template>
  <q-card class="sample-panel">
    <q-card-section class="flex tw-w-full">
      <TextField v-model="search" class="tw-w-5/12 tw-max-w-lg" :label="$t('field.search_customer')"/>
      <Button icon="add" :tooltip="$t('new_order')" color="positive" no-flat class="tw-w-[41px] tw-ml-1"
              @click="onAddCustomer"/>

      <q-space/>

      <Button icon="list" text-color="dark" class="tw-w-[41px]" :tooltip="$t('field.categorize')"
              @click="onCategorize"/>
      <Button icon="filter_alt" text-color="dark" class="tw-w-[41px]" :tooltip="$t('field.filter')"
              @click="onFilter"/>
      <Button :icon="displayMode.icon" text-color="dark" class="tw-w-[41px]" :tooltip="displayMode.tooltip"
              @click="isVisualizing = !isVisualizing"/>
    </q-card-section>

    <q-card-section class="row tw-w-full tw-py-0">
      <SampleItem v-for="item in pageItems" :class="displayMode.class" :visualize="isVisualizing"/>
    </q-card-section>

    <q-card-actions>
      <q-pagination
        v-model="page"
        flat
        :max="maxPage"
        color="dark"
        max-pages="3"
        direction-links
        :ellipses="false"
        active-color="primary"
        :boundary-numbers="false"
      />

      <q-space/>

      <Button
        stretch
        align="around"
        color="warning"
        class="tw-w-1/5"
        icon="fa-solid fa-cart-plus"
        @click="onOrder"
        :label="$t('field.order_item')"
      />
      <Button
        stretch
        icon="wallet"
        align="around"
        color="positive"
        class="tw-w-1/5"
        @click="onPurchase"
        :label="$t('field.purchase')"
      />
    </q-card-actions>
  </q-card>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import Select from "components/General/Other/Select.vue";
import SampleItem from "components/Work/SampleItem.vue";

export default {
  name: 'SamplePanel',

  components: {SampleItem, Select, Button, TextField},

  data: () => ({
    // Design
    isVisualizing: false,
    // Other
    search: '',
    // Pagination
    page: 1,
  }),

  computed: {
    // Display mode
    displayMode() {
      return this.isVisualizing
        ? {
          icon: 'fa-solid fa-down-left-and-up-right-to-center',
          class: 'col-3',
          tooltip: this.$t('field.compactize')
        }
        : {
          icon: 'fa-solid fa-image',
          class: 'col-4',
          tooltip: this.$t('field.visualize')
        }
    },
    // Max item can be display in a page
    itemsPerPage() {
      return this.isVisualizing ? 8 : 15
    },
    // Max page to display all items
    maxPage() {
      return 2
    },
    // Items of a page
    pageItems() {
      return this.itemsPerPage;
    }
  },

  methods: {
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

    /**
     * On placing order event handler
     */
    onOrder() {

    },

    /**
     * On purchase order event handler
     */
    onPurchase() {

    }
  }
}
</script>
