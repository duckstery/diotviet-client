<template>
  <div class="tw-text-lg">
    <IconMage :src="src" size="20px"/>
    <span class="tw-ml-2 tw-underline tw-underline-offset-4">{{ label }}:</span>
  </div>
</template>

<script>
import IconMage from "components/General/Other/IconMage.vue";

export default {
  name: "LabelField",

  components: {IconMage},

  props: {
    // IconMage src
    src: String,
    // Label
    label: String,
  }
}
</script>
