<template>
  <q-table
    v-bind="$attrs"
    v-model:selected="selected"
    v-model:expanded="expanded"
    v-model:pagination="pagination"

    flat
    bordered
    ref="table"
    virtual-scroll
    table-class="virtual-scrollbar"
    no-results-label="The filter didn't uncover any results"

    :rows="items"
    :columns="headers"
    :selection="selection"
    :visible-columns="visibleCols"
    :rows-per-page-options="[10, 25, 50, 100]"
    :rows-per-page-label="$t('field.records_per_page')"
    :no-data-label="$t('message.table_empty_data')"
    :selected-rows-label="numberOfRows => $t('message.rows_selected', {attr: numberOfRows})"

    @request="onSearchRequest"
  >
    <template #top>
      <TextField v-model="search" label="Search" icon="search" @keydown.enter="onSearchRequest(null)"/>

      <q-space/>

      <!-- Business operations -->
      <DropdownButton :label="$t('field.operation')" icon="fa-solid fa-ellipsis-vertical"
                      stretch color="positive" class="tw-ml-2" no-caps :disable="!isSelecting">
        <q-list>
          <template v-for="operation in dropdownOperations">
            <q-item v-if="operation.key" clickable v-close-popup
                    @click="onExecuteOperation(operation)">
              <q-item-section avatar>
                <q-icon :name="`fa-solid ${operation.icon}`" :color="operation.color"/>
              </q-item-section>

              <q-item-section>
                {{ $t(`field.${operation.key}`) }}
              </q-item-section>
            </q-item>
            <q-separator v-else inset/>
          </template>
        </q-list>
      </DropdownButton>

      <!-- Row controls -->
      <Button :label="$t('field.add')" icon="fa-solid fa-plus"
              stretch color="positive" class="tw-ml-2" no-caps @click="request('create')"/>
      <Button :label="$t('field.collapse')" icon="fa-solid fa-down-left-and-up-right-to-center" @click="onCollapse"
              stretch color="positive" class="tw-ml-2" no-caps/>

      <q-space/>

      <ImEx @request="request"/>
      <!-- Columns visibility controls -->
      <DropdownButton :label="$t('field.display_col')" icon="fa-solid fa-eye"
                      stretch color="positive" class="tw-ml-2" no-caps
      >
        <div class="row">
          <div v-for="header in headers" class="col-6">
            <q-checkbox
              v-model="visibleCols"
              size="xs"
              :label="header.label"
              :val="header.name"
              :disable="header.name === 'code' || header.name === 'id'"
            />
          </div>
        </div>
      </DropdownButton>
    </template>

    <!-- Header -->
    <template #header="props">
      <DataTableHeader :props="props"/>
    </template>

    <!-- Body -->
    <template #body="props">
      <!-- Item's row -->
      <DataTableItem :props="props" @click="props.expand = !props.expand"/>

      <!-- Item's details -->
      <ExpandableTr :props="props" :expand="props.expand" :height="detailRowHeight" :width="detailRowWidth">
        <template #default="{active}">
          <slot :item="props.cols" :width="detailRowWidth" :active="active"/>
        </template>
      </ExpandableTr>
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
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import DropdownButton from "components/General/Other/DropdownButton.vue";
import ExpandableTr from "components/Manage/ExpandableTr.vue";
import ImEx from "components/Manage/ImEx.vue";
import DataTableItem from "components/Manage/DataTableItem.vue";
import DataTableHeader from "components/Manage/DataTableHeader.vue";

export default {
  name: 'DataTable',

  components: {DataTableHeader, DataTableItem, ImEx, ExpandableTr, DropdownButton, Button, TextField},

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
    // Selection type
    selection: {
      type: String,
      default: 'multiple'
    },
    // Operations
    operations: {
      type: Array,
      default: () => ([])
    }
  },

  data: () => ({
    // Search
    search: '',
    selected: [],
    expanded: [],
    pagination: [],

    // Visible columns
    visibleCols: [],
  }),

  computed: {
    // Header background color
    headerBgColor() {
      return this.$q.dark.isActive
        ? '#1d1d1d'
        : '#fff'
    },
    // Detail (expandable) row's height
    detailRowHeight() {
      return Math.floor(this.$q.screen.height * 2 / 3)
    },
    // Product detail width
    detailRowWidth() {
      return isNaN(this.$refs.table?.$el.clientWidth)
        ? 500
        : this.$refs.table?.$el.clientWidth - 32
    },
    // Check if table is selecting something
    isSelecting() {
      return this.selected.length !== 0
    },
    // Business operations
    dropdownOperations() {
      return [
        {icon: 'fa-trash', key: 'delete', color: 'negative', event: 'delete'},
        {},
        ...this.operations
      ]
    },
    // Get selected item's id
    getSelectedItems() {
      // Create output
      const ids = []
      const versions = []

      this.selected.forEach(item => {
        ids.push(item.id)
        versions.push(item.version)
      })

      return {ids, versions}
    },
  },

  watch: {
    // Initiate column to be displayed
    headers: {
      immediate: true,
      handler(value) {
        this.visibleCols = value
          .filter(header => header.isInitDisplay) // Header is visible at initiation if it's 'code' or it's allow to be
          .map(header => header.name) // Get header name only
      }
    },
    // Clear expand and select status when items are changed
    items() {
      this.selected = []
      this.expanded = []
    }
  },

  emits: ['search', 'request'],

  methods: {
    /**
     * Request
     */
    request(mode, data) {
      this.$emit('request', mode, data)
    },

    /**
     * Collapse all expanded row
     */
    onCollapse() {
      this.$refs.table.setExpanded([])
    },

    /**
     * On request
     *
     * @param data
     */
    onSearchRequest(data) {
      // Get default pagination
      if (this.$util.isUnset(data)) {
        data = {
          pagination: {
            ...this.pagination,
            page: 1
          }
        }
      }

      this.$emit('search', {
        search: this.search,
        ...data
      })
    },

    /**
     * On execute operations
     */
    onExecuteOperation(operation) {
      // Craft request payload
      const payload = {...this.getSelectedItems, target: operation.target, option: operation.option, reason: ""}
      // Check if this operation execution need reason
      if (operation.reasonable) {
        this.$util.promptReason().onOk(reason => {
          // Set reason
          payload.reason = reason
          this.request(operation.event, payload)
        })
      } else {
        this.request(operation.event, payload)
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
