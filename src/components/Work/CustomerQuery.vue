<template>
  <q-select
    dense outlined use-input input-debounce="500" behavior="menu" class="tw-w-1/2 tw-max-w-lg" ref="select"
    color="brand" bg-color="brand"
    popup-content-class="virtual-scrollbar" clearable
    :model-value="getActiveCustomer" :label="$t('field.search_customer')" :options="customer.data"
    @update:model-value="setCustomer($event)" @filter="customer.onFilter"
  >
    <template #selected>
      <div v-if="selectedInfo" :class="selectedClass">
        {{ selectedInfo }}
        <Tooltip :content="selectedInfo"/>
      </div>
    </template>
    <template #option="scope">
      <q-item v-bind="scope.itemProps">
        <q-item-section>
          <q-item-label>{{ scope.opt.name }}</q-item-label>
          <q-item-label caption>{{ scope.opt.code }}</q-item-label>
        </q-item-section>
        <q-item-section side>
          <q-item-label class="text-primary">{{ scope.opt.phoneNumber }}</q-item-label>
        </q-item-section>
      </q-item>
    </template>
  </q-select>
  <Button icon="add" :tooltip="$t('field.add')" color="positive" no-flat class="tw-w-[41px] tw-ml-1"
          @click="onAddCustomer"/>
</template>

<script>
import Button from "components/General/Other/Button.vue";
import CustomerEditor from "components/Manage/Partner/Customer/CustomerEditor.vue";
import Tooltip from "components/General/Other/Tooltip.vue";

import {Dialog} from "quasar";
import {util} from "src/boot";
import {computed} from "vue";
import {mapActions} from "pinia";
import {useOrderStore} from "stores/order";
import {useMaxWidth} from "src/composables/useMaxWidth";
import {useSimpleSearch} from "src/composables/useSimpleSearch";
import {templateRef} from "@vueuse/core";

export default {
  name: "CustomerQuery",

  components: {Tooltip, Button},

  props: {
    // Customer
    modelValue: Object,
  },

  emits: ['update:modelValue'],

  setup() {
    // Resources
    const select = templateRef('select')
    const maxWidth = useMaxWidth(() => select.value.$el.querySelector("input"), 0.7)
    const getActiveCustomer = computed(() => useOrderStore().getActiveCustomer)

    // Selected info
    const selectedInfo = computed(() => {
      // Info
      let info = ""
      if (!util.isUnset(getActiveCustomer.value)) {
        info += `${getActiveCustomer.value.name}`
        if (!util.isUnset(getActiveCustomer.value.phoneNumber)) {
          info += ` - ${getActiveCustomer.value.phoneNumber}`
        }
      }
      return info
    })

    return {
      getActiveCustomer: getActiveCustomer,
      customer: useSimpleSearch('/customer/query', false),
      selectedInfo: selectedInfo,
      selectedClass: computed(() => 'text-primary tw-text-ellipsis tw-line-clamp-1 ' + maxWidth.value),
    }
  },

  methods: {
    /**
     * On add new customer
     */
    async onAddCustomer() {
      // Fetch groups
      const res = await this.$axios.get('/group/index/2')

      // Invoke dialog
      Dialog.create({
        component: CustomerEditor,
        componentProps: {
          mode: 'create',
          groups: res.data.payload
        }
      }).onOk(this.setCustomer)
    },

    // "Order" store
    ...mapActions(useOrderStore, ['setCustomer'])
  }
}
</script>
