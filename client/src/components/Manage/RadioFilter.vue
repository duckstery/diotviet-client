<template>
  <FilterPanel :title="title">
    <q-item v-for="option in [null, true, false]" tag="label" dense>
      <q-item-section avatar>
        <q-radio :model-value="modelValue" @update:model-value="$emit('update:model-value', $event)"
                 :val="option" color="primary" dense/>
      </q-item-section>
      <q-item-section>
        <q-item-label v-if="option === null">{{ $t('field.all') }}</q-item-label>
        <q-item-label v-else-if="option">{{ trueLabel ?? $t('field.true') }}</q-item-label>
        <q-item-label v-else>{{ trueLabel ?? $t('field.false') }}</q-item-label>
      </q-item-section>
    </q-item>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";

export default {
  name: "RadioFilter",

  components: {TextField, FilterPanel},

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

  emits: ['update:model-value'],
}
</script>
