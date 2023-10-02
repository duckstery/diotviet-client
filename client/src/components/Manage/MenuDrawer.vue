<template>
  <q-drawer
    :model-value="modelValue"
    :breakpoint="$q.screen.sizes.md - 1"
    :width="200"

    overlay
    bordered
    side="left"

    @update:model-value="$emit('update:modelValue', $event)"
  >
    <q-scroll-area class="fit" :horizontal-thumb-style="{ opacity: 0 }">
      <q-list padding>
        <q-item v-for="link in links" :to="link.to" clickable v-ripple @click="$emit('update:modelValue', false)">
          <q-item-section avatar>
            <q-icon :name="`fa-solid ${link.icon}`"/>
          </q-item-section>

          <q-item-section>
            {{ $t(`field.${link.key}`) }}
          </q-item-section>
        </q-item>
      </q-list>
    </q-scroll-area>
  </q-drawer>
</template>

<script>
export default {
  name: "MenuDrawer",

  props: {
    modelValue: Boolean,
    links: Array,
  },

  emits: ['update:modelValue']
}
</script>
