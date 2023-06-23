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
      <template v-if="typeof col.value === 'boolean'">
        <q-icon
          v-if="col.name === 'isMale' || col.name === 'gender'"
          :name="`fa-solid fa-${col.value ? 'mars' : 'venus'}`"
          :color="col.value ? 'primary' : 'negative'"
        />
        <q-icon
          v-else
          :name="`fa-solid fa-${col.value ? 'check' : 'xmark'}`"
          :color="col.value ? 'positive' : 'negative'"
        />
      </template>
      <span v-else-if="col.name === 'status'">{{ $t(`field.${col.value}`) }}</span>
      <span v-else class="tw-text-sm">{{ col.value }}</span>
    </q-td>
  </q-tr>
</template>

<script>
export default {
  name: 'DataTableItem',

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
        'tw-bg-blue-100': isExpanded && this.$env.isLight(),
        'tw-bg-blue-950': isExpanded && !this.$env.isLight(),
      }
    }
  }
}
</script>
