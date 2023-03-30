<template>
  <q-table
    v-bind="$attrs"

    flat
    bordered
    virtual-scroll
    :title="title"
    :rows="items"
    :columns="headers"
    :loading="loading"
    :selection="selection"
    :rows-per-page-options="[10, 25, 50]"
    :rows-per-page-label="$t('field.records_per_page')"
    :no-data-label="$t('message.table_empty_data')"
    no-results-label="The filter didn't uncover any results"
    class="tw-min-h-[500px] sticky-header"
    table-class="virtual-scrollbar"
    row-key="name"
    v-model:selected="selected"
  >
    <template v-slot:top-right>
      <q-input borderless dense debounce="300" v-model="filter" placeholder="Search">
        <template v-slot:append>
          <q-icon name="search"/>
        </template>
      </q-input>
    </template>

    <!-- Loading -->
    <template v-slot:loading>
      <q-inner-loading showing color="primary">
        <q-spinner-gears size="100px" color="primary"/>
      </q-inner-loading>
    </template>

    <!-- Header -->
    <template v-slot:header="props">
      <q-tr>
        <q-th auto-width>
          <q-checkbox size="xs" v-model="props.selected"/>
        </q-th>
        <q-th
          v-for="col in props.cols"
          :key="col.name"
          :props="props"
        >
          <span class="tw-text-sm">{{ col.label }}</span>
        </q-th>
      </q-tr>
    </template>

    <!-- Body -->
    <template v-slot:body="props">
      <!-- Item's row -->
      <q-tr :props="props" class="tw-cursor-pointer" @click="props.expand = !props.expand">
        <q-td auto-width>
          <q-checkbox size="xs" v-model="props.selected"/>
        </q-td>
        <q-td
          v-for="col in props.cols"
          :key="col.name"
          :props="props"
        >
          <span class="tw-text-sm">{{ col.value }}</span>
        </q-td>
      </q-tr>

      <!-- Item's details -->
      <q-tr v-if="props.expand" :props="props">
        <q-td colspan="100%">
          <!-- Will be defined by caller -->
          <slot :cols="props.cols"/>
        </q-td>
      </q-tr>
    </template>

    <!-- Empty scenario -->
    <template v-slot:no-data="{ icon, message, filter }">
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
  name: 'DataTable',

  props: {
    // Table title
    title: {
      type: String,
      default: 'Title'
    },
    // Table header
    headers: {
      type: Array,
      default: () => ([
        {name: 'calcium', label: 'Calcium (%)', field: 'calcium'},
        {name: 'iron', label: 'Iron (%)', field: 'iron'}
      ])
    },
    // Table items
    items: {
      type: Array,
      default: () => ([
        {id: 1, calcium: '14%', iron: '1%'},

      ])
    },
    // Table loading state
    loading: {
      type: Boolean,
      default: false
    }
  },

  data: () => ({
    selection: "multiple",
    selected: [],

    // Table state
    isSelecting: false,

    // Style header background

  }),

  computed: {
    // Header background color
    headerBgColor() {
      return this.$q.dark.isActive
        ? '#1d1d1d'
        : '#fff'
    },
  }
}
</script>

<style lang="scss">
.sticky-header {
  .q-table__top, thead > tr:first-child > th {
    background-color: v-bind(headerBgColor);
  }


  thead > tr > th {
    position: sticky;
    z-index: 1
  }
  thead > tr:first-child > th {
    top: 0
  }
  /* this is when the loading indicator appears */
  &.q-table--loading > thead > tr:last-child > th {
    /* height of all previous header rows */
    top: 48px
  }


  /* prevent scrolling behind sticky top row on focus */
tbody {
  /* height of all previous header rows */
  scroll-margin-top: 48px
}

}
</style>
