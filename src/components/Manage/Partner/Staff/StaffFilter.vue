<template>
  <!-- Gender -->
  <RadioFilter v-model="filter.isMale" class="tw-mt-3" :title="$t('field.gender')"
               :true-label="$t('field.male')" :false-label="$t('field.female')"/>

  <!-- Status -->
  <RadioFilter v-model="filter.isDeactivated" class="tw-mt-3" :title="$t('field.status')"
               :true-label="$t('field.activated')" :false-label="$t('field.deactivated')"/>

  <!-- Category filter -->
  <CheckboxFilter v-model="filter.roles" :items="$constant.roles()" :title="$t('field.role')" :height="160"
                  class="tw-mt-3"/>

  <!-- Create at filter -->
  <DateFilter v-model:from="filter.createAtFrom" v-model:to="filter.createAtTo" class="tw-mt-3"
              :title="$t('field.created_at')"/>

  <!-- Birthday filter -->
  <FilterPanel :title="$t('field.birthday')" class="tw-mt-3" reloadable @reload="reloadBirthday">
    <DatePicker v-model="filter.birthdayFrom" :label="$t('field.from_date')"/>
    <DatePicker v-model="filter.birthdayTo" :label="$t('field.to_date')" class="tw-mt-3"/>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";
import DatePicker from "components/General/Other/DatePicker.vue";
import DynamicFilter from "components/Manage/DynamicFilter.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import {useFilterRequest} from "src/composables/useFilterRequest";
import {reactive} from "vue";
import DateFilter from "components/Manage/DateFilter.vue";
import CheckboxFilter from "components/Manage/CheckboxFilter.vue";

export default {
  name: "CustomerFilter",

  components: {CheckboxFilter, DateFilter, RadioFilter, DynamicFilter, DatePicker, TextField, FilterPanel},

  props: {
    modelValue: Object,
    categories: Array,
    groups: Array
  },

  emits: ['request', 'update:modelValue', 'control'],

  setup(props, context) {
    // Filter
    const filter = reactive(props.modelValue)
    // Setup filter request
    useFilterRequest(filter, context)

    return {
      filter: filter,
      reloadBirthday: () => {
        filter.birthdayFrom = null
        filter.birthdayTo = null
      }
    }
  }
}
</script>
