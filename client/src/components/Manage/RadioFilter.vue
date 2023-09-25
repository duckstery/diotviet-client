<template>
  <FilterPanel :title="title">
    <RadioList :options="options" :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)"/>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";
import RadioList from "components/General/Other/RadioList.vue";
import {computed} from "vue";

export default {
  name: "RadioFilter",

  components: {RadioList, TextField, FilterPanel},

  props: {
    // Model
    modelValue: Boolean,
    // Title
    title: String,
    // Label for "true" option
    trueLabel: {
      type: String,
      default: null
    },
    // Label for "false" option
    falseLabel: {
      type: String,
      default: null
    },
  },

  emits: ['update:modelValue'],

  computed: {
    // RadioFilter options
    options() {
      return [
        {value: null, label: this.$t('field.all')},
        {value: true, label: this.trueLabel ?? this.$t('field.true')},
        {value: false, label: this.falseLabel ?? this.$t('field.false')}
      ]
    }
  }
}
</script>
