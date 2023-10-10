<template>
  <PopupTextField popup-icon="event" :value="displayValue" :label="label">
    <template #default="{hide}">
      <q-card>
        <q-date
            :model-value="tempModel" :range="range" :multiple="multiple" :navigation-min-year-month="minYearMonth"
            mask="YYYY-MM-DD" minimal
            @update:model-value="update($event)"
        />
        <q-card-actions align="right" class="tw-pt-0">
          <Button flat icon="close" @click="close(hide)">{{$t('field.cancel')}}</Button>
          <Button flat icon="check" @click="confirm(hide)">{{$t('field.confirm')}}</Button>
        </q-card-actions>
      </q-card>
    </template>
  </PopupTextField>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import PopupTextField from "components/General/Other/PopupTextField.vue";
import Button from "components/General/Other/Button.vue";
import {computed, nextTick, ref, toRef} from "vue";
import {syncRef} from "@vueuse/core";

export default {
  name: "DatePicker",

  components: {Button, PopupTextField, TextField},

  props: {
    // Model
    modelValue: String | Array,
    // Label
    label: String,
    // Readonly
    readonly: Boolean,
    // Range
    range: Boolean,
    // Multiple
    multiple: Boolean,
    // Min yearMonth
    minYearMonth: {
      type: String,
      default: '1950/01'
    }
  },

  emits: ['update:modelValue'],

  setup(props, {emit}) {
    // Convert modelValue to ref
    const modelValue = toRef(props, 'modelValue')
    // Temp model value
    const tempModel = ref(modelValue.value)
    // Sync modelValue and tempModel (ltr)
    syncRef(modelValue, tempModel, {direction: 'ltr'})

    // Display value
    const displayValue = computed(() => modelValue.value && typeof modelValue.value === "object"
        ? `${modelValue.value.from} ~ ${modelValue.value.to}`
        : modelValue.value)

    // Update model temporary
    const update = (value) => tempModel.value = value
    // On confirm
    const confirm = (callback) => {
      // Emit modelValue
      emit('update:modelValue', tempModel.value)
      // Hide proxy
      callback()
    }
    // On close
    const close = (callback) => {
      // Reset temp model
      tempModel.value = modelValue.value
      // Hide proxy
      callback()
    }

    return {
      tempModel: tempModel, displayValue: displayValue,
      update: update, confirm: confirm, close: close,
    }
  },
}
</script>
