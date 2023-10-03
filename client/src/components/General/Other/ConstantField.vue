<template>
  <span v-bind="$attrs">
    <q-icon :name="info.icon" :color="info.color" class="tw-mr-1" size="xs">
      <Tooltip v-if="short" :content="info.name"/>
    </q-icon>
    <template v-if="!short">
      {{ info.name }}
    </template>
  </span>
</template>

<script>
import Tooltip from "components/General/Other/Tooltip.vue";

export default {
  name: 'ConstantField',

  components: {Tooltip},

  props: {
    // Value
    value: Boolean,
    // Short mode
    short: Boolean,
    // Target
    target: String,
  },

  computed: {
    // Constants
    constants() {
      if (this.target === 'isDeactivated') {
        return this.$constant.staffStatuses()
      } else if (this.target === 'type') {
        return this.$constant.transactionTypes()
      } else if (this.target === 'status') {
        return this.$constant.statuses()
      } else if (this.target === 'isMale' || this.target === 'gender') {
        return this.$constant.genders()
      }

      return this.$constant.booleans()
    },
    // Info
    info() {
      // Get key
      const key = typeof this.value === 'boolean' ? (this.value ? 1 : 0) : this.value

      return this.constants[key]
    }
  }
}
</script>
