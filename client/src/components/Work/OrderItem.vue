<template>
  <q-card class="order-item hover:tw-border-sky-400 tw-border-transparent tw-border-solid tw-cursor-pointer" flat>
    <q-card-section>
      <!-- Title section -->
      <div class="row tw-text-[16px]">
        <div class="tw-font-semibold">{{ this.index + 1 }}.&nbsp;</div>
        <div class="tw-text-ellipsis tw-h-6 tw-max-w-sm tw-line-clamp-1">
          {{ value.title }}
          <q-tooltip class="tw-text-[14px]">{{ value.title }}</q-tooltip>
        </div>
        <div class="tw-ml-2">(Code: {{ value.code }})</div>

        <q-space/>

        <q-chip v-if="value.measureUnit" color="info" text-color="white" class="tw-mr-2">
          {{ value.measureUnit }}
        </q-chip>
        <Button icon="fa-solid fa-plus" flat :tooltip="$t('field.add')" size="sm" @click="onAddDuplicateItem"/>
        <Button icon="fa-solid fa-ellipsis-vertical" flat :tooltip="$t('field.more')" size="sm" @click="onMore"/>
      </div>

      <!-- Detail section -->
      <OrderItemDetail :value="value"/>

      <!-- Sub detail section -->
      <div>
        <template v-for="(detail, index) in details" :key="detail.uid">
          <hr class="tw-mt-4 tw-border-dotted tw-border-gray-400 tw-border-0 tw-border-t-[1px]"/>
          <OrderItemDetail v-model:value="details[index]" @remove="details.splice(index, 1)"/>
        </template>
      </div>

    </q-card-section>

  </q-card>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import OrderItemDetail from "components/Work/OrderItemDetail.vue";
import {useOrderStore} from "stores/order";
import {notify} from "src/boot";
import {ref} from "vue";

export default {
  name: 'OrderItem',
  components: {OrderItemDetail, TextField, Button},

  props: {
    // Item's index
    index: Number,
    // Item's value
    value: {
      type: Object,
      default: () => ({
        uid: "",
        id: 1,
        code: '000',
        title: 'Title of cdddd ddddd ddddd ddd dddddddddd ddddddddd'.toUpperCase(),
        measureUnit: 'Kg',
        src: 'https://cdn.quasar.dev/img/parallax2.jpg',
        originalPrice: '50000',
        discount: '10',
        discountUnit: '%',
        actualPrice: '45000',
        totalPrice: '45000',
        quantity: '1'
      })
    }
  },

  setup(props) {
    // Store
    const orderStore = useOrderStore()
    // Sub details
    const details = ref(orderStore.getDuplicateItemById(props.value.id))

    return {
      details: details,
      // More functionality
      onMore: () => notify('Under development!', 'warning'),
      // Add sub details
      onAddDuplicateItem: () => details.value.push(orderStore.addItem(props.value, true))
    }
  },
}
</script>
