<template>
  <TextField
    readonly class="tw-cursor-pointer" input-class="tw-cursor-pointer"
    :model-value="value" :label="label" :rules="rules" :debounce="debounce"
    :icon="icon" :compact="compact" :require="required" :mask="mask"
  >
    <template #append>
      <q-icon :name="popupIcon"/>
    </template>
    <template #default>
      <Tooltip :content="value"/>
      <q-popup-proxy
        ref="proxy" class="virtual-scrollbar" transition-show="scale" transition-hide="scale"
        :cover="cover" :anchor="anchor" :self="self" :offset="offset"
      >
        <slot :hide="hide"/>
      </q-popup-proxy>
    </template>
  </TextField>
</template>

<script>
import TextField from "components/General/Other/TextField.vue";
import Tooltip from "components/General/Other/Tooltip.vue";

export default {
  name: "PopupTextField",

  components: {Tooltip, TextField},

  props: {
    // Value
    value: String,
    // Label
    label: String,
    // Rules
    rules: Array,
    // Readonly
    readonly: Boolean,
    // Debounce
    debounce: String,
    // Cover mode
    cover: Boolean,
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
    // Popup icon
    popupIcon: {
      type: String,
      default: "unfold_more",
    },
    // Popup anchor
    anchor: {
      type: String,
      default: "top end"
    },
    // Popup anchor
    self: {
      type: String,
      default: "top start"
    },
    // Popup anchor
    offset: {
      type: Array,
      default: [20, 5]
    }
  },

  emits: ['update:model-value'],

  methods: {
    // Hide proxy
    hide() {
      this.$refs.proxy.hide()
    }
  }
}
</script>

