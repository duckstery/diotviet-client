<template>
  <div :class="{'tw-flex': !horizontal, 'tw-mb-5': isUsingVuelidate}">
    <LabelField :src="src" :label="label" class="tw-pt-2"/>
    <slot name="before"/>
    <q-space v-if="space"/>
    <slot :class="classObject" v-bind="vuelidateUsageProp"/>
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
        'error': this.vuelidate.$error,
        'error-message': this.getFirstErrorMessage(),
      }
    },
  },

  methods: {
    /**
     * Get the first error message
     *
     * @return {string|null}
     */
    getFirstErrorMessage() {
      if (this.$util.isUnset(this.vuelidate.$errors) || this.vuelidate.$errors.length === 0) {
        return null
      }

      // Get error type and property
      const type = this.vuelidate.$errors[0].$validator
      const property = this.vuelidate.$errors[0].$property

      return this.$t(
        `message.${this.$util.camelToSnake(type)}`,
        {attr: this.getAttributeString(property), ...this.vuelidate.$errors[0].$params}
      )
    },

    /**
     * Get attribute string
     *
     * @param attr
     */
    getAttributeString(attr) {
      return this.$t(`field.${this.$util.camelToSnake(attr)}`)?.toLowerCase()
    }
  },
}
</script>
