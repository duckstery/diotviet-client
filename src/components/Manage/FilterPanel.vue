<template>
  <q-card class="my-card" flat bordered>
    <q-card-section horizontal class="tw-p-2">
      <div class="tw-text-base tw-font-medium text-orange-9 tw-underline tw-underline-offset-2">{{ title }}</div>
      <q-space/>
      <Button v-if="reloadable" icon="refresh" flat :tooltip="$t('field.reload')" @click="$emit('reload')"/>
      <Button :icon="expandIcon" flat :tooltip="$t('field.expand')" @click="expanded = !expanded"/>
    </q-card-section>

    <q-slide-transition :duration="300">
      <div v-show="expanded">
        <div class="tw-p-2">
          <slot/>
        </div>
      </div>
    </q-slide-transition>
  </q-card>
</template>

<script>
import Button from "components/General/Other/Button.vue";

export default {
  name: "FilterPanel",

  components: {Button},

  props: {
    title: {
      type: String,
      default: ''
    },
    reloadable: Boolean
  },

  emits: ['reload'],

  data: () => ({
    expanded: true
  }),

  computed: {
    // Expand icon
    expandIcon() {
      return this.expanded ? 'keyboard_arrow_up' : 'keyboard_arrow_down'
    }
  },
}
</script>
