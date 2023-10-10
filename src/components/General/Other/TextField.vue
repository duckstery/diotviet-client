<template>
  <q-input
    :model-value="modelValue"
    v-bind="$attrs"

    dense
    color="brand"
    bg-color="brand"

    :mask="mask"
    :rules="rules"
    :label="label"
    :type="localType"
    :debounce="debounce"
    :readonly="readonly"
    :outlined="!compact"
    :clearable="!required"
    :unmasked-value="!!mask"
    :reverse-fill-mask="!!mask"
  >
    <template v-if="!compact && !!icon" #prepend>
      <q-icon :name="icon"/>
    </template>
    <template v-for="(_, slot) of $slots" #[slot]="scope">
      <slot v-if="slot !== 'prepend'" :name="slot" v-bind="scope"/>
    </template>
    <template v-if="type === 'password'" #after>
      <q-icon class="tw-ml-1 tw-cursor-pointer" :name="passwordIcon" @click="togglePassword" size="xs"/>
    </template>
  </q-input>
</template>

<script>
import {computed, ref, toRef} from "vue";
import {syncRef} from "@vueuse/core";

export default {
  name: "TextField",

  props: {
    // Value
    modelValue: String,
    // Label
    label: String,
    // Rules
    rules: Array,
    // Readonly
    readonly: Boolean,
    // Debounce
    debounce: String,
    // Icon
    icon: {
      type: String,
      default: null
    },
    // Compact mode
    compact: {
      type: Boolean,
      default: false
    },
    // Required
    required: {
      type: Boolean,
      default: false
    },
    // Mask
    mask: {
      type: String,
      default: "",
    },
    type: {
      type: String,
      default: 'text'
    }
  },

  setup(props) {
    // Local type
    const localType = ref(props.type)
    syncRef(toRef(props, 'type'), localType, {direction: 'ltr'})

    // Show password flag
    const shouldShowPassword = ref(false)
    // Show password when clicked
    const togglePassword = () => {
      // Switch flag
      shouldShowPassword.value = !shouldShowPassword.value
      localType.value = shouldShowPassword.value ? 'text' : 'password'
    }
    return {
      localType: localType,
      passwordIcon: computed(() => shouldShowPassword.value ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'),
      togglePassword: togglePassword
    }
  }
}
</script>

