<template>
  <q-tr :props="props" :class="generateItemRowClass(props.expand)"
        @click="$emit('click')">
    <q-td auto-width>
      <q-checkbox size="xs" v-model="props.selected"/>
    </q-td>
    <q-td
      v-for="col in props.cols"
      :key="col.name"
      :props="props"
    >
      <CustomerGender v-if="col.name === 'isMale' || col.name === 'gender'" :value="col.value" short/>
      <BooleanOption v-else-if="typeof col.value === 'boolean'" :value="col.value" short/>
      <OrderStatus v-else-if="col.name === 'status'" :value="col.value" short/>
      <span v-else class="tw-text-sm">{{ col.value }}</span>
    </q-td>
  </q-tr>
</template>

<script>
import OrderStatus from "components/Manage/Constant/OrderStatus.vue";
import CustomerGender from "components/Manage/Constant/CustomerGender.vue";
import BooleanOption from "components/Manage/Constant/BooleanOption.vue";

export default {
  name: 'DataTableItem',
  components: {BooleanOption, CustomerGender, OrderStatus},

  props: {
    props: {
      type: Object,
      default: () => ({
        cols: [],
        expand: false,
        selected: false,
      })
    }
  },

  emits: ['click'],

  methods: {
    /**
     * Generate item row class
     *
     * @return {object}
     */
    generateItemRowClass(isExpanded) {
      return {
        'tw-cursor-pointer': true,
        'tw-bg-blue-100': isExpanded && !this.$q.dark.isActive,
        'tw-bg-blue-950': isExpanded && this.$q.dark.isActive,
      }
    }
  }
}
</script>
