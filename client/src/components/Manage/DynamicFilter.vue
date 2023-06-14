<template>
  <FilterPanel :title="title">
    <!-- Search for item -->
    <TextField v-model="name" :label="$t('field.search')" icon="search"/>

    <q-scroll-area class="tw-h-[160px] tw-mt-3">
      <q-item v-for="item in satisfiedItems" tag="label" dense>
        <q-item-section avatar>
          <q-radio :model-value="modelValue" @update:model-value="$emit('update:model-value', $event)"
                   :val="item.id" color="primary" dense/>
        </q-item-section>
        <q-item-section>
          <q-item-label>{{ item.name }}</q-item-label>
        </q-item-section>
      </q-item>
    </q-scroll-area>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";

export default {
  name: "DynamicFilter",

  components: {TextField, FilterPanel},

  props: {
    // Model
    modelValue: Number,
    // Title
    title: String,
    // Items
    items: Array,
  },

  emits: ['update:model-value'],

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
      const satisfiedItems = this.items?.filter(item => item.name.includes(this.name))
      // Add group: All
      satisfiedItems.unshift(this.getAll)

      return satisfiedItems
    },
  }
}
</script>
