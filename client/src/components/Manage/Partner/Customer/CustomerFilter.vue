<template>
  <!-- Group filter -->
  <DynamicFilter v-model="filter.group" :items="groups" :title="$t('field.group')" class="tw-mt-3"
                 @control="$emit('control', ...$event)"/>

  <!-- Create at filter -->
  <FilterPanel :title="$t('field.created_at')" class="tw-mt-3">
    <DatePicker v-model="filter.createAtFrom" :label="$t('field.from_date')"/>
    <DatePicker v-model="filter.createAtTo" :label="$t('field.to_date')" class="tw-mt-3"/>
  </FilterPanel>

  <!-- Birthday filter -->
  <FilterPanel :title="$t('field.birthday')" class="tw-mt-3">
    <DatePicker v-model="filter.birthdayFrom" :label="$t('field.from_date')"/>
    <DatePicker v-model="filter.birthdayTo" :label="$t('field.to_date')" class="tw-mt-3"/>
  </FilterPanel>

  <!-- Last transaction filter -->
  <FilterPanel :title="$t('field.last_transaction')" class="tw-mt-3">
    <DatePicker v-model="filter.lastTransactionAtFrom" :label="$t('field.from_date')"/>
    <DatePicker v-model="filter.lastTransactionAtTo" :label="$t('field.to_date')" class="tw-mt-3"/>
  </FilterPanel>

  <!-- Gender -->
  <RadioFilter v-model="filter.isMale" class="tw-mt-3" :title="$t('field.gender')"
               :true-label="$t('field.male')" :false-label="$t('field.female')"/>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";
import DatePicker from "components/General/Other/DatePicker.vue";
import DynamicFilter from "components/Manage/DynamicFilter.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import {useFilterRequest} from "src/composables/useFilterRequest";
import {reactive} from "vue";

export default {
  name: "CustomerFilter",

  components: {RadioFilter, DynamicFilter, DatePicker, TextField, FilterPanel},

  props: {
    modelValue: Object,
    categories: Array,
    groups: Array
  },

  emits: ['request', 'update:modelValue', 'control'],

  setup(props, context) {
    // Filter
    const filter = reactive({
      group: null,
      createAtFrom: null,
      createAtTo: null,
      birthdayFrom: null,
      birthdayTo: null,
      lastTransactionAtFrom: null,
      lastTransactionAtTo: null,
      isMale: null
    })
    // Setup filter request
    useFilterRequest(filter, context)

    return {filter}
  }
}
</script>
