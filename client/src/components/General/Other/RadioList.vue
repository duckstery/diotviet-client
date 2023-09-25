<template>
  <q-list>
    <q-item v-for="option in options" tag="label" dense>
      <q-item-section side>
        <q-radio :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)"
                 :val="option.value" color="primary" dense/>
      </q-item-section>
      <q-item-section>
        <q-item-label>{{ option.label }}</q-item-label>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";

export default {
  name: "RadioList",

  components: {TextField, FilterPanel},

  props: {
    // Model
    modelValue: Boolean | String,
    // Label for "true" option
    options: {
      type: Array,
      default: () => ([{value: true, label: 'True'}])
    },
  },

  emits: ['update:modelValue'],
}
</script>
