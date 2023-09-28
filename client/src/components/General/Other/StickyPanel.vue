<template>
  <q-page-sticky :position="position" :offset="location">
    <q-fab
        v-if="$q.screen.lt.md" v-bind="$attrs" v-touch-pan.prevent.mouse="movePanel"
        direction="up" color="primary" vertical-actions-align="left"
        :header-class="headerClass" :icon="icon" :disable="disable"
    >
      <q-card class="tw-overflow-y-auto virtual-scrollbar"
              :style="`width: ${width}px; max-height: ${contentMaxHeight}px`">
        <q-card-section>
          <slot/>
        </q-card-section>
      </q-card>
    </q-fab>
    <q-expansion-item
        v-else v-bind="$attrs" v-touch-pan.prevent.mouse="movePanel"
        class="shadow-1 overflow-hidden" :header-class="headerClass"
        :icon="icon" :label="label" :style="`border-radius: 20px; width: ${width}px`"
    >
      <q-card class="tw-overflow-y-auto virtual-scrollbar"
              :style="`width: ${width}px; max-height: ${contentMaxHeight}px`">
        <q-card-section>
          <slot/>
        </q-card-section>
      </q-card>
    </q-expansion-item>
  </q-page-sticky>
</template>

<script>
import {computed, ref, toRefs} from "vue";
import {syncRef} from "@vueuse/core";

export default {
  name: "StickyPanel",

  props: {
    // Model value
    modelValue: Boolean,
    // Component's width
    width: {
      type: Number,
      default: 200
    },
    // Content's height
    contentMaxHeight: {
      type: Number,
      default: 400
    },
    // Header class
    headerClass: String,
    // Panel class
    panelClass: String,
    // Position
    position: {
      type: String,
      default: 'bottom-left'
    },
    // Offset
    offset: {
      type: Array,
      default: () => ([20, 20])
    },
    // Header's icon
    icon: String,
    // Header's label
    label: String,
    // Disable
    disable: Boolean,
    // Draggable
    draggable: Boolean,
  },

  emits: ['update:modelValue'],

  setup(props) {
    // Props to ref
    const {headerClass, disable, offset, position, draggable} = toRefs(props)

    // Local disable
    const localDisable = ref(props.disable)
    // Sync props.disable
    syncRef(disable, localDisable, {direction: "ltr"})


    // Since offset will sync with position, we need to calculate the actual direction that component should be dragged to
    // For ex, if position is 'top' and user drag direction is down, component will move up
    const direction = computed(() => [
      // Vertical movement direction
      position.value && position.value.includes('left') ? 1 : -1,
      // Horizontal movement direction
      position.value && position.value.includes('top') ? 1 : -1
    ])
    // Panel's absolute location
    const location = ref(props.offset)
    // Sync props.offset
    syncRef(offset, location, {direction: "ltr"})
    // On move panel
    const movePanel = (ev) => {
      if (draggable.value) {
        // Temporary disable panel
        localDisable.value = ev.isFirst !== true && ev.isFinal !== true
        // Set location
        location.value = [
          location.value[0] + (ev.delta.x * direction.value[0]),
          location.value[1] + (ev.delta.y * direction.value[1])
        ]
      }
    }

    return {
      // Disable panel
      disable: localDisable,
      // Panel absolute location
      location: location,

      // Computed
      headerClass: computed(() => 'bg-primary text-white' + ` ${headerClass}`),
      // Methods
      movePanel: movePanel
    }
  }
}
</script>
