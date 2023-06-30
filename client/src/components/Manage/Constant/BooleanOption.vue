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
import DisplayField from "components/General/Other/DisplayField.vue";
import Button from "components/General/Other/Button.vue";
import Skeleton from "components/General/Other/Skeleton.vue";
import Tooltip from "components/General/Other/Tooltip.vue";

export default {
  name: 'BooleanOption',

  components: {Tooltip, Skeleton, Button, DisplayField},

  props: {
    // Value
    value: Boolean,
    // Short mode
    short: Boolean,
  },

  computed: {
    info() {
      return this.$constant.booleans()[this.value ? 1 : 0]
    }
  }
}
</script>
