<template>
  <q-card class="sample-panel">
    <!-- Toolbar section -->
    <q-card-section class="flex tw-w-full">
      <SamplePanelToolbar v-model:is-visualizing="isVisualizing"/>
    </q-card-section>

    <!-- Items section -->
    <q-card-section class="tw-w-full tw-min-h-[424px] tw-py-0">
      <div class="row">
        <SampleItem v-for="item in pageItems" :class="isVisualizing ? 'col-3' : 'col-4'" :visualize="isVisualizing" @click="onAddItem(item)"/>
      </div>
    </q-card-section>

    <!-- Actions section -->
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
import {mapActions} from "pinia";
import {useOrderStore} from "stores/order";

import Button from "components/General/Other/Button.vue";
import Select from "components/General/Other/Select.vue";
import SampleItem from "components/Work/SampleItem.vue";
import SamplePanelToolbar from "components/Work/SamplePanelToolbar.vue";

export default {
  name: 'SamplePanel',

  components: {SamplePanelToolbar, SampleItem, Select, Button},

  data: () => ({
    // Design
    isVisualizing: false,
    // Pagination
    page: 1,
    // Items
    items: Array(17).fill({
      code: '001',
      title: 'Title of cdddd ddddd ddddd ddd dddddddddd ddddddddd'.toUpperCase(),
      originalPrice: '50000',
      discount: '10',
      discountUnit: '%',
      actualPrice: '45000',
      measureUnit: 'Kg',
      src: 'https://cdn.quasar.dev/img/parallax2.jpg'
    })
  }),

  computed: {
    // Max item can be display in a page
    itemsPerPage() {
      return this.isVisualizing ? 8 : 15
    },
    // Max page to display all items
    maxPage() {
      return Math.ceil(this.items.length / this.itemsPerPage)
    },
    // Calculate the start index that indicate the start of "page"
    startIndexOfPage() {
      return (this.page - 1) * this.itemsPerPage
    },
    // Items of a page
    pageItems() {
      return this.items.slice(this.startIndexOfPage, this.startIndexOfPage + this.itemsPerPage);
    }
  },

  methods: {
    /**
     * On place order event handler
     */
    onOrder() {

    },

    /**
     * On purchase order event handler
     */
    onPurchase() {

    },

    /**
     * On add item to order
     */
    onAddItem(item) {
      this.addItem(item);
    },

    // "Order" store
    ...mapActions(useOrderStore, ['addItem'])
  }
}
</script>
