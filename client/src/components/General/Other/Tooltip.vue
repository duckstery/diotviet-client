<template>
  <q-tooltip transition-show="scale" transition-hide="scale" class="tw-text-sm">
    <slot>
      {{ content }}
    </slot>
  </q-tooltip>
</template>

<script>
export default {
  name: "Tooltip",

  props: {
    content: String,
  }
}
</script>
