<template>
  <q-select
    dense outlined use-input input-debounce="500" behavior="menu" class="tw-w-1/2 tw-max-w-lg" ref="select"
    popup-content-class="virtual-scrollbar" clearable
    :model-value="getActiveCustomer" :label="$t('field.search_customer')" :options="customer.options"
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
import {computed, ref} from "vue";
import {mapActions} from "pinia";
import {useOrderStore} from "stores/order";
import {useMaxWidth} from "src/composables/useMaxWidth";
import {useSimpleSearch} from "src/composables/useSimpleSearch";

export default {
  name: "CustomerQuery",

  components: {Tooltip, Button},

  props: {
    // Customer
    modelValue: Object,
  },

  emits: ['update:model-value'],

  setup() {
    // Resources
    const select = ref(null)
    const maxWidth = useMaxWidth(() => select.value.$el.querySelector("input"), 0.7)
    const getActiveCustomer = computed(() => useOrderStore().getActiveCustomer)

    return {
      select, getActiveCustomer,
      customer: useSimpleSearch('/customer/query', false),
      selectedInfo: computed(() => !util.isUnset(getActiveCustomer.value) && `${getActiveCustomer.value.name} - ${getActiveCustomer.value.phoneNumber}`),
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
      })
    },

    // "Order" store
    ...mapActions(useOrderStore, ['setCustomer'])
  }
}
</script>


<style scoped>

</style>
