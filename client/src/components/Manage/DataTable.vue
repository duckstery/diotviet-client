<template>
  <q-table
    v-bind="$attrs"
    v-model:selected="selected"

    flat
    bordered
    virtual-scroll
    class="tw-h-full sticky-header"
    table-class="virtual-scrollbar"
    no-results-label="The filter didn't uncover any results"

    :rows="items"
    :columns="headers"
    :selection="selection"
    :visible-columns="visibleCols"
    :rows-per-page-options="[10, 25, 50]"
    :rows-per-page-label="$t('field.records_per_page')"
    :no-data-label="$t('message.table_empty_data')"
    :selected-rows-label="numberOfRows => $t('message.rows_selected', {attr: numberOfRows})"
  >
    <template #top>
      <TextField v-model="search" label="Search" icon="search"/>

      <q-space/>

      <Button :label="$t('field.add')" icon="fa-solid fa-plus"
              stretch color="positive" class="tw-ml-2" no-caps/>
      <!-- Columns visibility controls -->
      <DropdownButton :label="$t('field.display_col')" icon="fa-solid fa-eye"
                      stretch color="positive" class="tw-ml-2" no-caps
      >
        <div class="row">
          <div v-for="header in headers" class="col-6">
            <q-checkbox v-model="visibleCols" size="xs"
                        :label="header.label" :val="header.name" :disable="header.name === 'code'"/>
          </div>
        </div>
      </DropdownButton>
    </template>

    <template v-slot:top-right>

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
          <q-badge v-if="typeof col.value === 'boolean'" :color="col.value ? 'positive' : 'negative'">
            {{ $t(`field.${col.value}`) }}
          </q-badge>
          <span v-else class="tw-text-sm">{{ col.value }}</span>
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
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import DropdownButton from "components/General/Other/DropdownButton.vue";

export default {
  name: 'DataTable',

  components: {DropdownButton, Button, TextField},

  props: {
    // Table header
    headers: {
      type: Array,
      default: () => ([
        {name: 'code', label: 'Code', field: 'code', isInitDisplay: true},
        {name: 'calcium', label: 'Calcium (%)', field: 'calcium', isInitDisplay: true},
        {name: 'iron', label: 'Iron (%)', field: 'iron', isInitDisplay: true}
      ])
    },
    // Table items
    items: {
      type: Array,
      default: () => ([
        {code: 1, calcium: '14%', iron: '1%'},

      ])
    },
  },

  data: () => ({
    // Search
    search: '',

    selection: "multiple",
    selected: [],

    // Table state
    isSelecting: false,

    // Visible columns
    visibleCols: [],

    // Style header background

  }),

  computed: {
    // Header background color
    headerBgColor() {
      return this.$q.dark.isActive
        ? '#1d1d1d'
        : '#fff'
    },
  },

  watch: {
    // Initiate column to be displayed
    headers: {
      immediate: true,
      handler(value) {
        this.visibleCols = value
          .filter(header => header.name === 'code' || header.isInitDisplay) // Header is visible at initiation if it's 'code' or it's allow to be
          .map(header => header.name) // Get header name only
      }
    }
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
