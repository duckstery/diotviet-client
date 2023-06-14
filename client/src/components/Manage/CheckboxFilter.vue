<template>
  <FilterPanel :title="title">
    <q-scroll-area class="tw-h-[100px]">
      <q-item v-for="item in items" tag="label" dense>
        <q-item-section avatar>
          <q-checkbox :model-value="modelValue" @update:model-value="$emit('update:model-value', $event)"
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
  name: "CheckboxFilter",

  components: {TextField, FilterPanel},

  props: {
    // Model
    modelValue: Array,
    // Title
    title: String,
    // Items
    items: Array
  },

  emits: ['update:model-value'],
}
</script>
