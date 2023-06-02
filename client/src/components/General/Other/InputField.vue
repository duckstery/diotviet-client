<template>
  <div :class="{'tw-flex': !horizontal, 'tw-mb-5': isUsingVuelidate}">
    <LabelField :src="src" :label="label" class="tw-pt-2"/>
    <slot name="before"/>
    <q-space v-if="space"/>
    <slot :class="classObject" v-bind="vuelidateUsageProp" @update:modelValue="modelProxy"/>
  </div>
</template>

<script>
import IconMage from "components/General/Other/IconMage.vue";
import TextField from "components/General/Other/TextField.vue";
import LabelField from "components/General/Other/LabelField.vue";

export default {
  name: "InputField",

  components: {LabelField, TextField, IconMage},

  props: {
    // Put space between label and content
    space: Boolean,
    // IconMage src
    src: String,
    // Label
    label: String,
    // Display horizontally
    horizontal: Boolean,
    // Vuelidate property
    vuelidate: Object
  },

  computed: {
    // Class object
    classObject() {
      return {
        'tw-p-0': !this.horizontal,
        'tw-float-right': !this.horizontal,
        'tw-ml-3': !this.space && !this.horizontal,
        'tw-flex-grow': !this.space && !this.horizontal,
        'tw-mt-3': this.horizontal,
        'tw-w-full': this.horizontal
      }
    },

    // Checking if using Vuelidate
    isUsingVuelidate() {
      return !this.$util.isUnset(this.vuelidate)
    },

    // Check if using vuelidate
    vuelidateUsageProp() {
      return this.isUsingVuelidate && {
        'bottom-slots': true,
        'error': this.vuelidate.$error || this.vuelidate.$server?.$error,
        'error-message': this.getFirstErrorMessage(),
      }
    },
  },

  methods: {
    /**
     * Get the first error message
     *
     * @return {string}
     */
    getFirstErrorMessage() {
      let errors = [];

      // Check if field has no client error
      if (this.$util.isUnset(this.vuelidate.$errors) || this.vuelidate.$errors.length === 0) {
        // Check if field has no server error
        if (this.$util.isUnset(this.vuelidate.$server)) {
          return null
        } else {
          errors = this.vuelidate.$server.$errors
        }
      } else {
        errors = this.vuelidate.$errors
      }

      // Get error type and property
      const type = errors[0].$validator

      return type === 'server'
      ? errors[0].$message
      : this.$t(
        `message.${this.$util.camelToSnake(type)}`,
        {...errors[0].$params}
      )
    },

    /**
     * Proxy @update:modelValue event to clear $external error
     */
    modelProxy() {
      if (!this.$util.isUnset(this.vuelidate.$server)) {
        this.vuelidate.$server = null
      }
    }
  },
}
</script>
