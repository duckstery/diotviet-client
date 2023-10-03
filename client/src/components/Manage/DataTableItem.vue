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
      <ConstantField v-if="typeof col.value === 'boolean' || col.name === 'status'" :value="col.value" :target="col.name" short/>
      <span v-else class="tw-text-sm">{{ col.value }}</span>
    </q-td>
  </q-tr>
</template>

<script>
import ConstantField from "components/General/Other/ConstantField.vue";

export default {
  name: 'DataTableItem',
  components: {ConstantField},

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
