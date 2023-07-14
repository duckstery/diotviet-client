<template>
  <Button
    color="info"
    class="tw-p-2"
    icon="receipt"
    :label="$t('field.order')"
    :tooltip="$t('field.order')"
    @click="onProcess"
  />
</template>

<script>
import {useOrderProcessor} from "src/composables/useOrderProcessor";

import Button from "components/General/Other/Button.vue";

export default {
  name: 'OrderProcessor',

  components: {Button},

  setup() {
    // Get Order processor invoker
    const invoker = useOrderProcessor()

    return {
      onProcess: () => {
        invoker()
          .onOk(() => {})
          .onCancel(() => {})
          .onDismiss(() => {})
      }
    }
  }
}
</script>
