<template>
  <FilterPanel :title="title">
    <!-- Search for item -->
    <div class="tw-flex">
      <TextField v-model="name" class="tw-w-11/12 tw-max-w-lg" :label="$t('field.search')" icon="search"/>
      <Button icon="add" :tooltip="$t('field.add')" color="positive" no-flat class="tw-w-[41px] tw-ml-1"
              @click="$emit('control', ['create'])"/>
    </div>

    <q-scroll-area class="tw-h-[160px] tw-mt-3">
      <q-item v-for="item in satisfiedItems" tag="label" dense class="tw-pr-[20px]">
        <q-item-section side>
          <q-radio :model-value="modelValue" @update:model-value="$emit('update:model-value', $event)"
                   :val="item.id" color="primary" dense/>
        </q-item-section>
        <q-item-section>
          <q-item-label class="tw-min-w-[100px]">{{ item.name }}</q-item-label>
        </q-item-section>
        <q-space/>
        <q-item-section v-if="!$util.isUnset(item.id)" side class="tw-ml-5">
          <div class="q-gutter-xs">
            <Button icon="fa-solid fa-pen" size="sm" flat @click="$emit('control', ['update', item])"/>
            <Button icon="fa-solid fa-trash" size="sm" flat @click="$emit('control', ['delete', item.id])"/>
          </div>
        </q-item-section>
      </q-item>
    </q-scroll-area>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";

export default {
  name: "DynamicFilter",

  components: {Button, TextField, FilterPanel},

  props: {
    // Model
    modelValue: Number,
    // Title
    title: String,
    // Items
    items: Array,
  },

  emits: ['update:model-value', 'control'],

  data: () => ({
    name: '',
  }),

  computed: {
    // Option: All
    getAll() {
      return {
        id: null,
        name: this.$t('field.all')
      }
    },

    // Get satisfied group and always add group: All to front
    satisfiedItems() {
      // Get satisfiedItems
      const satisfiedItems = this.$util.isUnset(this.name) ? this.items : this.items?.filter(this.filterByName)
      // Add group: All
      satisfiedItems.unshift(this.getAll)

      return satisfiedItems.sort(this.alphabetically)
    },
  },

  methods: {
    /**
     * Callback for satisfied items filter
     *
     * @param item
     * @return {*}
     */
    filterByName(item) {
      return item.name.includes(this.name)
    },

    /**
     * Callback for satisfied items sort
     *
     * @param {{name: string}} a
     * @param {{name: string}} b
     */
    alphabetically(a, b) {
      this.$util.compare(a.name, b.name)
    }
  }
}
</script>
