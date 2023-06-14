<template>
  <TextField :model-value="modelValue" mask="date" :label="label" readonly>
    <template v-slot:append>
      <q-icon name="event" class="cursor-pointer">
        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
          <q-date
            :model-value="modelValue" @update:model-value="$emit('update:model-value', $event)"
            mask="YYYY-MM-DD"
            navigation-min-year-month="2000/01"
            minimal
          />
        </q-popup-proxy>
      </q-icon>
    </template>
  </TextField>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";

export default {
  name: "DatePicker",

  components: {TextField},

  props: {
    // Model
    modelValue: String,
    // Label
    label: String,
  },

  emits: ['update:model-value']
}
</script>
