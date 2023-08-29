<template>
  <q-select
    dense outlined use-input input-debounce="500" behavior="menu" icon="fa-solid fa-search"
    class="tw-w-1/4 tw-max-w-lg tw-ml-4"
    popup-content-class="virtual-scrollbar" ref="select" color="brand" bg-color="brand"
    :model-value="modelValue"
    :label="$t('field.search_product')" :options="items"
    @update:model-value="onSelect" @filter="onFilter"
  >
    <template #option="scope">
      <q-item v-bind="scope.itemProps">
        <q-item-section avatar>
          <q-avatar rounded>
            <q-img :src="scope.opt.src"/>
          </q-avatar>
        </q-item-section>
        <q-item-section>
          <q-item-label class="tw-max-w-lg">{{ scope.opt.title }}</q-item-label>
          <q-item-label caption>{{ scope.opt.code }}</q-item-label>
        </q-item-section>
        <q-item-section side top>
          <q-badge color="warning" text-color="brand">
            <q-icon name="fa-solid fa-tags" size="12"/>
            <span class="tw-ml-1">{{ $util.formatMoney(scope.opt.actualPrice) }}</span>
          </q-badge>
        </q-item-section>
      </q-item>
    </template>
  </q-select>
</template>

<script>
import Button from "components/General/Other/Button.vue";
import Tooltip from "components/General/Other/Tooltip.vue";
import TextField from "components/General/Other/TextField.vue";

import {util} from "src/boot";
import {ref, toRaw} from "vue";
import {useProductStore} from "stores/product";
import {useOrderStore} from "stores/order";

export default {
  name: "SampleQuery",

  components: {TextField, Tooltip, Button},

  setup() {
    // Search Product
    const items = ref([])
    const productStore = useProductStore()
    const modelValue = ref(null)
    // Filter Product
    const onFilter = (query, update) => update(() => items.value = util.isUnset(util.nullIfEmpty(query)) ? [] : productStore.find(query))

    // Select Product
    const orderStore = useOrderStore()
    const onSelect = (product) => orderStore.addItem(toRaw(product))

    return {
      modelValue: modelValue,
      items: items,
      onFilter: onFilter,
      onSelect: onSelect
    }
  },
}
</script>
