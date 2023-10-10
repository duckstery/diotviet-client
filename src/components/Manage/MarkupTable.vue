<template>
  <q-table
    dense
    flat
    bordered
    virtual-scroll
    table-class="virtual-scrollbar tw-text-lg"

    :rows="items"
    :columns="headers"
    :rows-per-page-options="[rowPerPage]"
    :hide-bottom="items && items.length <= 5"
    :no-data-label="$t('message.table_empty_data')"
  >
    <template #header="props">
      <q-tr :props="props" :class="itemRowClass">
        <q-th
          v-for="col in props.cols"
          :key="col.name"
          :props="props"
          class="text-weight-medium"
        >
          {{ $t(`field.${$util.camelToSnake(col.field)}`) }}
        </q-th>
      </q-tr>
    </template>

    <!-- Empty scenario -->
    <template #no-data="{ icon, message, filter }">
      <div class="full-width row flex-center q-gutter-sm">
        <q-icon size="sm" name="fa-solid fa-face-sad-cry"/>
        <span>{{ message }}</span>
        <q-icon size="sm" :name="filter ? 'filter_b_and_w' : icon"/>
      </div>
    </template>
  </q-table>
</template>

<script>
export default {
  name: 'MarkupTable',

  props: {
    // Table header
    headers: {
      type: Array,
      default: () => ([
        {name: 'code', label: 'Code', field: 'code'},
        {name: 'calcium', label: 'Calcium (%)', field: 'calcium'},
        {name: 'iron', label: 'Iron (%)', field: 'iron'}
      ])
    },
    // Table items
    items: {
      type: Array,
      default: () => ([
        {code: 1, calcium: '14%', iron: '1%'},
        {code: 1, calcium: '14%', iron: '1%'},
        {code: 1, calcium: '14%', iron: '1%'},
      ])
    },
    // Row per page
    rowPerPage: {
      type: Number,
      default: 5
    }
  },

  computed: {
    /**
     * Generate item row class
     *
     * @return {object}
     */
    itemRowClass() {
      return {
        'tw-bg-blue-100': !this.$q.dark.isActive,
        'tw-bg-blue-950': this.$q.dark.isActive,
      }
    }
  }
}
</script>
